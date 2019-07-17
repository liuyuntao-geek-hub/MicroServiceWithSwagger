from datetime import datetime
import time
import json
import pandas as pd
import numpy as np
import scipy.sparse as sp
from pandas.io.json import json_normalize
import traceback,sys
import sqlite3
from xgboost import XGBClassifier

import hbase
import response
import constants


def flat_dict_values(dict):
    dict_keys = [*dict.values()]
    dict_keys = [item for sublist in dict_keys for item in sublist]
    return dict_keys

header_dict = {'ICD': ['ICD_A_CD', 'ICD_B_CD', 'ICD_C_CD', 'ICD_D_CD', 'ICD_E_CD','ICD_OTHR_1_CD','ICD_OTHR_2_CD','ICD_OTHR_3_CD','ICD_OTHR_4_CD','ICD_OTHR_5_CD'],
               'ATCHMNT_IND': ['ATCHMNT_IND'],
               'EDI_CLM_FLNG_IND': ['EDI_CLM_FLNG_IND'],
               'PROV_IND': ['PROV_IND'],
               'PROV_ZIP_5_CD': ['PROV_ZIP_5_CD'],
               'PROV_ZIP_4_CD': ['PROV_ZIP_4_CD'],
               'PRVDR_STATUS': ['PRVDR_STATUS'],
               'PROD_NTWK' :['PROD_NTWK'],
               'SBSCRBR_ZIP_5_CD': ['SBSCRBR_ZIP_5_CD'],
               'SBSCRBR_ZIP_4_CD': ['SBSCRBR_ZIP_4_CD'],
               'CLM_TYPE_CD': ['CLM_TYPE_CD'],
               'MEMBER_SSN': ['memID'],
               'PROV_TAX_ID': ['PROV_TAX_ID'],
               'MK_FUND_TYPE_CD': ['MK_FUND_TYPE_CD'],
               'CLAIM_TYPE':['CLAIM_TYPE'],
               'MBU_CD': ['MBU_CD'],
               'FULL_MNGMNT_BU_CD': ['FULL_MNGMNT_BU_CD'],
               'SRVC_TYPE_CURNT_CD': ['SRVC_TYPE_CURNT_CD'],
               'SRVC_TYPE_ORGNL_CD': ['SRVC_TYPE_ORGNL_CD'],
               'TAX_LIC_SPLTYCDE': ['TAX_LIC_SPLTYCDE'],
               'TYPE_OF_BILL_CD': ['TYPE_OF_BILL_CD'],
               'CLM_FILE_COB_IND': ['CLM_FILE_COB_IND'],
               'BILLG_TXNMY_CD': ['BILLG_TXNMY_CD'],
               'RNDRG_TXNMY_CD': ['RNDRG_TXNMY_CD'],
               'RNDRG_TAX_ID': ['RNDRG_TAX_ID']}
header_num_cols = ['TOTL_CHRG_AMT', 'COB_BSIC_PAYMNT_AMT', 'COB_MM_PAYMNT_AMT', 'COB_SGMNT_CNT', 'MEDCR_CNT',
                   'PAT_AGE_NBR']
header_keep_cols = ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'CLM_PAYMNT_ACTN_1_CD', 'CLM_PAYMNT_ACTN_2_6_CD'] + \
                   flat_dict_values(header_dict) + header_num_cols
detail_dict = {'HCPCS_MDFR_CD': ['HCPCS_MDFR_CD'],
               'MODIFIER_CD': ['PROC_MDFR_CD', 'MDFR_1_CD', 'MDFR_2_CD', 'MDFR_3_CD'],
               'PROC_CD': ['PROC_CD'],
               'HCPCS_CD': ['HCPCS_CD'],
               'MBR_CNTRCT_TYPE_CD': ['MBR_CNTRCT_TYPE_CD'],
               'POT_CD': ['POT_CD'],
               'TRTMNT_TYPE_CD': ['TRTMNT_TYPE_CD'],
               'MBR_CNTRCT_CVRG_CD': ['MBR_CNTRCT_CVRG_CD'],
               'MBR_CNTRCT_CD': ['MBR_CNTRCT_CD'],
               'PN_CD': ['PN_CD'],
               'TOS_TYPE_CD': ['TOS_TYPE_CD']}
detail_num_cols = ['DTL_LINE_NBR', 'BILLD_CHRGD_AMT', 'MEDCRB_APRVD_AMT', 'MEDCRB_PAID_AMT', 'MEDCRB_COINSRN_AMT',
                   'MEDCRB_DDCTBL_AMT']
detail_date_cols = ['BNFT_YEAR_CNTRCT_EFCTV_DT', 'BNFT_YEAR_CNTRCT_REV_DT', 'MBR_CNTRCT_EFCTV_DT', 'MBR_CNTRCT_END_DT',
                    'SRVC_FROM_DT_DTL', 'SRVC_TO_DT_DTL']
edit_dict = {'ERR_CD': ['ERR_1_CD', 'ERR_2_CD', 'ERR_3_CD', 'ERR_4_CD', 'ERR_5_CD', 'ERR_6_CD', 'ERR_7_CD', 'ERR_8_CD',
                        'ERR_9_CD', 'ERR_10_CD', 'ERR_11_CD', 'ERR_12_CD', 'ERR_13_CD']}
edit_keep_cols = ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR'] + flat_dict_values(edit_dict)

detail_keep_cols = ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR'] + flat_dict_values(
    detail_dict) + detail_num_cols + detail_date_cols


