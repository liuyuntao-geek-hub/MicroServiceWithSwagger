import time
import pandas as pd
import copy
import scipy.sparse as sp
import numpy as np
import traceback,sys
import constants as const
import response


def run(app, logger, extra, payload, payload_req):
    start_time = time.time()
    field_mapping = {}
    test_row = {}  # return value
    field_mapping['MEDCRB_APPV_EQU_PAID'] = 0.0
    field_mapping['MEDCRB_TWO_ZERO'] = 0.0
    
    field_mapping['SRVC_OUT_BNFT'] = 0.0
    field_mapping['SRVC_OVERLAP_BNFT'] = 0.0
    field_mapping['SRVC_OUT_MBR'] = 0.0
    field_mapping['SRVC_OVERLAP_MBR'] = 0.0

    payload['header']['JAA_claims'] = '0'
    payload['header']['ITS_HOST_claims'] = '0'
    payload['header']['Medi_Cal_claims'] = '0'
    payload['header']['special_edit_claims'] = '0'

    if payload.get('CLM_PAYMNT_PRCS_CD', None) == '15' and (payload['KEY_CHK_DCN_NBR'][5:7] not in (['48','87','08','47','49'])) and (payload['KEY_CHK_DCN_NBR'][5] not in (['M', 'N'])):
        payload['header']['JAA_claims'] = '1'
    if payload.get('GRP_NBR', None) is not None and payload['GRP_NBR'][0:3] == 'ITS':
        payload['header']['ITS_HOST_claims'] = '1'
    if payload.get('MBR_PROD_CLS_CD', None) in ['01','02','03', '06', '04', '08']:
        payload['header']['Medi_Cal_claims'] = '1'
    error_codes=['XPR', 'HCB','HCC','HCD','HCE','HCF','HCG','HCH','HCI','HCJ','HCK','HCL','HCM','HCN','HCO','HCP']
    err_cds = [] if payload_req.get('ERR_CDS') is None else payload_req.get('ERR_CDS')
    criteria_met = any(x in err_cds for x in error_codes)
    if criteria_met:
        payload['header']['special_edit_claims'] = '1'

    
    for key in payload.keys():
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
                field_mapping = __claim_header_processing__(field_mapping)
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
                        field_mapping = __claim_edit_processing__(field_mapping)
                    elif key == 'details':
                        field_mapping = __claim_detail_processing__(field_mapping)
                        #print(field_mapping)
        else:
            field_mapping[key] = content
            field_mapping[key + '_' + content] = 1.0


           
        '''
        raw_data['JAA_claims']= raw_data[['KEY_CHK_DCN_NBR','CLM_PAYMNT_PRCS_CD']].apply(lambda x: np.where((x['CLM_PAYMNT_PRCS_CD']=='15')&((x['KEY_CHK_DCN_NBR'][5:7] not in (['48','87','08','47','49']))&(x['KEY_CHK_DCN_NBR'][5] not in ['M','N'])),1,0),axis=1)
        raw_data['ITS_HOST']= raw_data['GRP_NBR'].apply(lambda x: np.where(x[0:3]=='ITS',1,0))
        raw_data['Medi_Cal']= raw_data['MBR_PROD_CLS_CD'].apply(lambda x: np.where(x in ['01','02','03'],1,0))
        raw_data['SSB_West_Virginia']= raw_data['MBR_PROD_CLS_CD'].apply(lambda x: np.where(x in ['06'],1,0))
        raw_data['SSB_South_Carolina_Medicare']= raw_data['MBR_PROD_CLS_CD'].apply(lambda x: np.where(x in ['04','08'],1,0))
        '''

    logger.debug('Elapsed time', extra=response.extra_tags(extra, {'Field_mapping': time.time() - start_time}, 0))
    logger.debug('mapping', extra=response.extra_tags(extra, {'ltr_mapping': field_mapping}))
    values = []
    cols = []
    reject_list = app.config['model_encodings']['reject_list']
    for idx, value in enumerate(reject_list):
        if field_mapping.get(value) is not None:
            # logger.debug(value)
            #print(value + ' -'  + str(idx-1) + ' - ' + str(float(field_mapping.get(value))))
            values.append(float(field_mapping.get(value)))
            cols.append(idx-1)
    logger.debug(values, extra=extra)
    logger.debug(cols, extra=extra)
            
    X = sp.csr_matrix((values, ([0]*len(values), cols)))
    m = X.shape[0]
    X_2 = X[np.array(range(0,m)),:]
    #with open("../../out.generated", 'a') as f:
    #    f.write("**************************** Begin of row # "+field_mapping["KEY_CHK_DCN_NBR"]+"\n")
    #    f.write(str(X_2))
    #    f.write("\n\n**************************** end of row # "+field_mapping["KEY_CHK_DCN_NBR"]+"\n")
    logger.debug(X_2, extra=extra)

    start_time = time.time()

    usecases = app.config['usecases']
    models = []
    test_row_1 = {}

    try:
        for usecase in usecases:
            if (usecase['name'] == 'ltr'):
                models = usecase['models']
                break

        for model in models:
            binary_model = copy.deepcopy(model['binary'])
            pred_test = binary_model.predict_proba(X_2)[:, -1]
            test_row_1[model['model']+ '_score'] = pred_test
            del binary_model

        '''
        ['01030' '10790' '10830' '11060' '11100' '11120' '11250' '11260' '11280'
        '11320' '12010' '12170' '12230' '12240' '12380' '12390' '12520' '12690'
        '12790' '12820' '12840' '12850' '12880' '12900' '13510' '14120' '14200'
        '15270' '17400' '17550' '17580' '20050' '2010M' '20200' '20250' '2025F'
        '20520' '20540' '21400' '21410' '21640' '21910' '21920' '23100' '24090'
        '24160' '24230' '24250' '24610' '24680' '24740' '24772' '25020' '32050'
        '32330' '40230' '50040' '50060' '50110' '50220' '50760' '51420' '51440'
        '51450' '51480' '51530' '51570' '51610' '75010' '85280' '90710' '9COB0'
        'ANLAB' 'ASH00' 'AUOHO' 'CLMCO' 'CLMRO' 'CXTB0' 'CXTH0' 'D0706' 'D1308'
        'G2400' 'GAASH' 'HDJA1' 'HDJAA' 'LABP0' 'LABX0' 'M9000' 'M9990' 'M9999'
        'MCDJ0' 'MCJ00' 'MCJS0' 'MCR00' 'NYPND' 'PAIM0' 'PREFX' 'S0002' 'SCC37'
        'WCQ02']
        '''

        df_test = pd.DataFrame(test_row_1)
        # df_test.to_csv('df_test.csv')
        # print(df_test)
        df_predict_top3 = df_test.apply(lambda x: pd.Series(x.nlargest(3).index.values).str.split('_').str.get(0), axis=1)
        # print(df_predict_top3)
        df_pred_score_top3 = df_test.apply(lambda x: pd.Series(x.nlargest(3).values), axis=1)
        # print(df_pred_score_top3)

        '''
        df_predict_top3 = test_row.iloc[:,2:8].apply(lambda x: pd.Series(x.nlargest(3).index.values).str.split('_').str.get(0), axis=1)
        df_pred_score_top3 = test_row.iloc[:,2:8].apply(lambda x: pd.Series(x.nlargest(3).values), axis=1)
        '''

        test_row['top_1_reason'] = df_predict_top3.iloc[:, 0].to_dict()[0]
        test_row['top_1_score'] = df_pred_score_top3.iloc[:, 0].to_dict()[0]
        test_row['top_2_reason'] = df_predict_top3.iloc[:, 1].to_dict()[0]
        test_row['top_2_score'] = df_pred_score_top3.iloc[:, 1].to_dict()[0]
        test_row['top_3_reason'] = df_predict_top3.iloc[:, 2].to_dict()[0]
        test_row['top_3_score'] = df_pred_score_top3.iloc[:, 2].to_dict()[0]
        # test_row.drop(labels=['50110_score','51480_score','HDJA1_score','21640_score','01030_score','51420_score','11250_score','PREFX_score','MCR00_score','G2400_score','21410_score'],axis=1,inplace=True)

        # logger.info(test_row)
    except Exception as e:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=extra)
        return response.generate_desc(903, 'ltr', test_row, payload_req)

    logger.debug('Elapsed time', extra=response.extra_tags(extra, {'model_execution': time.time() - start_time}, 0))
    # result_row['respCd'] = '700'
    # result_row['resDesc'] = 'LTR Model Processed successfully'
    result_row = {}
    err_cds = payload_req.get('ERR_CDS', [])
    actionValue = df_predict_top3.iloc[:, 0].to_dict()[0]
    logger.debug('ltr model score - ' + str(df_pred_score_top3.iloc[:, 0].to_dict()[0]), extra=extra)
    if df_pred_score_top3.iloc[:, 0].to_dict()[0] >= const.LTR_THRESHOLD and "293" in err_cds and actionValue == '50110':
        logger.info("Business exclusion 1 occur after model execution ", extra=extra)
        response.generate_desc(709, 'ltr', result_row, payload_req)
    elif df_pred_score_top3.iloc[:, 0].to_dict()[0] >= const.LTR_THRESHOLD and actionValue == '50110' and field_mapping['CURNT_QUE_UNIT_NBR'] == "GE" :
        logger.info("Business exclusion 2 occur after model execution", extra=extra)
        response.generate_desc(709, 'ltr', result_row, payload_req)
    elif df_pred_score_top3.iloc[:, 0].to_dict()[0] >= const.LTR_THRESHOLD and actionValue in ["11250", "01030"] and (field_mapping["MBR_CNTRCT_EFCTV_DT"] == field_mapping["SRVC_FROM_DT"] or
                                                 field_mapping["BNFT_YEAR_CNTRCT_REV_DT"] == field_mapping["SRVC_FROM_DT"]) :
        logger.info("Business exclusion 3 occur after model execution", extra=extra)
        response.generate_desc(709, 'ltr', result_row, payload_req)
    elif df_pred_score_top3.iloc[:, 0].to_dict()[0] >= const.LTR_THRESHOLD and actionValue == '01030' and ('509' in err_cds or '597' in err_cds) == False:
        logger.info("Business exclusion 4 occur after model execution", extra=extra)
        response.generate_desc(709, 'ltr', result_row, payload_req)
    elif df_pred_score_top3.iloc[:, 0].to_dict()[0] >= const.LTR_THRESHOLD and actionValue == 'M9999':
        logger.info("Business exclusion 5 occur after model execution", extra=extra)
        response.generate_desc(709, 'ltr', result_row, payload_req)
    elif df_pred_score_top3.iloc[:, 0].to_dict()[0] >= const.LTR_THRESHOLD:
        recommendations = []
        recommendation = {}
        recommendation['modelName'] = 'LTR'
        recommendation['actionCode'] = 'RJT'
        recommendation['actionValue'] = 'R' + actionValue
        recommendation['lineNumber'] = '00'
        recommendation['modelScore'] = df_pred_score_top3.iloc[:, 0].to_dict()[0]
        recommendation['currentQueue'] = payload_req.get('CURNT_QUE_UNIT_NBR', None)
        recommendations.append(recommendation)
        result_row['recommendations'] = recommendations
        response.generate_desc(700, 'ltr', result_row, payload_req)
    else:
        response.generate_desc(710, 'ltr', result_row, payload_req)
    return result_row