def run(app, logger, extra, payload, payload_req):
    field_mapping = {}
    result_row = {}
    test_row = {}  # return value
    field_mapping['MEDCRB_APPV_EQU_PAID'] = 0.0
    field_mapping['MEDCRB_TWO_ZERO'] = 0.0
    try: 
        for key in payload.keys():
            if key == 'hist_header' or key == 'hist_details':
                continue
            content = payload[key]

            if isinstance(content, dict):
                for k, v in content.items():
                    if v is None:
                        continue
                    #print("k : " + str(k) + " " + str(type(k)) + "  v : " + str(v) + " " + str(type(v)))
                    field_mapping[k.strip()] = v
                    # logger.debug(k.strip(), extra=extra)
                    field_mapping[k.strip() + '_' + str(v).strip()] = 1.0
                    # logger.debug(k.strip() + '_' + str(v).strip(), extra=extra)
                if key == 'header':
                    field_mapping = __process_raw_head__(field_mapping)
                    test_row['KEY_CHK_DCN_NBR'] = field_mapping['KEY_CHK_DCN_NBR']
                    test_row['CURNT_QUE_UNIT_NBR'] = field_mapping['CURNT_QUE_UNIT_NBR']
            elif isinstance(content, list):
                for elem in content:
                    if isinstance(elem, dict):
                        for k, v in elem.items():
                            if v is None:
                                continue
                            field_mapping[k.strip()] = v
                            # logger.debug(k.strip())  
                            field_mapping[k.strip() + '_' + str(v).strip()] = 1.0
                            # logger.debug(k.strip() + '_' + v.strip())
                        if key == 'edits':
                            field_mapping = __process_raw_edits__(field_mapping)
                        elif key == 'details':
                            field_mapping = __process_detail_cols__(field_mapping)
                            #print(field_mapping)

        populate_hist(payload, payload_req, logger, extra)
        start_time = time.time()
        values = []
        cols = []
        dup_col_list = app.config['model_encodings']['dup_col_list']
        for idx, value in enumerate(dup_col_list):
            if field_mapping.get(value) is not None:
                # logger.debug(value)
                #print(value + ' -'  + str(idx-1) + ' - ' + str(float(field_mapping.get(value))))
                values.append(float(field_mapping.get(value)))
                cols.append(idx-1)
        logger.debug(values, extra=extra)
        logger.debug(cols, extra=extra)
        X = sp.csr_matrix((values, ([0]*len(values), cols)))     
        col_dict = pd.DataFrame(list(zip(dup_col_list, range(len(dup_col_list)))), columns=['VAR', 'col'])
        row_dict = pd.DataFrame(list(zip([payload['header'].get('KEY_CHK_DCN_NBR')], [payload['header'].get('CURNT_QUE_UNIT_NBR')], [0])), columns=['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'row'])
        usecases = app.config['usecases']
        models = []
        for usecase in usecases:
            if (usecase['name'] == 'dup'):
                models = usecase['models']
                break
        dup_model = None
        corrected_billing = None
        for model in models:
            if model['model'] == 'dup_model':
                dup_model = model['binary']
            else:
                corrected_billing = model['binary']

        histories = payload.get('hist', [])
        hist_headers = []
        hist_details = []
        for history in histories:
            hist_headers.append(history.get('hist_header', []))
            hist_details.extend(history.get('hist_details', []))

        if len(hist_headers) == 0 or len(hist_details) == 0:
            logger.error("hist_header is empty for payload : "+str(payload), extra=extra)
            return response.generate_desc(711,'dup',result_row, payload_req)

        current_header = json_normalize(payload['header'])
        current_detail = json_normalize(payload['details'])
        history_header = json_normalize(hist_headers)
        history_detail = json_normalize(hist_details)

        logger.debug('Elapsed time',extra=response.extra_tags(extra, {'Prepare_mapping_data': time.time() - start_time}, 0))


        dup_top3, dup_merged, X, Y, col = DUP_deploy(current_header, current_detail, json_normalize(payload['edits']), history_header, history_detail, X, col_dict, row_dict, dup_model,logger,extra)

        start_time = time.time()
        model = corrected_billing
        # m = X.shape[0]
        '''
        arry = X.toarray()
        print('################################')
        for idx, val in enumerate(arry[0]):
            if val != 0:
                print('(0,' + str(idx) + ')\t' + str(val))
        print('*******************************')
        '''
        # print(m)
        # X_2 = X[np.array(range(0,m)),:]
        # print(X_2)
        m = X.shape[0]
        X_2 = X[np.array(range(0,m)),:]
        pred_train = model.predict_proba(X_2)
        df_train = pd.DataFrame(pred_train, columns=model.classes_)

        logger.debug('Elapsed time',extra=response.extra_tags(extra, {'BILLING_model_execution': time.time() - start_time}, 0))
        start_time = time.time()
        Y = pd.concat([Y, df_train], axis=1, join='inner')
        Y['Top 1'] = Y[['Corrected Billing Reject', 'Dup Reject', 'Not Dup or Corrected Billing']].idxmax(axis=1)
        Y['Top 1 Score'] = Y[['Corrected Billing Reject', 'Dup Reject', 'Not Dup or Corrected Billing']].max(axis=1)
        Y.KEY_CHK_DCN_NBR = Y.KEY_CHK_DCN_NBR.apply(str)
        dup_top3.KEY_CHK_DCN_NBR_CRNT = dup_top3.KEY_CHK_DCN_NBR_CRNT.apply(str)
        row_data = Y.merge(dup_top3, how='left', left_on='KEY_CHK_DCN_NBR', right_on='KEY_CHK_DCN_NBR_CRNT')
        row_data = row_data.drop(columns=['KEY_CHK_DCN_NBR_CRNT'])
        row_data['Top_1_DUP_ID'] = row_data.Top_1_DUP.str.split('|', expand=True).iloc[:, 0]
        row_data['Result'] = 'Bypass Dup Edits'
        row_data.loc[row_data['Top 1'] == 'Dup Reject', 'Result'] = 'Reject for Dup'
        row_data.loc[row_data['Top 1'] == 'Corrected Billing Reject', 'Result'] = 'Reject for Corrected Billing'
        row_data = row_data.fillna('*')

        # row_data.to_csv(final_output_file, index=False)
        row_data = row_data[row_data['Top 1 Score'] >= constants.DUP_THRESHOLD]
        if row_data.empty:
            logger.debug('Elapsed time',extra=response.extra_tags(extra, {'result_row building': time.time() - start_time}, 0))
            return response.generate_desc(710,"dup",result_row, payload_req)
        row_data['recommendation'] = 'Dup/Corr.Billing.Recommendation:{' + row_data.Result + '}Original Claim:' + row_data[
            'Top_1_DUP_ID']
        row_data = row_data.loc[
            ~row_data['recommendation'].str.contains('{Bypass', regex=False), ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'recommendation',
                                                                    'Top 1 Score']].reset_index(drop=True)
        
        if row_data.empty:
            logger.debug('Elapsed time',extra=response.extra_tags(extra, {'result_row building': time.time() - start_time}, 0))
            return response.generate_desc(709,"dup",result_row,payload_req)

        row_data = row_data.loc[row_data.KEY_CHK_DCN_NBR.isin(dup_top3.KEY_CHK_DCN_NBR_CRNT), :].reset_index(drop=True)
        row_data = row_data.loc[row_data.KEY_CHK_DCN_NBR.isin(dup_top3.KEY_CHK_DCN_NBR_CRNT), :].reset_index(drop=True)
        recommendation = row_data.to_json(orient='records')
        recommendation = json.loads(recommendation)
        if isinstance(recommendation, dict):
            recommendation['modelName'] = 'DUP'
            recommendation['lineNumber'] = '00'
            recommendation['currentQueue'] = payload_req.get('CURNT_QUE_UNIT_NBR', None)
        elif isinstance(recommendation, list):
            for rec in recommendation:
                rec['modelName'] = 'DUP'
                rec['lineNumber'] = '00'
                rec['currentQueue'] = payload_req.get('CURNT_QUE_UNIT_NBR', None)
        # translate(recommendation)
        result_row['recommendations'] = translate(recommendation)
        response.generate_desc(700, 'dup', result_row,payload_req)
        if len(recommendation) > 0:
            dup_top3['KEY_CHK_DCN_NBR_HIST'] = dup_top3.Top_1_DUP.str.split('|', expand=True).iloc[:, 0]
            dup_top3_copy = dup_top3.loc[dup_top3.KEY_CHK_DCN_NBR_CRNT.isin(row_data.KEY_CHK_DCN_NBR), ['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST']]
            current_header.TOTL_CHRG_AMT = pd.to_numeric(current_header.TOTL_CHRG_AMT, downcast='float', errors='coerce').map(
                '${:,.2f}'.format)
            history_header.TOTL_CHRG_AMT = pd.to_numeric(history_header.TOTL_CHRG_AMT, downcast='float', errors='coerce').map(
                '${:,.2f}'.format)
            current_detail.BILLD_CHRGD_AMT = pd.to_numeric(current_detail.BILLD_CHRGD_AMT, downcast='float', errors='coerce').map(
                '${:,.2f}'.format)
            history_detail.BILLD_CHRGD_AMT = pd.to_numeric(history_detail.BILLD_CHRGD_AMT, downcast='float', errors='coerce').map(
                '${:,.2f}'.format)
            current_detail.ELGBL_EXPNS_AMT = pd.to_numeric(current_detail.ELGBL_EXPNS_AMT, downcast='float', errors='coerce').map(
                '${:,.2f}'.format)
            history_detail.ELGBL_EXPNS_AMT = pd.to_numeric(history_detail.ELGBL_EXPNS_AMT, downcast='float', errors='coerce').map(
                '${:,.2f}'.format)
            current_header['CLM_PAYMNT_ACTN_12'] = current_header['CLM_PAYMNT_ACTN_1_CD'] + current_header['CLM_PAYMNT_ACTN_2_6_CD']
            history_header['CLM_PAYMNT_ACTN_12'] = history_header['CLM_PAYMNT_ACTN_1_CD'] + history_header['CLM_PAYMNT_ACTN_2_6_CD']
            current_header['MRN_NBR_12'] = current_header['MRN_NBR'] + current_header['MDCL_RCRD_2_NBR']
            history_header['MRN_NBR_12'] = history_header['MRN_NBR'] + history_header['MDCL_RCRD_2_NBR']
            current_detail['SRVC_FROM_DT_DTL'] = current_detail['SRVC_FROM_DT_DTL'].astype(str)
            current_detail['SRVC_TO_DT_DTL'] = current_detail['SRVC_TO_DT_DTL'].astype(str)
            history_detail['SRVC_FROM_DT_DTL'] = history_detail['SRVC_FROM_DT_DTL'].astype(str)
            history_detail['SRVC_TO_DT_DTL'] = history_detail['SRVC_TO_DT_DTL'].astype(str)
            dup_top3_copy['Header_compare'] = dup_top3_copy.apply(__subset_header__, args=(current_header, history_header,), axis=1)
            dup_top3_copy['Detail_current'] = dup_top3_copy['KEY_CHK_DCN_NBR_CRNT'].apply(__subset_detail__, args=(current_detail,))
            dup_top3_copy['Detail_history'] = dup_top3_copy['KEY_CHK_DCN_NBR_HIST'].apply(__subset_detail__, args=(history_detail,))
            row_data_different = row_data.merge(dup_top3_copy, how='inner', left_on='KEY_CHK_DCN_NBR',
                                                right_on='KEY_CHK_DCN_NBR_CRNT').drop(
                columns=['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST'])
            row_data_different = row_data_different.fillna('')
            row_data_different = row_data_different.loc[~row_data_different['recommendation'].str.contains('{Bypass', regex=False),
                                :].reset_index(drop=True)
            row_data_different['Result_title'] = "Result"
            row_data_different['Header_compare_title'] = "Claim Header Compare"
            row_data_different['Detail_current_title'] = "Current Claim Detail"
            row_data_different['Detail_history_title'] = "Historic Claim Detail"
            row_data_different = row_data_different.loc[:,
                                ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'Result_title', 'Header_compare_title',
                                'Detail_current_title', 'Detail_history_title', 'recommendation', 'Header_compare', 'Detail_current',
                                'Detail_history']]
            model_insights = []
            model_insight = {}
            model_insight['modelName'] = 'dup'
            model_insight['modelInsight'] =  row_data_different.to_json()
            model_insights.append(model_insight)
            result_row['modelInsights'] = model_insights
            logger.debug('Elapsed time',extra=response.extra_tags(extra, {'result_row building': time.time() - start_time}, 0))
        return result_row
    except:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=extra)
        return response.generate_desc(903, 'dup',result_row,payload_req)


def translate(recommendation):
    translate_recommendation = []
    
    if isinstance(recommendation, dict):
        recommendation = [recommendation]
    
    for rec in recommendation:
        t = {}
        t['modelName'] = rec['modelName']
        t['description'] = rec['recommendation']
        t['lineNumber'] = rec['lineNumber']
        t['actionCode'] = 'RJT'
        t['actionValue'] = 'RDUP00'
        t['modelScore'] = rec['Top 1 Score']
        t['currentQueue'] = rec['currentQueue']
        translate_recommendation.append(t)
    return translate_recommendation


def __subset_detail__(x, df_detail):
    detail_compare_cols = ['KEY_CHK_DCN_NBR', 'DTL_LINE_NBR', 'BILLD_CHRGD_AMT', 'BILLD_SRVC_UNIT_QTY', 'PROC_CD',
                           'HCPCS_CD', 'SRVC_FROM_DT_DTL', 'SRVC_TO_DT_DTL', 'PROC_MDFR_CD', 'HCPCS_MDFR_CD',
                           'MDFR_1_CD', 'MDFR_2_CD', 'MDFR_3_CD', 'HCFA_PT_CD', 'ELGBL_EXPNS_AMT']
    df_detail_sub = df_detail.loc[df_detail.KEY_CHK_DCN_NBR == x, detail_compare_cols].sort_values(['DTL_LINE_NBR'])
    df_detail_sub.columns = ['DCN Number', 'Line #', 'Billed Charged Amount', 'Units', 'Procedure Code', 'HCPCS Code',
                             'From Date', 'To Date', 'Procedure Modifier Code', 'HCPCS Modifier Code',
                             'Modifier Code 1', 'Modifier Code 2', 'Modifier Code 3', 'place of treatment',
                             'Allowed amount']
    return df_detail_sub

def __subset_header__(x, current_header, history_header):
    current_header_sub = current_header[current_header.KEY_CHK_DCN_NBR == x.KEY_CHK_DCN_NBR_CRNT]
    history_header_sub = history_header[history_header.KEY_CHK_DCN_NBR == x.KEY_CHK_DCN_NBR_HIST]
    header_compare_cols_eng = ['DCN Number', 'Member Code', 'Medical Record Number',
                                'Claim Action Code', 'Total Charge Amount', 'Provider TaxID',
                                'Provider Name', 'Provider Second Name', 'Provider Status', 'Frequency Code',
                                'Claim Type', 'Group Number', 'ICD Code A', 'ICD Code B', 'ICD Code C', 'ICD Code D',
                                'ICD Code E']
    header_compare_cols = ['KEY_CHK_DCN_NBR', 'PAT_MBR_CD', 'MRN_NBR_12', 'CLM_PAYMNT_ACTN_12',
                           'TOTL_CHRG_AMT', 'PROV_TAX_ID', 'PROV_NM', 'PROV_SCNDRY_NM',
                           'PRVDR_STATUS', 'TYPE_OF_BILL_CD', 'CLAIM_TYPE', 'GRP_NBR', 'ICD_A_CD', 'ICD_B_CD',
                           'ICD_C_CD', 'ICD_D_CD', 'ICD_E_CD']
    header_compare = {'Field': header_compare_cols_eng,
                      'Current': np.array(current_header_sub[header_compare_cols])[0],
                      'History': np.array(history_header_sub[header_compare_cols])[0]}
    header_compare = pd.DataFrame(header_compare)
    return header_compare


def __process_raw_head__(field_mapping):
    for icd_cd in header_dict['ICD']:
        # default to * if key is not found
        mapping = field_mapping.get(icd_cd, '*')
        if mapping != '*':
            field_mapping['ICD_' + field_mapping[icd_cd].strip()] = 1.0
    return field_mapping


def __process_raw_edits__(field_mapping):
    for err_cd in edit_dict['ERR_CD']:
        mapping = field_mapping.get(err_cd, '*')
        if mapping != '*':
            field_mapping['ERR_CD_' + field_mapping[err_cd].strip()] = 1.0
    return field_mapping


def __process_detail_cols__(field_mapping):
    if (float(field_mapping.get('MEDCRB_APRVD_AMT', 0)) > 0 and 
        (float(field_mapping.get('MEDCRB_APRVD_AMT',  0)) == float(field_mapping.get('MEDCRB_PAID_AMT',  0))) ) and \
        field_mapping['MEDCRB_APPV_EQU_PAID'] == 0.0:        
        field_mapping['MEDCRB_APPV_EQU_PAID'] = 1.0
    if (float(field_mapping.get('MEDCRB_COINSRN_AMT', 0)) + float(field_mapping.get('MEDCRB_DDCTBL_AMT', 0))) == 0 \
        and field_mapping['MEDCRB_TWO_ZERO'] == 0.0:
        field_mapping['MEDCRB_TWO_ZERO'] = 1.0
    for mod_cd in detail_dict['MODIFIER_CD']:
        mapping = field_mapping.get(mod_cd, '*')
        if mapping != '*':
            field_mapping['MODIFIER_CD_' + field_mapping[mod_cd]] = 1.0
    for col in detail_date_cols:
        if field_mapping[col] == '12/31/9999': 
            field_mapping[col] = '12/31/2049'
        if field_mapping[col] == '9999-12-31':
            field_mapping[col] = '2049-12-31'
        if '/' in field_mapping[col]:
            field_mapping[col] = pd.to_datetime(field_mapping[col], format= '%m/%d/%Y')
        elif '-' in field_mapping[col]:
            field_mapping[col] = pd.to_datetime(field_mapping[col], format= '%Y-%m-%d')
        # print('Finished processing col', col)

    if field_mapping.get('SRVC_OUT_BNFT', 0) == 0:
        field_mapping['SRVC_OUT_BNFT'] = 1 * ((field_mapping['SRVC_FROM_DT_DTL'] >= field_mapping['BNFT_YEAR_CNTRCT_REV_DT']) | (
        field_mapping['SRVC_TO_DT_DTL'] <= field_mapping['BNFT_YEAR_CNTRCT_EFCTV_DT']))

    field_mapping['SRVC_WITHIN_BNFT'] = 1 * ((field_mapping['SRVC_FROM_DT_DTL'] >= field_mapping['BNFT_YEAR_CNTRCT_EFCTV_DT']) & (
            field_mapping['SRVC_TO_DT_DTL'] <= field_mapping['BNFT_YEAR_CNTRCT_REV_DT']))

    field_mapping['SRVC_WITHIN_MBR'] = 1 * ((field_mapping['SRVC_FROM_DT_DTL'] >= field_mapping['MBR_CNTRCT_EFCTV_DT']) & (
            field_mapping['SRVC_TO_DT_DTL'] <= field_mapping['MBR_CNTRCT_END_DT']))

    if field_mapping.get('SRVC_OVERLAP_BNFT',0) == 0:
        field_mapping['SRVC_OVERLAP_BNFT'] = 1 * ((field_mapping['SRVC_FROM_DT_DTL'] <= field_mapping['BNFT_YEAR_CNTRCT_REV_DT']) & (
            field_mapping['SRVC_TO_DT_DTL'] >= field_mapping['BNFT_YEAR_CNTRCT_EFCTV_DT']))
        field_mapping['SRVC_OVERLAP_BNFT'] = field_mapping['SRVC_OVERLAP_BNFT'] - field_mapping['SRVC_WITHIN_BNFT']
    
    if field_mapping.get('SRVC_OUT_MBR', 0) == 0:
        field_mapping['SRVC_OUT_MBR'] = 1 * ((field_mapping['SRVC_FROM_DT_DTL'] >= field_mapping['MBR_CNTRCT_END_DT']) | (
            field_mapping['SRVC_TO_DT_DTL'] <= field_mapping['MBR_CNTRCT_EFCTV_DT']))
    
    if field_mapping.get('SRVC_OVERLAP_MBR', 0) == 0:
        field_mapping['SRVC_OVERLAP_MBR'] = 1 * ((field_mapping['SRVC_FROM_DT_DTL'] <= field_mapping['MBR_CNTRCT_END_DT']) & (
            field_mapping['SRVC_TO_DT_DTL'] >= field_mapping['MBR_CNTRCT_EFCTV_DT']))
        field_mapping['SRVC_OVERLAP_MBR'] = field_mapping['SRVC_OVERLAP_MBR'] - field_mapping['SRVC_WITHIN_MBR']
 
    '''
    field_mapping = field_mapping.groupby(['row'], as_index=False).agg(
        {'SRVC_OUT_BNFT': np.max, 'SRVC_OVERLAP_BNFT': np.max, 'SRVC_OUT_MBR': np.max, 'SRVC_OVERLAP_MBR': np.max})
    '''
    return field_mapping
    

# load and preprocess raw data
def clean_raw_data(raw_data, string_cols, dt_cols):
    for col in string_cols:
        raw_data[col] = raw_data[col].apply(str)
    #strt_time = time.time()
    #dash_list = []
    #slash_list = []
    for col in dt_cols:
        #We dont't need str conversion  - already type is str and if not str then pd.to_datetime will fail
        #raw_data[col] = raw_data[col].apply(str)
        raw_data[col] = raw_data[col].replace(['9999-12-31','12/31/9999'], ['2049-12-31','12/31/2049'])
        if '/' in raw_data[col][0]:
            #slash_list.append(col)
            raw_data[col] = pd.to_datetime(raw_data[col], format='%m/%d/%Y')
        elif '-' in raw_data[col][0]:
            #dash_list.append(col)
            raw_data[col] = pd.to_datetime(raw_data[col], format='%Y-%m-%d')

    #raw_data[slash_list] = pd.to_datetime(raw_data[slash_list], format='%m/%d/%Y')
    #raw_data[dash_list] = pd.to_datetime(raw_data[dash_list], format='%Y-%m-%d')
    #raw_data[dt_cols].apply(pd.to_datetime)
    #print("FOR LOOP TIME : "+str(time.time()-strt_time))
    return raw_data