def __claim_header_processing__(field_mapping):
    for icd_cd in const.HEADER_DICT['ICD']:
        # default to * if key is not found
        mapping = field_mapping.get(icd_cd, '*')
        if mapping != '*':
            field_mapping['ICD_' + field_mapping[icd_cd]] = 1.0
    for lmt_cls_cd in const.HEADER_DICT['LMT_CLS_CD']:
        mapping = field_mapping.get(lmt_cls_cd, '*')
        if mapping != '*':
            field_mapping['LMT_CLS_CD_' + field_mapping[lmt_cls_cd]] = 1.0
    return field_mapping


def __claim_edit_processing__(field_mapping):
    for err_cd in const.EDIT_DICT['ERR_CD']:
        mapping = field_mapping.get(err_cd, '*')
        if mapping != '*':
            field_mapping['ERR_CD_' + field_mapping[err_cd]] = 1.0
    return field_mapping


def __claim_detail_processing__(field_mapping):
    for cls_cd in const.DETAIL_DICT['PROC_SRVC_CLS_CD']:
        mapping = field_mapping.get(cls_cd, '*')
        if mapping != '*':
            field_mapping['PROC_SRVC_CLS_CD_' + field_mapping[cls_cd]] = 1.0
    if (float(field_mapping.get('MEDCRB_APRVD_AMT', 0)) > 0 and 
        (float(field_mapping.get('MEDCRB_APRVD_AMT',  0)) == float(field_mapping.get('MEDCRB_PAID_AMT',  0))) ) and \
        field_mapping['MEDCRB_APPV_EQU_PAID'] == 0.0:        
        field_mapping['MEDCRB_APPV_EQU_PAID'] = 1.0
    if (float(field_mapping.get('MEDCRB_COINSRN_AMT', 0)) + float(field_mapping.get('MEDCRB_DDCTBL_AMT', 0))) == 0 \
        and field_mapping['MEDCRB_TWO_ZERO'] == 0.0:
        field_mapping['MEDCRB_TWO_ZERO'] = 1.0

    for mod_cd in const.DETAIL_DICT['MODIFIER_CD']:
        mapping = field_mapping.get(mod_cd, '*')
        if mapping != '*':
            field_mapping['MODIFIER_CD_' + field_mapping[mod_cd]] = 1.0
        
    for col_name in const.DETAIL_DATE_COLS:
        if field_mapping[col_name] == '12/31/9999':
            field_mapping[col_name] = '12/31/2049' 
        elif field_mapping[col_name] == '9999-12-31':
            field_mapping[col_name] == '2049-12-31'
        if '/' in field_mapping[col_name]:
            field_mapping[col_name] = pd.to_datetime(field_mapping[col_name],format= '%m/%d/%Y')
        elif '-' in field_mapping[col_name]:
            field_mapping[col_name] = pd.to_datetime(field_mapping[col_name],format= '%Y-%m-%d')
    if field_mapping.get('SRVC_OUT_BNFT', 0) == 0:
        field_mapping['SRVC_OUT_BNFT'] = 1 * ((field_mapping['SRVC_FROM_DT'] >= field_mapping['BNFT_YEAR_CNTRCT_REV_DT']) or (field_mapping['SRVC_TO_DT'] <= field_mapping['BNFT_YEAR_CNTRCT_EFCTV_DT']))

    field_mapping['SRVC_WITHIN_BNFT'] = 1 * (field_mapping['SRVC_FROM_DT'] >= field_mapping['BNFT_YEAR_CNTRCT_EFCTV_DT'] and field_mapping['SRVC_TO_DT'] <=  field_mapping['BNFT_YEAR_CNTRCT_REV_DT'])
    if field_mapping.get('SRVC_OVERLAP_BNFT', 0) == 0:
        field_mapping['SRVC_OVERLAP_BNFT'] = 1 * ((field_mapping['SRVC_FROM_DT'] <= field_mapping['BNFT_YEAR_CNTRCT_REV_DT']) and (field_mapping['SRVC_TO_DT'] >= field_mapping['BNFT_YEAR_CNTRCT_EFCTV_DT']))
        field_mapping['SRVC_OVERLAP_BNFT'] = field_mapping['SRVC_OVERLAP_BNFT'] - field_mapping['SRVC_WITHIN_BNFT']

    if field_mapping.get('SRVC_OUT_MBR', 0) == 0:
        field_mapping['SRVC_OUT_MBR'] = 1 * ((field_mapping['SRVC_FROM_DT'] >= field_mapping['MBR_CNTRCT_END_DT']) or (field_mapping['SRVC_TO_DT'] <= field_mapping['MBR_CNTRCT_EFCTV_DT']))

    field_mapping['SRVC_WITHIN_MBR'] = 1 * (field_mapping['SRVC_FROM_DT'] >= field_mapping['MBR_CNTRCT_EFCTV_DT'] and field_mapping['SRVC_TO_DT'] <= field_mapping['MBR_CNTRCT_END_DT'])
    # TODO need to seff if the field is being set right.  Field added in R4
    field_mapping['Coverage_Start_eq_DOS'] = 1*((field_mapping['MBR_CNTRCT_EFCTV_DT'] == field_mapping['SRVC_FROM_DT']) or (field_mapping['BNFT_YEAR_CNTRCT_REV_DT'] == field_mapping['SRVC_FROM_DT']))
    if field_mapping.get('SRVC_OVERLAP_MBR', 0) == 0:
        field_mapping['SRVC_OVERLAP_MBR'] = 1 * ((field_mapping['SRVC_FROM_DT'] <= field_mapping['MBR_CNTRCT_END_DT']) and (field_mapping['SRVC_TO_DT'] >= field_mapping['MBR_CNTRCT_EFCTV_DT']))
        field_mapping['SRVC_OVERLAP_MBR'] = field_mapping['SRVC_OVERLAP_MBR'] - field_mapping['SRVC_WITHIN_MBR']

    
    return field_mapping