def exact_match(col_list1, col_list2, compare_table):
    output_array = np.zeros(compare_table.shape[0])
    output_array_deno = np.sum(compare_table.loc[:, col_list1] != 'None', axis=1) + 0.0
    for col_name1 in col_list1:
        compare_table[col_name1] = compare_table[col_name1].str.replace(' ', '')
        output_array_0 = np.zeros(compare_table.shape[0])
        for col_name2 in col_list2:
            compare_table[col_name2] = compare_table[col_name2].str.replace(' ', '')
            output_array_0 = output_array_0 + np.array(
                (compare_table[col_name1] == compare_table[col_name2]) & (compare_table[col_name1] != 'None'))
        output_array_0 = (output_array_0 > 0) * 1
        output_array = output_array + output_array_0
    output_array = output_array / output_array_deno
    output_array[output_array_deno == 0] = 0
    return output_array  # (output_array > 0) * 1


def merge_to_model_ready(current_header, current_detail, history_header, history_detail, exact_match_header):
    history_detail = pd.merge(history_detail, history_header, on=['KEY_CHK_DCN_NBR', 'CLM_CMPLTN_DT', 'KEY_CHK_DCN_ITEM_CD'], how='inner')
    current_detail = pd.merge(current_detail, current_header, on=['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR'], how='inner')
    other_cols_header = np.setdiff1d(np.array(current_header.columns), np.array(exact_match_header))
    other_cols_header_rename = {x: x + '_CRNT' for x in other_cols_header}
    current_header = current_header.rename(columns=other_cols_header_rename)
    other_cols_header = np.setdiff1d(np.array(history_header.columns), np.array(exact_match_header))
    other_cols_header_rename = {x: x + '_HIST' for x in other_cols_header}
    history_header = history_header.rename(columns=other_cols_header_rename)
    current_header = pd.merge(current_header, history_header, on=exact_match_header, how='inner')
    current_header = current_header[current_header.KEY_CHK_DCN_NBR_CRNT != current_header.KEY_CHK_DCN_NBR_HIST]
    Y = current_header[['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'KEY_CHK_DCN_ITEM_CD_HIST', 'CLM_PAYMNT_ACTN_CD_CRNT', 'CLM_PAYMNT_ACTN_CD_HIST']].drop_duplicates()
    Y = Y[(Y.KEY_CHK_DCN_NBR_CRNT != Y.KEY_CHK_DCN_NBR_HIST)]
    crnt_rename = {x: x + '_CRNT' for x in np.array(current_detail.columns)}
    hist_rename = {x: x + '_HIST' for x in np.array(history_detail.columns)}
    current_detail = current_detail.rename(columns=crnt_rename)
    history_detail = history_detail.rename(columns=hist_rename)
    history_detail.KEY_CHK_DCN_NBR_HIST = history_detail.KEY_CHK_DCN_NBR_HIST + '|' + history_detail.KEY_CHK_DCN_ITEM_CD_HIST
    Y.KEY_CHK_DCN_NBR_HIST = Y.KEY_CHK_DCN_NBR_HIST + '|' + Y.KEY_CHK_DCN_ITEM_CD_HIST
    history_detail = history_detail.drop(columns=['KEY_CHK_DCN_ITEM_CD_HIST'])
    Y = Y.drop(columns=['KEY_CHK_DCN_ITEM_CD_HIST'])
    X = pd.merge(Y, current_detail, on=['KEY_CHK_DCN_NBR_CRNT'], how='left')
    X = pd.merge(X, history_detail, on=['KEY_CHK_DCN_NBR_HIST'], how='left')
    del current_detail, history_detail, current_header, history_header
    # prepare output table
    start_time = time.time()
    matching_result = X.loc[:, ['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'DTL_LINE_NBR_CRNT', 'claim_lvl_dup_edit_CRNT']]

    # PROV_TAX_ID exact match
    start_time = time.time()
    col_list1 = ['PROV_TAX_ID_CRNT']
    col_list2 = ['PROV_TAX_ID_HIST']
    matching_result.loc[:, 'PROV_TAX_ID'] = exact_match(col_list1, col_list2, X)
    # PROV_SPCLTY_CD exact match
    start_time = time.time()
    col_list1 = ['PROV_SPCLTY_CD_CRNT']
    col_list2 = ['PROV_SPCLTY_CD_HIST']
    matching_result.loc[:, 'PROV_SPCLTY_CD'] = exact_match(col_list1, col_list2, X)
    # PROV_LCNS_CD exact match
    start_time = time.time()
    col_list1 = ['PROV_LCNS_CD_CRNT']
    col_list2 = ['PROV_LCNS_CD_HIST']
    matching_result.loc[:, 'PROV_LCNS_CD'] = exact_match(col_list1, col_list2, X)
    # PROV_NM exact match
    start_time = time.time()
    X['PROV_NM_CRNT'] = X['PROV_NM_CRNT'].str.lower()
    X['PROV_NM_HIST'] = X['PROV_NM_HIST'].str.lower()
    col_list1 = ['PROV_NM_CRNT']
    col_list2 = ['PROV_NM_HIST']
    matching_result.loc[:, 'PROV_NM'] = exact_match(col_list1, col_list2, X)
    # PROV_SCNDRY_NM exact match
    start_time = time.time()
    X['PROV_SCNDRY_NM_CRNT'] = X['PROV_SCNDRY_NM_CRNT'].str.lower()
    X['PROV_SCNDRY_NM_HIST'] = X['PROV_SCNDRY_NM_HIST'].str.lower()
    col_list1 = ['PROV_SCNDRY_NM_CRNT']
    col_list2 = ['PROV_SCNDRY_NM_HIST']
    matching_result.loc[:, 'PROV_SCNDRY_NM'] = exact_match(col_list1, col_list2, X)
    # PROC_CD exact match
    start_time = time.time()
    col_list1 = ['PROC_CD_CRNT']
    col_list2 = ['PROC_CD_HIST']
    matching_result.loc[:, 'PROC_CD'] = exact_match(col_list1, col_list2, X)
    # TOS_TYPE_CD exact match
    start_time = time.time()
    col_list1 = ['TOS_TYPE_CD_CRNT']
    col_list2 = ['TOS_TYPE_CD_HIST']
    matching_result.loc[:, 'TOS_TYPE_CD'] = exact_match(col_list1, col_list2, X)
    # HCPCS_CD exact match
    start_time = time.time()
    col_list1 = ['HCPCS_CD_CRNT']
    col_list2 = ['HCPCS_CD_HIST']
    matching_result.loc[:, 'HCPCS_CD'] = exact_match(col_list1, col_list2, X)
    # BILLD_CHRGD_AMT int exact match
    start_time = time.time()
    X['BILLD_CHRGD_AMT_CRNT'] = pd.to_numeric(X['BILLD_CHRGD_AMT_CRNT'], downcast='integer', errors='coerce')
    X['BILLD_CHRGD_AMT_HIST'] = pd.to_numeric(X['BILLD_CHRGD_AMT_HIST'], downcast='integer', errors='coerce')
    matching_result.loc[:, 'BILLD_CHRGD_AMT'] = np.array(X['BILLD_CHRGD_AMT_CRNT'] == X['BILLD_CHRGD_AMT_HIST']) * 1
    # BILLD_SRVC_UNIT_QTY int exact match
    start_time = time.time()
    X['BILLD_SRVC_UNIT_QTY_CRNT'] = pd.to_numeric(X['BILLD_SRVC_UNIT_QTY_CRNT'], downcast='integer', errors='coerce')
    X['BILLD_SRVC_UNIT_QTY_HIST'] = pd.to_numeric(X['BILLD_SRVC_UNIT_QTY_HIST'], downcast='integer', errors='coerce')
    matching_result.loc[:, 'BILLD_SRVC_UNIT_QTY'] = np.array(X['BILLD_SRVC_UNIT_QTY_CRNT'] == X['BILLD_SRVC_UNIT_QTY_HIST']) * 1
    # UNITS_OCR_NBR int exact match
    start_time = time.time()
    X['UNITS_OCR_NBR_CRNT'] = pd.to_numeric(X['UNITS_OCR_NBR_CRNT'], downcast='integer', errors='coerce')
    X['UNITS_OCR_NBR_HIST'] = pd.to_numeric(X['UNITS_OCR_NBR_HIST'], downcast='integer', errors='coerce')
    matching_result.loc[:, 'UNITS_OCR_NBR'] = np.array(X['UNITS_OCR_NBR_CRNT'] == X['UNITS_OCR_NBR_HIST']) * 1
    # ICD_CD exact match
    start_time = time.time()
    col_list1 = ['ICD_A_CD_CRNT', 'ICD_B_CD_CRNT', 'ICD_C_CD_CRNT', 'ICD_D_CD_CRNT', 'ICD_E_CD_CRNT']
    col_list2 = ['ICD_A_CD_HIST', 'ICD_B_CD_HIST', 'ICD_C_CD_HIST', 'ICD_D_CD_HIST', 'ICD_E_CD_HIST']
    matching_result.loc[:, 'ICD_CD'] = exact_match(col_list1, col_list2, X)
    # PROC_MDFR_CD exact match
    start_time = time.time()
    col_list1 = ['PROC_MDFR_CD_CRNT']
    col_list2 = ['PROC_MDFR_CD_HIST']
    matching_result.loc[:, 'PROC_MDFR_CD'] = exact_match(col_list1, col_list2, X)
    # HCPCS_MDFR_CD exact match
    start_time = time.time()
    col_list1 = ['HCPCS_MDFR_CD_CRNT']
    col_list2 = ['HCPCS_MDFR_CD_HIST']
    matching_result.loc[:, 'HCPCS_MDFR_CD'] = exact_match(col_list1, col_list2, X)
    # MDFR_CD exact match
    start_time = time.time()
    col_list1 = ['MDFR_1_CD_CRNT', 'MDFR_2_CD_CRNT', 'MDFR_3_CD_CRNT']
    col_list2 = ['MDFR_1_CD_HIST', 'MDFR_2_CD_HIST', 'MDFR_3_CD_HIST']
    matching_result.loc[:, 'MDFR_CD'] = exact_match(col_list1, col_list2, X)
    # HCFA_PT_CD exact match
    start_time = time.time()
    col_list1 = ['HCFA_PT_CD_CRNT']
    col_list2 = ['HCFA_PT_CD_HIST']
    matching_result.loc[:, 'HCFA_PT_CD'] = exact_match(col_list1, col_list2, X)
    # TYPE_OF_BILL_CD exact match
    start_time = time.time()
    col_list1 = ['TYPE_OF_BILL_CD_CRNT']
    col_list2 = ['TYPE_OF_BILL_CD_HIST']
    matching_result.loc[:, 'TYPE_OF_BILL_CD'] = exact_match(col_list1, col_list2, X)
    # PROV_LCNS_CD exact match
    start_time = time.time()
    col_list1 = ['CLAIM_TYPE_CRNT']
    col_list2 = ['CLAIM_TYPE_HIST']
    matching_result.loc[:, 'CLAIM_TYPE'] = exact_match(col_list1, col_list2, X)
    # TOTL_CHRG_AMT int exact match
    start_time = time.time()
    X['TOTL_CHRG_AMT_CRNT'] = pd.to_numeric(X['TOTL_CHRG_AMT_CRNT'], downcast='integer', errors='coerce')
    X['TOTL_CHRG_AMT_HIST'] = pd.to_numeric(X['TOTL_CHRG_AMT_HIST'], downcast='integer', errors='coerce')
    matching_result.loc[:, 'TOTL_CHRG_AMT'] = np.array(X['TOTL_CHRG_AMT_CRNT'] == X['TOTL_CHRG_AMT_HIST']) * 1

    start_time = time.time()
    matching_result.loc[:, 'SRVC_FROM_DT_DTL'] = abs((X['SRVC_FROM_DT_DTL_CRNT'] - X['SRVC_FROM_DT_DTL_HIST']).dt.days)
    matching_result.loc[pd.isnull(matching_result.loc[:, 'SRVC_FROM_DT_DTL']), 'SRVC_FROM_DT_DTL'] = 100
    # SRVC_TO_DT_DTL difference
    start_time = time.time()
    matching_result.loc[:, 'SRVC_TO_DT_DTL'] = abs((X['SRVC_TO_DT_DTL_CRNT'] - X['SRVC_TO_DT_DTL_HIST']).dt.days)
    matching_result.loc[pd.isnull(matching_result.loc[:, 'SRVC_TO_DT_DTL']), 'SRVC_TO_DT_DTL'] = 100
    # GPR_NBR exact match
    start_time = time.time()
    col_list1 = ['GRP_NBR_CRNT']
    col_list2 = ['GRP_NBR_HIST']
    matching_result.loc[:, 'GRP_NBR'] = exact_match(col_list1, col_list2, X)
    matching_result_agg = matching_result.groupby(['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'DTL_LINE_NBR_CRNT'],as_index=False).agg('max')
    X_claim = matching_result_agg.groupby(['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST'], as_index=False).agg('mean')
    line_nbr_agg = X.groupby(['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST'], as_index=False)['DTL_LINE_NBR_CRNT','DTL_LINE_NBR_HIST'].agg('max')
    line_nbr_agg['diff_DTL_LINE_NBR'] = abs(line_nbr_agg['DTL_LINE_NBR_CRNT'].apply(int) - line_nbr_agg['DTL_LINE_NBR_HIST'].apply(int))
    X_claim = pd.merge(X_claim, line_nbr_agg.loc[:,['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST','diff_DTL_LINE_NBR']], on=['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST'], how='left')
    Y = pd.merge(Y, X_claim, on=['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST'], how='left')
    # TODO not sure why i have to drop DTL_LINE_NBR_CRNT
    Y_data = Y.loc[:, ['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'CLM_PAYMNT_ACTN_CD_CRNT', 'CLM_PAYMNT_ACTN_CD_HIST', 'DTL_LINE_NBR_CRNT']]
    # Y_data = Y.loc[:, ['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'CLM_PAYMNT_ACTN_CD_CRNT', 'CLM_PAYMNT_ACTN_CD_HIST']]
    # TODO not sure why i have to drop DTL_LINE_NBR_CRNT
    # X_data = Y.drop(columns=['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'CLM_PAYMNT_ACTN_CD_CRNT', 'CLM_PAYMNT_ACTN_CD_HIST', 'DTL_LINE_NBR_CRNT'])
    X_data = Y.drop(columns=['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'CLM_PAYMNT_ACTN_CD_CRNT', 'CLM_PAYMNT_ACTN_CD_HIST'])
    col = pd.DataFrame({'VAR':pd.Series(X_data.columns.values)})
    col['col'] = range(X_data.shape[1])
    X_data = X_data.fillna(0)
    X_data = sp.csr_matrix(X_data.values)
    X = X.loc[:,['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'PROV_TAX_ID_CRNT', 'PROV_TAX_ID_HIST', 'PROV_SPCLTY_CD_CRNT', 'PROV_SPCLTY_CD_HIST', 'PROV_LCNS_CD_CRNT', 'PROV_LCNS_CD_HIST', 'PROV_NM_CRNT', 'PROV_NM_HIST', 'PROV_SCNDRY_NM_CRNT', 'PROV_SCNDRY_NM_HIST', 'ICD_A_CD_CRNT', 'ICD_A_CD_HIST', 'ICD_B_CD_CRNT', 'ICD_B_CD_HIST', 'ICD_C_CD_CRNT', 'ICD_C_CD_HIST', 'ICD_D_CD_CRNT', 'ICD_D_CD_HIST', 'ICD_E_CD_CRNT', 'ICD_E_CD_HIST', 'TYPE_OF_BILL_CD_CRNT', 'TYPE_OF_BILL_CD_HIST', 'CLAIM_TYPE_CRNT', 'CLAIM_TYPE_HIST', 'GRP_NBR_CRNT', 'GRP_NBR_HIST', 'TOTL_CHRG_AMT_CRNT', 'TOTL_CHRG_AMT_HIST']]
    X = pd.merge(X, line_nbr_agg.loc[:,['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST','DTL_LINE_NBR_CRNT','DTL_LINE_NBR_HIST']], on=['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST'], how='left')
    X = X.drop_duplicates()

    return X_data, Y_data, col, X


def score_model_data(Dup_model_name, X, Y, col, X_merged):

    # model = pickle.load(open(Dup_model_name, 'rb'))
    model = Dup_model_name
    '''
    for ar in X:
        arry = ar.toarray()
        print('################################')
        for idx, val in enumerate(arry[0]):
            if val != 0:
                print('(0,' + str(idx) + ')\t' + str(val))
        print('*******************************')
    '''

    Y['DUP_Score'] = model.predict_proba(X)[:, -1]

    Y['history_dup_claim'] = np.array(Y.CLM_PAYMNT_ACTN_CD_HIST.isin(['RDUPD7', 'RDUPD8', 'RDUPHD', 'RDUPN0', 'RDUPQ0', 'RDUP00', 'R12040', 'R17620', 'R24690'])) * 1
    model_out = Y.sort_values(['KEY_CHK_DCN_NBR_CRNT', 'DUP_Score','CLM_PAYMNT_ACTN_CD_HIST','history_dup_claim'], ascending=[True, False, True, True])
    model_out['idx'] = model_out.groupby(['KEY_CHK_DCN_NBR_CRNT']).cumcount()
    final_out = model_out.groupby(['KEY_CHK_DCN_NBR_CRNT'], as_index=False).agg({'idx': np.max}).rename(
        columns={'idx': 'num_potential_dups'})
    final_out['num_potential_dups'] = final_out['num_potential_dups'] + 1
    for i in range(3):
        idx = i + 1
        tmp = model_out[model_out['idx'] == i][['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST', 'DUP_Score']].rename(
            columns={'KEY_CHK_DCN_NBR_HIST': 'Top_' + str(idx) + '_DUP', 'DUP_Score': 'Top_' + str(idx) + '_DUP_Score'})
        final_out = pd.merge(final_out, tmp, how='left', on=['KEY_CHK_DCN_NBR_CRNT'])
#         feature_data = pd.DataFrame(X.toarray(),columns=col.VAR)
#         feature_data = feature_data.reset_index(drop=True)
#         score_feature_data = pd.concat([Y,feature_data],axis=1,join='inner')
#         score_feature_data.to_csv(output_feature_file, index=False)
    
#         Y.to_csv(output_score_file, index=False)
    
#         feature_data = pd.DataFrame(X.toarray(),columns=col.VAR)
#         feature_data = feature_data.reset_index(drop=True)
#         TF_col = ['PROV_TAX_ID','PROV_SPCLTY_CD', 'PROV_LCNS_CD', 'PROV_NM', 'PROV_SCNDRY_NM','PROC_CD', 'TOS_TYPE_CD', 'HCPCS_CD', 'BILLD_CHRGD_AMT','BILLD_SRVC_UNIT_QTY', 'UNITS_OCR_NBR', 'ICD_CD', 'PROC_MDFR_CD','HCPCS_MDFR_CD', 'MDFR_CD', 'HCFA_PT_CD', 'TYPE_OF_BILL_CD','CLAIM_TYPE', 'TOTL_CHRG_AMT']
#         diff_col = ['diff_DTL_LINE_NBR']
#         feature_data = feature_data.loc[:,TF_col+diff_col]
#         feature_data.loc[:,TF_col] = feature_data.loc[:,TF_col] < 1
#         feature_data.loc[:,diff_col] = feature_data.loc[:,diff_col] > 0
#         Y['diff_col'] = feature_data.apply(lambda x: '|'.join(x.index[x].tolist()),axis=1)
#         Y.to_csv(output_diff_file, index=False)
    
    X_merged = X_merged.merge(final_out.loc[:,['KEY_CHK_DCN_NBR_CRNT','Top_1_DUP','Top_1_DUP_Score']],how='left',right_on=['KEY_CHK_DCN_NBR_CRNT','Top_1_DUP'],left_on=['KEY_CHK_DCN_NBR_CRNT', 'KEY_CHK_DCN_NBR_HIST']).drop(columns=['Top_1_DUP'])
    return final_out, Y, X_merged


# correct billing part
def correct_billing_model_data(DUP_score, row_data, col_locator, Correct_billing_X, X, Y, col, current_detail, history_detail):
    DUP_score = DUP_score.loc[:,['KEY_CHK_DCN_NBR_CRNT','KEY_CHK_DCN_NBR_HIST','CLM_PAYMNT_ACTN_CD_CRNT','CLM_PAYMNT_ACTN_CD_HIST','DTL_LINE_NBR_CRNT','DUP_Score']]
    row_data['KEY_CHK_DCN_NBR'] = row_data['KEY_CHK_DCN_NBR'].apply(str)
    DUP_max_score = DUP_score.sort_values(by=['DUP_Score','CLM_PAYMNT_ACTN_CD_HIST'], ascending=[True, False]).drop_duplicates(subset=['KEY_CHK_DCN_NBR_CRNT'],keep='last').rename(columns={'DUP_Score': 'max_score'}).reset_index(drop=True)
    DUP_max_score = DUP_max_score.loc[:,['KEY_CHK_DCN_NBR_CRNT','KEY_CHK_DCN_NBR_HIST','CLM_PAYMNT_ACTN_CD_HIST','max_score']]
    DUP_row_P_agg = DUP_score.loc[~DUP_score.CLM_PAYMNT_ACTN_CD_HIST.str.startswith('R'),:].reset_index(drop=True).groupby(by=['KEY_CHK_DCN_NBR_CRNT'],as_index=False)['DUP_Score'].max().rename(columns={'DUP_Score': 'max_paid_score'})
    DUP_row = row_data.merge(DUP_max_score, how='left',left_on=['KEY_CHK_DCN_NBR'], right_on=['KEY_CHK_DCN_NBR_CRNT'])
    DUP_row = DUP_row.drop(columns=['KEY_CHK_DCN_NBR_CRNT'])
    DUP_row_score = DUP_row.merge(DUP_row_P_agg, how='left',left_on=['KEY_CHK_DCN_NBR'], right_on=['KEY_CHK_DCN_NBR_CRNT'])
    DUP_row_score = DUP_row_score.drop(columns=['KEY_CHK_DCN_NBR_CRNT'])
    DUP_row_score.loc[pd.isnull(DUP_row_score.max_score),'max_score'] = 0
    DUP_row_score.loc[pd.isnull(DUP_row_score.max_paid_score),'max_paid_score'] = DUP_row_score.loc[pd.isnull(DUP_row_score.max_paid_score),'max_score']

    col_locator = col_locator.append(pd.DataFrame([['max_score',col_locator.shape[0]],['max_paid_score',col_locator.shape[0]+1],['Original_P',col_locator.shape[0]+2],['Original_R',col_locator.shape[0]+3],['TOTL_CHRG_same',col_locator.shape[0]+4],['ICD_CD_same',col_locator.shape[0]+5],['LINE_NBR_same',col_locator.shape[0]+6]], columns=['VAR','col'])).reset_index(drop=True)
    current_detail['DTL_LINE_NBR'] = pd.to_numeric(current_detail['DTL_LINE_NBR'], downcast='integer', errors='coerce')
    current_detail_agg = current_detail.loc[:,['KEY_CHK_DCN_NBR','DTL_LINE_NBR']].groupby(['KEY_CHK_DCN_NBR'],as_index=False).agg('max')
    history_detail['DTL_LINE_NBR'] = pd.to_numeric(history_detail['DTL_LINE_NBR'], downcast='integer', errors='coerce')
    history_detail.KEY_CHK_DCN_NBR = history_detail.KEY_CHK_DCN_NBR + '|' + history_detail.KEY_CHK_DCN_ITEM_CD
    history_detail_agg = history_detail.loc[:,['KEY_CHK_DCN_NBR','DTL_LINE_NBR']].groupby(['KEY_CHK_DCN_NBR'],as_index=False).agg('max')
    other_cols_header = ['KEY_CHK_DCN_NBR','DTL_LINE_NBR']
    other_cols_header_rename = {x: x + '_CRNT' for x in other_cols_header}
    current_detail_agg = current_detail_agg.rename(columns=other_cols_header_rename)
    other_cols_header_rename = {x: x + '_HIST' for x in other_cols_header}
    history_detail_agg = history_detail_agg.rename(columns=other_cols_header_rename)
    DUP_row_score1 = DUP_row_score.merge(current_detail_agg,how='left',left_on='KEY_CHK_DCN_NBR',right_on='KEY_CHK_DCN_NBR_CRNT')
    DUP_row_score1 = DUP_row_score1.merge(history_detail_agg,how='left',on='KEY_CHK_DCN_NBR_HIST')
    DUP_row_score1 = DUP_row_score1.fillna('*')
    DUP_row_score1['Original_P'] = np.array((DUP_row_score1.CLM_PAYMNT_ACTN_CD_HIST.str.startswith('P')) | (DUP_row_score1.CLM_PAYMNT_ACTN_CD_HIST.str.startswith('D'))) * 1
    DUP_row_score1['Original_R'] = np.array(DUP_row_score1.CLM_PAYMNT_ACTN_CD_HIST.str.startswith('R')) * 1
    DUP_row_score1['LINE_NBR_same'] = np.array(DUP_row_score1['DTL_LINE_NBR_CRNT'] == DUP_row_score1['DTL_LINE_NBR_HIST']) * 1
    Y['TOTL_CHRG_same'] = np.reshape(X[:, np.array(col['VAR']=='TOTL_CHRG_AMT')].toarray(),(X.shape[0],))
    Y['ICD_CD_same'] = np.array(np.reshape(X[:, np.array(col['VAR']=='ICD_CD')].toarray(),(X.shape[0],)) == 1) * 1
    DUP_row_score1 = DUP_row_score1.merge(Y.loc[:,['KEY_CHK_DCN_NBR_CRNT','KEY_CHK_DCN_NBR_HIST','TOTL_CHRG_same','ICD_CD_same']], how='left', on=['KEY_CHK_DCN_NBR_CRNT','KEY_CHK_DCN_NBR_HIST'])
    DUP_row_score1 = DUP_row_score1.fillna(0)
    X_data = Correct_billing_X
    DUP_row_score1 = DUP_row_score1.sort_values(by=['row']).reset_index(drop=True)
    # pd.set_option('display.max_columns', None)
    # print(type(DUP_row_score1))
    # print(DUP_row_score1)

    X_data_sub1 = sp.hstack((X_data, np.reshape(np.array(DUP_row_score1.max_score), (len(np.array(DUP_row_score1.max_score)), 1))))+0.0
    X_data_sub1 = sp.hstack((X_data_sub1, np.reshape(np.array(DUP_row_score1.max_paid_score), (len(np.array(DUP_row_score1.max_paid_score)), 1))))+0.0
    X_data_sub1 = sp.hstack((X_data_sub1, np.reshape(np.array(DUP_row_score1.Original_P), (len(np.array(DUP_row_score1.Original_P)), 1))))+0.0
    X_data_sub1 = sp.hstack((X_data_sub1, np.reshape(np.array(DUP_row_score1.Original_R), (len(np.array(DUP_row_score1.Original_R)), 1))))+0.0
    X_data_sub1 = sp.hstack((X_data_sub1, np.reshape(np.array(DUP_row_score1.TOTL_CHRG_same), (len(np.array(DUP_row_score1.TOTL_CHRG_same)), 1))))+0.0
    X_data_sub1 = sp.hstack((X_data_sub1, np.reshape(np.array(DUP_row_score1.ICD_CD_same), (len(np.array(DUP_row_score1.ICD_CD_same)), 1))))+0.0
    X_data_sub1 = sp.hstack((X_data_sub1, np.reshape(np.array(DUP_row_score1.LINE_NBR_same), (len(np.array(DUP_row_score1.LINE_NBR_same)), 1))))+0.0
    X_data_sub1 = sp.csr_matrix(X_data_sub1)
    return X_data_sub1, DUP_row_score1.loc[:,['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','row','max_score','max_paid_score','Original_P','Original_R','TOTL_CHRG_same','ICD_CD_same','LINE_NBR_same']],col_locator


def DUP_deploy(current_header, current_detail, raw_edit, history_header, history_detail, Correct_billing_X, col_locator, row_data, Dup_model_name,logger,extra):
    start_time = time.time()
    string_cols_header = ['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','CLM_PAYMNT_ACTN_1_CD', 'CLM_PAYMNT_ACTN_2_6_CD', 'MEMBER_SSN','PAT_MBR_CD', 'PROV_TAX_ID', 'PROV_SPCLTY_CD', 'PROV_LCNS_CD', 'PROV_NM', 'PROV_SCNDRY_NM', 'ICD_A_CD', 'ICD_B_CD', 'ICD_C_CD','ICD_D_CD', 'ICD_E_CD', 'TYPE_OF_BILL_CD', 'CLAIM_TYPE','GRP_NBR']
    dt_cols_header = ['SRVC_FROM_DT', 'SRVC_THRU_DT']
    current_header = clean_raw_data(current_header, string_cols_header, dt_cols_header)
    string_cols_detail = ['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','PROC_CD', 'TOS_TYPE_CD', 'PROC_SRVC_CLS_1_CD', 'PROC_SRVC_CLS_2_CD','PROC_SRVC_CLS_3_CD', 'HCPCS_CD', 'PROC_MDFR_CD', 'HCPCS_MDFR_CD', 'MDFR_1_CD', 'MDFR_2_CD','MDFR_3_CD', 'HCFA_PT_CD', 'POT_CD']
    dt_cols_detail = ['SRVC_FROM_DT_DTL', 'SRVC_TO_DT_DTL']
    current_detail = clean_raw_data(current_detail, string_cols_detail, dt_cols_detail)

    string_cols_header = ['KEY_CHK_DCN_NBR', 'KEY_CHK_DCN_ITEM_CD', 'CLM_PAYMNT_ACTN_1_CD', 'CLM_PAYMNT_ACTN_2_6_CD', 'MEMBER_SSN','PAT_MBR_CD', 'PROV_TAX_ID', 'PROV_SPCLTY_CD', 'PROV_LCNS_CD', 'PROV_NM', 'PROV_SCNDRY_NM', 'ICD_A_CD', 'ICD_B_CD', 'ICD_C_CD','ICD_D_CD', 'ICD_E_CD', 'TYPE_OF_BILL_CD', 'CLAIM_TYPE','GRP_NBR']
    dt_cols_header = ['CLM_CMPLTN_DT', 'SRVC_FROM_DT', 'SRVC_THRU_DT']
    history_header = clean_raw_data(history_header, string_cols_header, dt_cols_header)

    string_cols_detail = ['KEY_CHK_DCN_NBR', 'KEY_CHK_DCN_ITEM_CD', 'PROC_CD', 'TOS_TYPE_CD', 'PROC_SRVC_CLS_1_CD', 'PROC_SRVC_CLS_2_CD','PROC_SRVC_CLS_3_CD', 'HCPCS_CD', 'PROC_MDFR_CD', 'HCPCS_MDFR_CD', 'MDFR_1_CD', 'MDFR_2_CD','MDFR_3_CD', 'HCFA_PT_CD', 'POT_CD']
    dt_cols_detail = ['CLM_CMPLTN_DT', 'SRVC_FROM_DT_DTL', 'SRVC_TO_DT_DTL']
    history_detail = clean_raw_data(history_detail, string_cols_detail, dt_cols_detail)

    #print("Load raw data finished in %s minutes" % ((time.time() - start_time) / 60))

    edit_code_list = ['QCB','QCM','QC0','QC5','QC8','QTB','QTD','QTM','QTR','QTS','QT0','QT2','QT5','QT6','QT7','QT8','QV1','QV2','QV9','Q47']
    raw_edit['claim_lvl_dup_edit'] = ((raw_edit.ERR_1_CD.isin(edit_code_list)) | (raw_edit.ERR_2_CD.isin(edit_code_list)) | (raw_edit.ERR_3_CD.isin(edit_code_list)) | (raw_edit.ERR_4_CD.isin(edit_code_list)) | (raw_edit.ERR_5_CD.isin(edit_code_list)) | (raw_edit.ERR_6_CD.isin(edit_code_list)) | (raw_edit.ERR_7_CD.isin(edit_code_list)) | (raw_edit.ERR_8_CD.isin(edit_code_list)) | (raw_edit.ERR_9_CD.isin(edit_code_list)) | (raw_edit.ERR_10_CD.isin(edit_code_list)) | (raw_edit.ERR_11_CD.isin(edit_code_list)) | (raw_edit.ERR_12_CD.isin(edit_code_list)) | (raw_edit.ERR_13_CD.isin(edit_code_list))).astype(int)

    raw_edit_agg = raw_edit.groupby(['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR'], as_index=False)['claim_lvl_dup_edit'].sum()
    raw_edit_sub = raw_edit_agg.loc[:,['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR','claim_lvl_dup_edit']]
    raw_edit_sub.claim_lvl_dup_edit = np.array(raw_edit_sub.claim_lvl_dup_edit >= 1) * 1
    current_header = current_header.merge(raw_edit_sub, how='left', on=['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR'])
    current_header.loc[pd.isnull(current_header.claim_lvl_dup_edit),'claim_lvl_dup_edit'] = 0
    current_header['CLM_PAYMNT_ACTN_CD'] = current_header['CLM_PAYMNT_ACTN_1_CD'] + current_header['CLM_PAYMNT_ACTN_2_6_CD']
    del raw_edit_sub, raw_edit_agg, raw_edit
    #print("Process current data finished in %s minutes" % ((time.time() - start_time) / 60))


    history_header['CLM_PAYMNT_ACTN_CD'] = history_header['CLM_PAYMNT_ACTN_1_CD'] + history_header['CLM_PAYMNT_ACTN_2_6_CD']
    # history_header = history_header.loc[~history_header['CLM_PAYMNT_ACTN_CD'].isin(['RDUPD7', 'RDUPD8', 'RDUPHD', 'RDUPN0', 'RDUPQ0', 'RDUP00', 'R12040', 'R17620', 'R24690']),:]
    history_header = history_header.drop(columns=['CLM_PAYMNT_ACTN_2_6_CD'])
    current_header = current_header.drop(columns=['CLM_PAYMNT_ACTN_1_CD', 'CLM_PAYMNT_ACTN_2_6_CD'])
    history_detail = history_detail.drop(columns=['PROC_SRVC_CLS_1_CD', 'PROC_SRVC_CLS_2_CD','PROC_SRVC_CLS_3_CD', 'POT_CD'])
    current_detail = current_detail.drop(columns=['PROC_SRVC_CLS_1_CD', 'PROC_SRVC_CLS_2_CD','PROC_SRVC_CLS_3_CD', 'POT_CD'])
    #print("Process history data finished in %s minutes" % ((time.time() - start_time) / 60))


    current_header['MEMBER_SSN'] = current_header['memID']
    X, Y, col, X_merged = merge_to_model_ready(current_header, current_detail, history_header,history_detail, ['MEMBER_SSN', 'PAT_MBR_CD', 'SRVC_FROM_DT', 'SRVC_THRU_DT'])
    # print(col)
    #print("Top Dups Model data finished in %s minutes" % ((time.time() - start_time) / 60))
    logger.debug('Elapsed time', extra=response.extra_tags(extra, {'Prepare_model_data': time.time() - start_time}, 0))
    start_time = time.time()
    final_out, DUP_score, dup_merged = score_model_data(Dup_model_name, X, Y, col, X_merged)
    dup_top3 = final_out
    logger.debug('Elapsed time', extra=response.extra_tags(extra, {'DUP_model_execution': time.time() - start_time}, 0))
    #print("Top Dups Prediction finished in %s minutes" % ((time.time() - start_time) / 60))
    
    start_time = time.time()
    # TODO MESSING WITH THE VALUES
    X,Y,col = correct_billing_model_data(DUP_score, row_data, col_locator, Correct_billing_X, X, Y, col, current_detail, history_detail)
    # X,Z,col = correct_billing_model_data(DUP_score, row_data, col_locator, Correct_billing_X, X, Y, col, current_detail, history_detail)
    # print("Correct Billing Model data finished in %s minutes" % ((time.time() - start_time) / 60))
    # print(dup_top3)
    
    return dup_top3, dup_merged, X, Y, col


def populate_hist(payload,payload_req,logger,extra):        # histories = payload.get('hist', [])
    MEMBER_SSN = payload['header'].get('memID') if payload['header'].get('memID') is not None else ''
    PAT_MBR_CD = payload['header'].get('PAT_MBR_CD') if payload['header'].get('PAT_MBR_CD') is not None else ''
    SRVC_FROM_DT = payload['header'].get('SRVC_FROM_DT') if payload['header'].get('SRVC_FROM_DT') is not None else ''
    SRVC_THRU_DT = payload['header'].get('SRVC_THRU_DT') if payload['header'].get('SRVC_THRU_DT') is not None else ''
    SRVC_FROM_DT = datetime.strptime(SRVC_FROM_DT, '%m/%d/%Y').strftime('%Y-%m-%d')
    SRVC_THRU_DT = datetime.strptime(SRVC_THRU_DT, '%m/%d/%Y').strftime('%Y-%m-%d')
    # hist_hdr_sql = "SELECT * FROM HIST_HDR WHERE MEMBER_SSN = ? AND PAT_MBR_CD = ? AND SRVC_FROM_DT = ? AND SRVC_THRU_DT = ?"
    # hist_dtl_sql = "SELECT * FROM HIST_DTL WHERE KEY_CHK_DCN_NBR = ? AND KEY_CHK_DCN_ITEM_CD = ? AND CLM_CMPLTN_DT = ?"
    #print("values are : MEMBER_SSN : "+str(MEMBER_SSN)+ " PAT_MBR_CD :"+str(PAT_MBR_CD)+ " SRVC_FROM_DT : "+str(SRVC_FROM_DT)+" SRVC_THRU_DT : "+str(SRVC_THRU_DT))
    conn = None
    start_time = time.time()
    try:
        hist = []
        memID = MEMBER_SSN + str(PAT_MBR_CD) + SRVC_FROM_DT + SRVC_THRU_DT
        output = json.loads(hbase.hbaseLookup(memID, constants.HBASE_DUP_COLUMN, logger, extra))
        
        hist_hdr_data =  pd.DataFrame.from_dict(json_normalize(output['CogxClaim']), orient='columns')
        hist_hdr_data.drop('details', axis=1, inplace=True)
        hist_hdr_data.columns = [x.split('.')[1] if len(x.split('.')) > 0 else x for x in hist_hdr_data.columns]
        '''
        hist_hdr_data = json_normalize(output['cogxClaim'])
        hist_hdr_data.columns = map(str.upper, hist_hdr_data.columns)        
        hist_hdr_data = hist_hdr_data.drop(['DTL_LINE_NBR', 'PROC_CD', 'TOS_TYPE_CD', 'PROC_SRVC_CLS_1_CD', 'PROC_SRVC_CLS_2_CD', 'PROC_SRVC_CLS_3_CD', 'HCPCS_CD', 'BILLD_CHRGD_AMT', 'BILLD_SRVC_UNIT_QTY', 'UNITS_OCR_NBR', 'PROC_MDFR_CD', 'HCPCS_MDFR_CD', 'MDFR_1_CD', 'MDFR_2_CD', 'MDFR_3_CD', 'HCFA_PT_CD', 'POT_CD', 'ELGBL_EXPNS_AMT', 'SRVC_FROM_DT_DTL', 'SRVC_TO_DT_DTL', 'ICD_9_1_CD', 'LOAD_INGSTN_ID'], axis=1)
        hist_hdr_data['CLM_CMPLTN_DT'] =  hist_hdr_data['CLM_CMPLTN_DT'].apply(lambda x : x[0:4] + '-' + x[4:6] + '-' + x[6:])
        hist_hdr_data['SRVC_FROM_DT'] =  hist_hdr_data['SRVC_FROM_DT'].apply(lambda x : x[0:4] + '-' + x[4:6] + '-' + x[6:])
        hist_hdr_data['SRVC_THRU_DT'] =  hist_hdr_data['SRVC_THRU_DT'].apply(lambda x : x[0:4] + '-' + x[4:6] + '-' + x[6:])
        '''
        hist_hdr_data = hist_hdr_data.drop_duplicates().reset_index(drop=True)
        hist_dtl_data = json_normalize(output['CogxClaim'], record_path='details')
        '''
        hist_dtl_data = json_normalize(output['cogxClaim'])
        hist_dtl_data.columns = map(str.upper, hist_dtl_data.columns)
        hist_dtl_data = hist_dtl_data.drop(['CLM_PAYMNT_ACTN_1_CD', 'CLM_PAYMNT_ACTN_2_6_CD', 'MEMBER_SSN', 'PAT_MBR_CD', 'GRP_NBR', 'SRVC_FROM_DT', 'SRVC_THRU_DT', 'PROV_TAX_ID', 'PROV_NM', 'PROV_SCNDRY_NM', 'PROV_SPCLTY_CD', 'PROV_LCNS_CD', 'TOTL_CHRG_AMT', 'TYPE_OF_BILL_CD', 'CLAIM_TYPE', 'MDCL_RCRD_2_NBR', 'MRN_NBR', 'ICD_A_CD', 'ICD_B_CD', 'ICD_C_CD', 'ICD_D_CD', 'ICD_E_CD', 'PRVDR_STATUS', 'ICD_9_1_CD', 'LOAD_INGSTN_ID'], axis=1)
        hist_dtl_data = hist_dtl_data.drop_duplicates().reset_index(drop=True)
        hist_dtl_data['CLM_CMPLTN_DT'] = hist_dtl_data['CLM_CMPLTN_DT'].apply(lambda x : x[0:4] + '-' + x[4:6] + '-' + x[6:])
        hist_dtl_data['SRVC_FROM_DT_DTL'] = hist_dtl_data['SRVC_FROM_DT_DTL'].apply(lambda x : x[0:4] + '-' + x[4:6] + '-' + x[6:])
        hist_dtl_data['SRVC_TO_DT_DTL'] = hist_dtl_data['SRVC_TO_DT_DTL'].apply(lambda x : x[0:4] + '-' + x[4:6] + '-' + x[6:])
        '''
        for index,row in hist_hdr_data.iterrows():
            hist_element = {}
            hist_details = []
            hist_headers = row.to_dict()
            KEY_CHK_DCN_NBR = row.get('KEY_CHK_DCN_NBR')
            if (KEY_CHK_DCN_NBR == payload_req['KEY_CHK_DCN_NBR']):
                continue
            KEY_CHK_DCN_ITEM_CD = row.get('KEY_CHK_DCN_ITEM_CD')
            CLM_CMPLTN_DT = row.get('CLM_CMPLTN_DT')

            for index,row1 in hist_dtl_data.iterrows():
                row1 = row1.to_dict()
                if (row['KEY_CHK_DCN_NBR'] == row1['KEY_CHK_DCN_NBR'] and row['KEY_CHK_DCN_ITEM_CD'] == row1['KEY_CHK_DCN_ITEM_CD'] and row['CLM_CMPLTN_DT'] == row1['CLM_CMPLTN_DT']):
                    hist_details.append(row1)
            hist_element['hist_header'] = hist_headers
            hist_element['hist_details'] = hist_details
            hist.append(hist_element)
        payload['hist'] = hist
        '''
        conn = sqlite3.connect(constants.DB_PATH_DUP)
        hist_hdr_data = pd.read_sql_query(hist_hdr_sql, conn,params=(MEMBER_SSN, PAT_MBR_CD, SRVC_FROM_DT, SRVC_THRU_DT,))
        hist_hdr_data = hist_hdr_data.drop_duplicates().reset_index(drop=True)
        hist = []
        for index,row in hist_hdr_data.iterrows():
            hist_element = {}
            hist_details = []
            hist_headers = row.to_dict()
            KEY_CHK_DCN_NBR = row.get('KEY_CHK_DCN_NBR')
            KEY_CHK_DCN_ITEM_CD = row.get('KEY_CHK_DCN_ITEM_CD')
            CLM_CMPLTN_DT = row.get('CLM_CMPLTN_DT')
            
            #print("for hist details ; KEY_CHK_DCN_NBR : "+str(KEY_CHK_DCN_NBR)+ " KEY_CHK_DCN_ITEM_CD : "+str(KEY_CHK_DCN_ITEM_CD)+ " CLM_CMPLTN_DT : "+str(CLM_CMPLTN_DT))
            hist_dtl_data = pd.read_sql_query(hist_dtl_sql, conn,params=(KEY_CHK_DCN_NBR, KEY_CHK_DCN_ITEM_CD, CLM_CMPLTN_DT,))
            hist_dtl_data = hist_dtl_data.drop_duplicates().reset_index(drop=True)
            for index,row in hist_dtl_data.iterrows():
                row = row.to_dict()
                hist_details.append(row)
            hist_element['hist_header'] = hist_headers
            hist_element['hist_details'] = hist_details
            hist.append(hist_element)
        payload['hist'] = hist
        '''
    except:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
        payload['hist'] = []
    finally:
        logger.debug('Elapsed time',extra=response.extra_tags(extra, {'Reference Data Fetch': time.time() - start_time}, 0))
        # conn.close()

