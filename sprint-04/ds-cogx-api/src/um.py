import pandas as pd
from scipy import sparse
from scipy.sparse import csr_matrix
import numpy as np
import sqlite3
import time
import copy
import traceback,sys
from fuzzywuzzy import fuzz
import os
os.environ['KMP_DUPLICATE_LIB_OK']='True'
import response
import requests
from pandas.io.json import json_normalize
import json
import constants
import hbase


def run(app, logger, extra, payload, payload_req):
    claim_line_data = pd.DataFrame.from_dict(payload, orient='columns')
    print(claim_line_data.memID)
    memID = claim_line_data.loc[:, ['memID']].iloc[0][0]
    print(memID)
    start_time = time.time()
    result_row = {}
    try:  
          
        output = json.loads(hbase.hbaseLookup(memID, constants.HBASE_UM_COLUMN, logger, extra))
        um_auth_data = pd.DataFrame(output['cogxUM'], dtype='object') #json_normalize(output['cogxUM'])
        um_auth_data.columns = map(str.upper, um_auth_data.columns)
        um_auth_data.rename(columns={'SRC_UM_PROV_ID_RP' : 'src_um_prov_id_rp','PROV_ID_RP' : 'prov_id_rp'}, inplace=True)
        ''' 
        conn = sqlite3.connect(constants.DB_PATH)
        um_auth_data = pd.read_sql_query('SELECT * FROM UM WHERE SRC_SBSCRBR_ID = ?', conn, params=(memID, ))
        '''
    except Exception as e:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=extra)
        return response.generate_desc(711, 'um', result_row, payload_req)

    logger.debug('Elapsed time', extra=response.extra_tags(extra, {'Reference Data Fetch': time.time() - start_time}, 0))

    if len(um_auth_data) == 0:
        #result_row['respCd'] = '701'
        #result_row['resDesc'] = 'UM Model is not executed'
        return response.generate_desc(711, 'um', result_row, payload_req)

    logger.debug('Number of UM rows found %d' % len(um_auth_data), extra=extra)
    final_out, score_feature_data, claim_merged = __UM_deploy__(um_auth_data, claim_line_data, app, logger, extra)
    
    if final_out is None and score_feature_data is None and claim_merged is None:
        return response.generate_desc(903, 'um', result_row, payload_req)
    start_time = time.time()
    # output result
    final_out_group = final_out.groupby(['KEY_CHK_DCN_NBR'], as_index=False).agg({'Top_1_UM_Score': np.max})
    claim_list = final_out_group[final_out.Top_1_UM_Score >= constants.UM_THRESHOLD]

    if claim_list.size > 0:
        claim_merged['SRC_PROV_NM'] = claim_merged['SRC_PROV_FRST_NM'] + ' ' + claim_merged['SRC_PROV_LAST_NM']
        claim_merged['SRC_PROV_NM_RP'] = claim_merged['SRC_PROV_FRST_NM_RP'] + ' ' + claim_merged['SRC_PROV_LAST_NM_RP']
        claim_merged_group = claim_merged.groupby(['KEY_CHK_DCN_NBR'], as_index=False).agg({'score': np.max})
        claim_list = claim_merged_group.KEY_CHK_DCN_NBR[claim_merged_group.score >= constants.UM_THRESHOLD]
        claim_merged = claim_merged.loc[claim_merged.KEY_CHK_DCN_NBR.isin(claim_list),:].reset_index(drop=True)
        claim_merged = claim_merged.sort_values(['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR', 'score', 'Tax_id_NA'], ascending=[True, True, True, False, True])
        claim_merged = claim_merged.drop_duplicates(subset=['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'DTL_LINE_NBR']).reset_index(drop=True)
        claim_merged['-'] = '-'
        claim_merged = claim_merged_transfer(claim_merged)
        claim_merged_output = claim_merged.loc[:,['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR','UM_CASE_NBR', 'RFRNC_NBR','score']]
        claim_merged_output = claim_merged_output.sort_values(['KEY_CHK_DCN_NBR','score'], ascending=[True, False])
        claim_merged_output = claim_merged_output.drop_duplicates(subset=['KEY_CHK_DCN_NBR']).reset_index(drop=True)
        claim_merged_output['Header_compare_title'] = "Claim/Case Header"
        claim_merged_output['auth_detail_title'] = "Auth data"
        claim_merged_output['claim_detail_title'] = "Claim Data"
        claim_merged_output['Header_compare'] = claim_merged_output.apply(__subset_header__, args=(claim_merged,), axis=1)
        claim_merged_output['auth_detail'] = claim_merged_output.apply(__auth_detail__, args=(claim_merged,), axis=1)
        claim_merged_output['claim_detail'] = claim_merged_output.apply(__claim_detail__, args=(claim_merged,), axis=1)
        claim_merged_output = claim_merged_output.loc[:,['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','Header_compare_title','Header_compare','auth_detail_title','auth_detail','claim_detail_title','claim_detail']]
        response.generate_desc(700, 'um', result_row, payload_req)
        #result_row['respCd'] = '700'
        #result_row['resDesc'] = 'UM Model Processed successfully'
        recommendations = []

        for idx in range(final_out.shape[0]):
            recommendation = {}
            recommendation['modelName'] = 'UM'
            recommendation['description'] = None
            recommendation['lineNumber'] = final_out['DTL_LINE_NBR'].iloc[idx]
            recommendation['actionCode'] = 'TAG'
            recommendation['actionValue'] = final_out['Top_1_UM'].iloc[idx]
            recommendation['modelScore'] = float(final_out['Top_1_UM_Score'].iloc[idx])
            recommendation['currentQueue'] = payload_req.get('CURNT_QUE_UNIT_NBR', None)
            recommendations.append(recommendation)
        result_row['recommendations'] = recommendations
        model_insights = []
        model_insight = {}
        model_insight['modelName'] = 'um'
        # model_insight['modelInsight'] =  claim_merged_output.to_json(orient="records")
        model_insight['modelInsight'] =  claim_merged_output.to_json()
        model_insights.append(model_insight)
        result_row['modelInsights'] = model_insights
    else:
        #result_row['respCd'] = '703'
        #result_row['resDesc'] = 'UM Model threshold not met'
        response.generate_desc(710, 'um', result_row, payload_req)
    logger.debug('Elapsed time',extra =response.extra_tags(extra, {'result_row building': time.time()-start_time},0))
    return result_row


def bubble_sort_df(df,col1, col2, col0):
    index_21 = (df[col1] > df[col2]) & (df[col2] != '')
    df.loc[index_21,col0] = df.loc[index_21,col1]
    df.loc[index_21,col1] = df.loc[index_21,col2]
    df.loc[index_21,col2] = df.loc[index_21,col0]
    return df


def claim_merged_transfer(claim_merged):
    claim_merged['UM_SRVC_STTS_CD_desc'] = claim_merged['UM_SRVC_STTS_CD'].str.replace(' ', '')
    UM_SRVC_STTS_CD = {'A':'Approved','N':'Denied','C':'Cancelled','P':'Pended','V':'Partial','O':'Other','UNK':'Unknown','NA':'NA','~NA':'Not Applicable','R':'Redirected','X':'Voided','J':'Jeopardy','~01':'~01'}
    for item in UM_SRVC_STTS_CD:
        claim_merged.loc[claim_merged['UM_SRVC_STTS_CD_desc'] == item,'UM_SRVC_STTS_CD_desc'] = UM_SRVC_STTS_CD[item]

    claim_merged['MDFR_CD_drop'] = '* '
    claim_merged['MDFR_CD_1'] = ''
    claim_merged['MDFR_CD_2'] = ''
    claim_merged['MDFR_CD_3'] = ''
    for cols in ['MDFR_1_CD','MDFR_2_CD','MDFR_3_CD','HCPCS_MDFR_CD','MEDCR_PROC_MDFR_CD','PROC_MDFR_CD']:
        claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged['MDFR_CD_1'] == ''),'MDFR_CD_1'] = claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged['MDFR_CD_1'] == ''),cols]
        claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols] != claim_merged['MDFR_CD_1']) & (claim_merged['MDFR_CD_2'] == ''),'MDFR_CD_2'] = claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols] != claim_merged['MDFR_CD_1']) & (claim_merged['MDFR_CD_2'] == ''),cols]
        claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols] != claim_merged['MDFR_CD_1']) & (claim_merged[cols] != claim_merged['MDFR_CD_2']) & (claim_merged['MDFR_CD_3'] == ''),'MDFR_CD_3'] = claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols] != claim_merged['MDFR_CD_1']) & (claim_merged[cols] != claim_merged['MDFR_CD_2']) & (claim_merged['MDFR_CD_3'] == ''),cols]

    claim_merged = bubble_sort_df(claim_merged,'MDFR_CD_1', 'MDFR_CD_2', 'MDFR_CD_drop')
    claim_merged = bubble_sort_df(claim_merged,'MDFR_CD_2', 'MDFR_CD_3', 'MDFR_CD_drop')
    claim_merged = bubble_sort_df(claim_merged,'MDFR_CD_1', 'MDFR_CD_2', 'MDFR_CD_drop')

    claim_merged['MDFR_CD_drop'] = '*'
    claim_merged['Procedure_Code'] = ''
    claim_merged['HCPCS_Code'] = ''
    claim_merged['Revenue_code'] = ''
    for cols in ['HCPCS_CD','MEDCR_PROC_CD','PROC_CD']:
        claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols].str.len()==5) & (~claim_merged[cols].str[0].str.isnumeric()),'HCPCS_Code'] = claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols].str.len()==5) & (~claim_merged[cols].str[0].str.isnumeric()),cols]
        claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols].str.len()==4) & (claim_merged[cols].str.isnumeric())& (claim_merged[cols].str[0] == '0'),'Revenue_code'] = claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols].str.len()==4) & (claim_merged[cols].str.isnumeric())& (claim_merged[cols].str[0] == '0'),cols]
        claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols] != claim_merged['HCPCS_Code']) & (claim_merged[cols] != claim_merged['Revenue_code']) & (claim_merged['Procedure_Code'] == ''),'Procedure_Code'] = claim_merged.loc[(claim_merged[cols] != claim_merged['MDFR_CD_drop']) & (claim_merged[cols] != claim_merged['HCPCS_Code']) & (claim_merged[cols] != claim_merged['Revenue_code']) & (claim_merged['Procedure_Code'] == ''),cols]
    return claim_merged


def __clean_raw_data__(raw_data, string_cols):
    for col in string_cols:
        raw_data[col] = raw_data[col].apply(str)
    return raw_data


# function for exact match
# ignore '*'
def __exact_match__(col_list1, col_list2, compare_table):
    output_array = np.zeros(compare_table.shape[0])
    for col_name1 in col_list1:
        compare_table[col_name1] = compare_table[col_name1].str.replace(' ', '')
        for col_name2 in col_list2:
            compare_table[col_name2] = compare_table[col_name2].str.replace(' ', '')
            output_array = output_array + np.array((compare_table[col_name1] == compare_table[col_name2]) & (compare_table[col_name1] != '*'))
    return (output_array > 0) * 1


# function for date compare of overlap, partial overlap, no overlap
def __date_match__(dateA1, dateA2, dateB1, dateB2, compare_table):
    no_overlap = np.array((compare_table[dateA1] > compare_table[dateB2]) | (compare_table[dateB1] > compare_table[dateA2]))
    complete_overlap = np.array(((compare_table[dateA1] >= compare_table[dateB1]) & (compare_table[dateA2] <= compare_table[dateB2])) | ((compare_table[dateB1] >= compare_table[dateA1]) & (compare_table[dateB2] <= compare_table[dateA2])))
    partial_overlap = np.array((~no_overlap) & (~complete_overlap))
    return (no_overlap) * 1, (complete_overlap) * 1, (partial_overlap) * 1


def __subset_header__(x, claim_merged):
    claim_merged = claim_merged[(claim_merged.KEY_CHK_DCN_NBR == x.KEY_CHK_DCN_NBR) & (claim_merged.DTL_LINE_NBR == x.DTL_LINE_NBR)].reset_index(drop=True)
    header_name = ['DCN',  'Provider TaxID', 'Provider NPI', 'Provider Medicare#', 'Provider Name', 'Provider Second Name', 'Member Code', 'From Date', 'To Date', 'Claim Type']
    header_claim = ['KEY_CHK_DCN_NBR', 'PROV_TAX_ID', 'BILLG_NPI', '-', 'PROV_NM','PROV_SCNDRY_NM', 'PAT_MBR_CD', 'clm_from_dt', 'clm_to_dt', 'CLAIM_TYPE']
    header_compare = {'Field': header_name,
                        'Claim': np.array(claim_merged[header_claim])[0]
                    }
    header_compare = pd.DataFrame(header_compare)
    return header_compare


def __auth_detail__(x, claim_merged):
    detail_compare = claim_merged.loc[claim_merged.KEY_CHK_DCN_NBR == x.KEY_CHK_DCN_NBR,
                                      ['RFRNC_NBR', 'UM_SRVC_STTS_CD_desc','UM_RQRD_IND', 'RQSTD_PROC_SRVC_CD', 'AUTHRZD_PROC_SRVC_CD', 
                                       'SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'SRC_PROV_NM', 'SRC_PROV_NM_RP',
                                       'AUTHRZD_SRVC_FROM_DT', 'AUTHRZD_SRVC_TO_DT', 'RQSTD_SRVC_FROM_DT', 'RQSTD_SRVC_TO_DT', 
                                       'AUTHRZD_QTY']].rename(columns={'RFRNC_NBR': 'UM Case Number',
                                                                       'SRC_UM_PROV_ID':'Provider TaxID', 
                                                                       'src_um_prov_id_rp':'Provider NPI', 
                                                                       'PROV_ID':'Provider Medicare#', 
                                                                       'SRC_PROV_NM':'Provider Name', 
                                                                       'SRC_PROV_NM_RP':'Provider Second Name',
                                                                       'RQSTD_PROC_SRVC_CD':'Procedure Code', 
                                                                       'AUTHRZD_PROC_SRVC_CD':'HCPCS Code', 
                                                                       'AUTHRZD_SRVC_FROM_DT':'Authorized From Date', 
                                                                       'AUTHRZD_SRVC_TO_DT':'Authorized To Date', 
                                                                       'RQSTD_SRVC_FROM_DT':'Requested From Date', 
                                                                       'RQSTD_SRVC_TO_DT':'Requested To Date', 
                                                                       'AUTHRZD_QTY':'Units', 
                                                                       'UM_SRVC_STTS_CD_desc':'UM Case Status'})
    detail_compare = detail_compare.loc[detail_compare.UM_RQRD_IND != 'N',:].reset_index(drop=True)
    detail_compare = detail_compare.drop(columns=['UM_RQRD_IND'])
    detail_compare = detail_compare.drop_duplicates()
    return detail_compare


def __claim_detail__(x, claim_merged):
    detail_compare = claim_merged.loc[claim_merged.KEY_CHK_DCN_NBR == x.KEY_CHK_DCN_NBR,
                                      ['DTL_LINE_NBR','UM_RQRD_IND','RFRNC_NBR','UM_SRVC_STTS_CD_desc', 'Procedure_Code','HCPCS_Code','Revenue_code', 
                                       'dtl_from_dt', 'dtl_to_dt', 'BILLD_SRVC_UNIT_QTY','MDFR_CD_1','MDFR_CD_2','MDFR_CD_3'
                                       ]].rename(columns={'DTL_LINE_NBR': 'Line #','RFRNC_NBR': 'UM Case Number', 
                                                         'Procedure_Code':'Procedure Code','HCPCS_Code':'HCPCS Code',
                                                         'Revenue_code':'Revenue code', 'dtl_from_dt':'From Date', 
                                                         'dtl_to_dt':'To Date', 'BILLD_SRVC_UNIT_QTY':'Units',
                                                         'MDFR_CD_1':'Modifier Code 1','MDFR_CD_2':'Modifier Code 2',
                                                         'MDFR_CD_3':'Modifier Code 3','UM_RQRD_IND':'UM Required Indicator', 
                                                         'UM_SRVC_STTS_CD_desc':'UM Case Status'})
    detail_compare = detail_compare.loc[detail_compare['UM Required Indicator'] != 'N',:]

    detail_compare = detail_compare.drop_duplicates()
    return detail_compare


# function for column contain column
# ignore '*'
#@timing
def __contain_match__(col_list1, col_list2, compare_table):
    output_array = np.zeros(compare_table.shape[0])
    for col_name1 in col_list1:
        if pd.isna(compare_table[col_name1].str.len().unique())[0]:
            continue
        compare_table[col_name1] = compare_table[col_name1].str.replace(' ', '')
        for col_name2 in col_list2: #np.array(col_locator['VAR'].str.startswith("PROV_TAX_ID"))
            if pd.isna(compare_table[col_name2].str.len().unique()[0]):
                continue
            compare_table[col_name2] = compare_table[col_name2].str.replace(' ', '')
            output_array = output_array + np.array([(x[0] in x[1] and x[0] != '*' and x[1] != '*') for x in zip(compare_table[col_name1], compare_table[col_name2])])
    return (output_array > 0) * 1


def __UM_deploy__(um_auth_data, claim_line_data, app, logger, extra):
    start_time = time.time()
    um_auth_data = um_auth_data.loc[:, ['SRC_SBSCRBR_ID', 'RFRNC_NBR', 'SRC_MBR_CD', 'RQSTD_PROC_SRVC_CD', 'AUTHRZD_PROC_SRVC_CD', 'RQSTD_SRVC_FROM_DT', 'RQSTD_SRVC_TO_DT', 'AUTHRZD_SRVC_FROM_DT', 'AUTHRZD_SRVC_TO_DT', 'SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'prov_id_rp', 'RQSTD_QTY', 'AUTHRZD_QTY', 'SRC_PROV_FRST_NM_RP','SRC_PROV_LAST_NM_RP', 'SRC_PROV_FRST_NM', 'SRC_PROV_LAST_NM','CLNCL_SOR_CD', 'MBRSHP_SOR_CD','UM_SRVC_STTS_CD']]
    um_auth_data = um_auth_data.drop_duplicates().reset_index(drop=True)

    claim_line_data = claim_line_data.loc[:, ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'action_code', 'DTL_LINE_NBR', 'UM_CASE_NBR', 'memID', 'PROV_NM', 'PROV_SCNDRY_NM', 'PAT_MBR_CD', 'PROC_CD', 'HCPCS_CD', 'clm_from_dt', 'clm_to_dt', 'dtl_from_dt', 'dtl_to_dt', 'PROV_TAX_ID', 'BILLD_SRVC_UNIT_QTY', 'CLAIM_TYPE', 'BILLG_NPI', 'TOS_TYPE_CD','MEDCR_PROC_CD','HCPCS_MDFR_CD','MDFR_1_CD', 'MDFR_2_CD', 'MDFR_3_CD', 'MEDCR_PROC_MDFR_CD','PROC_MDFR_CD','UM_RQRD_IND']]
    claim_line_data = claim_line_data.drop_duplicates().reset_index(drop=True)

    um_auth_data = um_auth_data.fillna('*')
    claim_line_data = claim_line_data.fillna('*')

    um_auth_data = __clean_raw_data__(um_auth_data, ['SRC_SBSCRBR_ID', 'RFRNC_NBR', 'SRC_MBR_CD', 'RQSTD_PROC_SRVC_CD', 'AUTHRZD_PROC_SRVC_CD', 'RQSTD_SRVC_FROM_DT', 'RQSTD_SRVC_TO_DT', 'AUTHRZD_SRVC_FROM_DT', 'AUTHRZD_SRVC_TO_DT', 'SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'prov_id_rp', 'RQSTD_QTY', 'AUTHRZD_QTY', 'SRC_PROV_FRST_NM_RP','SRC_PROV_LAST_NM_RP', 'SRC_PROV_FRST_NM', 'SRC_PROV_LAST_NM','CLNCL_SOR_CD', 'MBRSHP_SOR_CD','UM_SRVC_STTS_CD'])
    claim_line_data = __clean_raw_data__(claim_line_data, ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'action_code', 'DTL_LINE_NBR', 'UM_CASE_NBR', 'memID', 'PROV_NM', 'PROV_SCNDRY_NM', 'PAT_MBR_CD', 'PROC_CD', 'HCPCS_CD', 'clm_from_dt', 'clm_to_dt', 'dtl_from_dt', 'dtl_to_dt', 'PROV_TAX_ID', 'BILLD_SRVC_UNIT_QTY', 'CLAIM_TYPE', 'BILLG_NPI', 'TOS_TYPE_CD','MEDCR_PROC_CD','HCPCS_MDFR_CD','MDFR_1_CD', 'MDFR_2_CD', 'MDFR_3_CD', 'MEDCR_PROC_MDFR_CD','PROC_MDFR_CD','UM_RQRD_IND'])

    # merge claim line data and csv
    claim_merged = claim_line_data.merge(um_auth_data, how='inner', left_on='memID', right_on='SRC_SBSCRBR_ID')

    # print(claim_merged.columns)
    logger.debug('no of claim_merged rows %d' % len(claim_merged), extra=extra)

    # prepare output table
    matching_result = claim_merged.loc[:, ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'DTL_LINE_NBR', 'UM_CASE_NBR', 'RFRNC_NBR']]

    # member code exact match
    col_list1 = ['PAT_MBR_CD']
    col_list2 = ['SRC_MBR_CD']
    matching_result.loc[:, 'Member_code_exact'] = __exact_match__(col_list1, col_list2, claim_merged)

    # proc code exact match
    col_list1 = ['PROC_CD', 'HCPCS_CD', 'MEDCR_PROC_CD']
    col_list2 = ['RQSTD_PROC_SRVC_CD', 'AUTHRZD_PROC_SRVC_CD']
    matching_result.loc[:, 'Proc_code_exact'] = __exact_match__(col_list1, col_list2, claim_merged)

    # date format change
    claim_merged['clm_from_dt2'] = pd.to_datetime(claim_merged['clm_from_dt'], format='%m/%d/%Y', errors='coerce')
    claim_merged['clm_to_dt2'] = pd.to_datetime(claim_merged['clm_to_dt'], format='%m/%d/%Y', errors='coerce')
    claim_merged['dtl_from_dt2'] = pd.to_datetime(claim_merged['dtl_from_dt'], format='%m/%d/%Y', errors='coerce')
    claim_merged['dtl_to_dt2'] = pd.to_datetime(claim_merged['dtl_to_dt'], format='%m/%d/%Y', errors='coerce')
    claim_merged['RQSTD_SRVC_FROM_DT2'] = claim_merged['RQSTD_SRVC_FROM_DT']
    claim_merged['RQSTD_SRVC_TO_DT2'] = claim_merged['RQSTD_SRVC_TO_DT']
    claim_merged['AUTHRZD_SRVC_FROM_DT2'] = claim_merged['AUTHRZD_SRVC_FROM_DT']
    claim_merged['AUTHRZD_SRVC_TO_DT2'] = claim_merged['AUTHRZD_SRVC_TO_DT']
    if '/' in claim_merged['RQSTD_SRVC_FROM_DT'][0]:
        claim_merged.loc[claim_merged['RQSTD_SRVC_FROM_DT2'].str[-4:]<'1900', 'RQSTD_SRVC_FROM_DT2'] = '1/1/1900'
        claim_merged.loc[claim_merged['RQSTD_SRVC_FROM_DT2'].str[-4:]>'2100', 'RQSTD_SRVC_FROM_DT2'] = '12/31/2100'
        claim_merged.loc[claim_merged['RQSTD_SRVC_TO_DT2'].str[-4:]<'1900', 'RQSTD_SRVC_TO_DT2'] = '1/1/1900'
        claim_merged.loc[claim_merged['RQSTD_SRVC_TO_DT2'].str[-4:]>'2100', 'RQSTD_SRVC_TO_DT2'] = '12/31/2100'
        claim_merged.loc[claim_merged['AUTHRZD_SRVC_FROM_DT2'].str[-4:]<'1900', 'AUTHRZD_SRVC_FROM_DT2'] = '1/1/1900'
        claim_merged.loc[claim_merged['AUTHRZD_SRVC_FROM_DT2'].str[-4:]>'2100', 'AUTHRZD_SRVC_FROM_DT2'] = '12/31/2100'
        claim_merged.loc[claim_merged['AUTHRZD_SRVC_TO_DT2'].str[-4:]<'1900', 'AUTHRZD_SRVC_TO_DT2'] = '1/1/1900'
        claim_merged.loc[claim_merged['AUTHRZD_SRVC_TO_DT2'].str[-4:]>'2100', 'AUTHRZD_SRVC_TO_DT2'] = '12/31/2100'
        claim_merged['RQSTD_SRVC_FROM_DT2'] = pd.to_datetime(claim_merged['RQSTD_SRVC_FROM_DT'], format='%m/%d/%Y', errors='coerce')
        claim_merged['RQSTD_SRVC_TO_DT2'] = pd.to_datetime(claim_merged['RQSTD_SRVC_TO_DT'], format='%m/%d/%Y', errors='coerce')
        claim_merged['AUTHRZD_SRVC_FROM_DT2'] = pd.to_datetime(claim_merged['AUTHRZD_SRVC_FROM_DT'], format='%m/%d/%Y', errors='coerce')
        claim_merged['AUTHRZD_SRVC_TO_DT2'] = pd.to_datetime(claim_merged['AUTHRZD_SRVC_TO_DT'], format='%m/%d/%Y', errors='coerce')
    elif '-' in claim_merged['RQSTD_SRVC_FROM_DT'][0]:
        claim_merged.loc[claim_merged['RQSTD_SRVC_FROM_DT2']<'1900-01-01', 'RQSTD_SRVC_FROM_DT2'] = '1900-01-01'
        claim_merged.loc[claim_merged['RQSTD_SRVC_FROM_DT2']>'2100-12-31', 'RQSTD_SRVC_FROM_DT2'] = '2100-12-31'
        claim_merged.loc[claim_merged['RQSTD_SRVC_TO_DT2']<'1900-01-01', 'RQSTD_SRVC_TO_DT2'] = '1900-01-01'
        claim_merged.loc[claim_merged['RQSTD_SRVC_TO_DT2']>'2100-12-31', 'RQSTD_SRVC_TO_DT2'] = '2100-12-31'
        claim_merged.loc[claim_merged['AUTHRZD_SRVC_FROM_DT2']<'1900-01-01', 'AUTHRZD_SRVC_FROM_DT2'] = '1900-01-01'
        claim_merged.loc[claim_merged['AUTHRZD_SRVC_FROM_DT2']>'2100-12-31', 'AUTHRZD_SRVC_FROM_DT2'] = '2100-12-31'
        claim_merged.loc[claim_merged['AUTHRZD_SRVC_TO_DT2']<'1900-01-01', 'AUTHRZD_SRVC_TO_DT2'] = '1900-01-01'
        claim_merged.loc[claim_merged['AUTHRZD_SRVC_TO_DT2']>'2100-12-31', 'AUTHRZD_SRVC_TO_DT2'] = '2100-12-31'
        claim_merged['RQSTD_SRVC_FROM_DT2'] = pd.to_datetime(claim_merged['RQSTD_SRVC_FROM_DT2'], format='%Y-%m-%d', errors='coerce')
        claim_merged['RQSTD_SRVC_TO_DT2'] = pd.to_datetime(claim_merged['RQSTD_SRVC_TO_DT2'], format='%Y-%m-%d', errors='coerce')
        claim_merged['AUTHRZD_SRVC_FROM_DT2'] = pd.to_datetime(claim_merged['AUTHRZD_SRVC_FROM_DT2'], format='%Y-%m-%d', errors='coerce')
        claim_merged['AUTHRZD_SRVC_TO_DT2'] = pd.to_datetime(claim_merged['AUTHRZD_SRVC_TO_DT2'], format='%Y-%m-%d', errors='coerce')
    claim_merged.loc[pd.isnull(claim_merged.loc[:, 'AUTHRZD_SRVC_FROM_DT2']), 'AUTHRZD_SRVC_FROM_DT2'] = claim_merged.loc[pd.isnull(claim_merged.loc[:, 'AUTHRZD_SRVC_FROM_DT2']), 'RQSTD_SRVC_FROM_DT2']
    claim_merged.loc[pd.isnull(claim_merged.loc[:, 'AUTHRZD_SRVC_TO_DT2']), 'AUTHRZD_SRVC_TO_DT2'] = claim_merged.loc[pd.isnull(claim_merged.loc[:, 'AUTHRZD_SRVC_TO_DT2']), 'RQSTD_SRVC_TO_DT2']

    # dtl_from_dt - AUTHRZD_SRVC_FROM_DT difference
    matching_result.loc[:, 'dtl_AUTHRZD_from_dt'] = (claim_merged['dtl_from_dt2'] - claim_merged['AUTHRZD_SRVC_FROM_DT2']).dt.days
    # AUTHRZD_SRVC_TO_DT - dtl_to_dt difference
    matching_result.loc[:, 'AUTHRZD_dtl_to_dt'] = (claim_merged['AUTHRZD_SRVC_TO_DT2'] - claim_merged['dtl_to_dt2']).dt.days
    # clm_from_dt - AUTHRZD_SRVC_FROM_DT difference
    matching_result.loc[:, 'clm_AUTHRZD_from_dt'] = (claim_merged['clm_from_dt2'] - claim_merged['AUTHRZD_SRVC_FROM_DT2']).dt.days
    # AUTHRZD_SRVC_TO_DT - clm_to_dt difference
    matching_result.loc[:, 'AUTHRZD_clm_to_dt'] = (claim_merged['AUTHRZD_SRVC_TO_DT2'] - claim_merged['clm_to_dt2']).dt.days
    # dtl_from_dt - RQSTD_SRVC_FROM_DT difference
    matching_result.loc[:, 'dtl_RQSTD_from_dt'] = (claim_merged['dtl_from_dt2'] - claim_merged['RQSTD_SRVC_FROM_DT2']).dt.days
    # RQSTD_SRVC_TO_DT - dtl_to_dt difference
    matching_result.loc[:, 'RQSTD_dtl_to_dt'] = (claim_merged['RQSTD_SRVC_TO_DT2'] - claim_merged['dtl_to_dt2']).dt.days
    # clm_from_dt - RQSTD_SRVC_FROM_DT difference
    matching_result.loc[:, 'clm_RQSTD_from_dt'] = (claim_merged['clm_from_dt2'] - claim_merged['RQSTD_SRVC_FROM_DT2']).dt.days
    # RQSTD_SRVC_TO_DT - clm_to_dt difference
    matching_result.loc[:, 'RQSTD_clm_to_dt'] = (claim_merged['RQSTD_SRVC_TO_DT2'] - claim_merged['clm_to_dt2']).dt.days
    matching_result.loc[:,['dtl_AUTHRZD_from_dt','AUTHRZD_dtl_to_dt','clm_AUTHRZD_from_dt','AUTHRZD_clm_to_dt','dtl_RQSTD_from_dt','RQSTD_dtl_to_dt','clm_RQSTD_from_dt','RQSTD_clm_to_dt']] = matching_result.loc[:,['dtl_AUTHRZD_from_dt','AUTHRZD_dtl_to_dt','clm_AUTHRZD_from_dt','AUTHRZD_clm_to_dt','dtl_RQSTD_from_dt','RQSTD_dtl_to_dt','clm_RQSTD_from_dt','RQSTD_clm_to_dt']].fillna(-365)
    matching_result.loc[:,['dtl_AUTHRZD_from_dt','AUTHRZD_dtl_to_dt','clm_AUTHRZD_from_dt','AUTHRZD_clm_to_dt','dtl_RQSTD_from_dt','RQSTD_dtl_to_dt','clm_RQSTD_from_dt','RQSTD_clm_to_dt']] = matching_result.loc[:,['dtl_AUTHRZD_from_dt','AUTHRZD_dtl_to_dt','clm_AUTHRZD_from_dt','AUTHRZD_clm_to_dt','dtl_RQSTD_from_dt','RQSTD_dtl_to_dt','clm_RQSTD_from_dt','RQSTD_clm_to_dt']].clip(lower=-365, upper=365)

    # tax id contain match
    col_list1 = ['PROV_TAX_ID', 'BILLG_NPI']
    col_list2 = ['SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'prov_id_rp']
    matching_result.loc[:, 'Tax_id_contain'] = __exact_match__(col_list1, col_list2, claim_merged)
    matching_result.loc[claim_merged.SRC_UM_PROV_ID.str[:9] == claim_merged.PROV_TAX_ID, 'Tax_id_contain'] = 1
    matching_result.loc[claim_merged.src_um_prov_id_rp.str[:9] == claim_merged.PROV_TAX_ID, 'Tax_id_contain'] = 1
    matching_result.loc[claim_merged.SRC_UM_PROV_ID.str[-13:-4] == claim_merged.PROV_TAX_ID, 'Tax_id_contain'] = 1
    matching_result.loc[claim_merged.src_um_prov_id_rp.str[-13:-4] == claim_merged.PROV_TAX_ID, 'Tax_id_contain'] = 1
    matching_result.loc[claim_merged.SRC_UM_PROV_ID.str[-12:-3] == claim_merged.PROV_TAX_ID, 'Tax_id_contain'] = 1

    # PROV_NM contain match
    claim_merged['PROV_NM2'] = ' ' + claim_merged['PROV_NM'] + ' '
    index_SRC = (claim_merged['PROV_NM'] != claim_merged['PROV_SCNDRY_NM'])
    claim_merged.loc[index_SRC,'PROV_NM2'] = claim_merged.loc[index_SRC,'PROV_NM2'] + claim_merged.loc[index_SRC,'PROV_SCNDRY_NM'] + ' '
    claim_merged['SRC_PROV_NM2'] = ' ' + claim_merged['SRC_PROV_FRST_NM'] + ' '
    index_SRC = claim_merged['SRC_PROV_LAST_NM'] != claim_merged['SRC_PROV_FRST_NM']
    claim_merged.loc[index_SRC,'SRC_PROV_NM2'] = claim_merged.loc[index_SRC,'SRC_PROV_NM2'] + claim_merged.loc[index_SRC,'SRC_PROV_LAST_NM'] + ' '
    index_SRC = (claim_merged['SRC_PROV_FRST_NM_RP'] != claim_merged['SRC_PROV_FRST_NM']) & (claim_merged['SRC_PROV_FRST_NM_RP'] != claim_merged['SRC_PROV_LAST_NM'])
    claim_merged.loc[index_SRC,'SRC_PROV_NM2'] = claim_merged.loc[index_SRC,'SRC_PROV_NM2'] + claim_merged.loc[index_SRC,'SRC_PROV_FRST_NM_RP'] + ' '
    index_SRC = (claim_merged['SRC_PROV_LAST_NM_RP'] != claim_merged['SRC_PROV_FRST_NM']) & (claim_merged['SRC_PROV_LAST_NM_RP'] != claim_merged['SRC_PROV_LAST_NM']) & (claim_merged['SRC_PROV_LAST_NM_RP'] != claim_merged['SRC_PROV_FRST_NM_RP'])
    claim_merged.loc[index_SRC,'SRC_PROV_NM2'] = claim_merged.loc[index_SRC,'SRC_PROV_NM2'] + claim_merged.loc[index_SRC,'SRC_PROV_LAST_NM_RP'] + ' '
    text_list = [' association ',' care ',' center ',' clinics ',' enterprises ',' group ',' inc ',' ins ',' institute ',' health ',' hospital ',' hospitalist ',' medical ']
    for text in text_list:
        claim_merged['PROV_NM2'] = claim_merged['PROV_NM2'].str.replace(text,' ')
        claim_merged['SRC_PROV_NM2'] = claim_merged['SRC_PROV_NM2'].str.replace(text,' ')
    claim_merged_sub = claim_merged.loc[:,['PROV_NM2','SRC_PROV_NM2']].drop_duplicates().reset_index(drop=True)
    claim_merged_sub['PROV_NM_contain']=claim_merged_sub.apply(lambda x:fuzz.token_sort_ratio(x["PROV_NM2"],x["SRC_PROV_NM2"]),axis=1)
    claim_merged = claim_merged.reset_index().merge(claim_merged_sub, how='inner', on=['PROV_NM2','SRC_PROV_NM2']).sort_values(by='index').reset_index(drop=True)
    matching_result.loc[:, 'PROV_NM_contain'] = claim_merged.loc[:, 'PROV_NM_contain']
    matching_result['PROV_NM_contain'] = (matching_result['PROV_NM_contain']/25).round(0)/4

    # tax id is NA
    tax_id_NA_list = ['*','UNK','999999999999','UMSHELLPRAC','UMSHELLFAC','Unknown','############','999999999','UUUUU1','NA']
    col_list2 = ['SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'prov_id_rp']
    for col_name2 in col_list2:
        claim_merged[col_name2+'2'] = claim_merged[col_name2]
        claim_merged.loc[claim_merged[col_name2+'2']=="", col_name2+'2'] = 'UNK'
        for tax_id_NA_item in tax_id_NA_list:
            claim_merged[col_name2+'2'] = claim_merged[col_name2+'2'].str.replace(tax_id_NA_item, 'UNK')
    matching_result.loc[:, 'Tax_id_NA'] = np.array(claim_merged.SRC_UM_PROV_ID2.str.startswith('UNK') & claim_merged.src_um_prov_id_rp2.str.startswith('UNK') & claim_merged.PROV_ID2.str.startswith('UNK') & claim_merged.prov_id_rp2.str.startswith('UNK')) * 1

    # TOS_TYPE_CD
    TOS_TYPE_CD_list = ['OPH','PHT','DXL','PSY','SUP','HHC','RAB','SRG','PHO','DME','SPC','RAD','MWH','PRV','HSP','CHE','PAN','AMB','SPT','PAS','SNF','AIR','ERP','CON','DEN']
    for TOS_TYPE_CD_item in TOS_TYPE_CD_list:
        matching_result.loc[:, 'TOS_TYPE_CD_'+TOS_TYPE_CD_item] = np.array(claim_merged['TOS_TYPE_CD']==TOS_TYPE_CD_item) * 1

    # qty format change
    claim_merged['BILLD_SRVC_UNIT_QTY2'] = pd.to_numeric(claim_merged['BILLD_SRVC_UNIT_QTY'], downcast='float', errors='coerce')
    claim_merged['RQSTD_QTY2'] = pd.to_numeric(claim_merged['RQSTD_QTY'], downcast='float', errors='coerce')
    claim_merged['AUTHRZD_QTY2'] = pd.to_numeric(claim_merged['AUTHRZD_QTY'], downcast='float', errors='coerce')
    # qty difference
    matching_result.loc[:, 'Qty_diff_RQSTD'] = claim_merged['RQSTD_QTY2'] - claim_merged['BILLD_SRVC_UNIT_QTY2']
    matching_result.loc[:, 'Qty_diff_AUTHRZD'] = claim_merged['AUTHRZD_QTY2'] - claim_merged['BILLD_SRVC_UNIT_QTY2']

    # claim_type variable
    matching_result.loc[:, 'CLAIM_TYPE_INPT'] = (claim_merged['CLAIM_TYPE'] == 'INPT') * 1
    matching_result.loc[:, 'CLAIM_TYPE_PROF'] = (claim_merged['CLAIM_TYPE'] == 'PROF') * 1
    matching_result.loc[:, 'CLAIM_TYPE_SN'] = (claim_merged['CLAIM_TYPE'] == 'SN') * 1
    matching_result.loc[:, 'CLAIM_TYPE_OUTPT'] = (claim_merged['CLAIM_TYPE'] == 'OUTPT') * 1

    # CLNCL_SOR_CD variable
    claim_merged['CLNCL_SOR_CD2'] = claim_merged['CLNCL_SOR_CD'].str.replace(' ','')
    loop_list = ['873','1036','872','870','948','1103','1035']
    for item in loop_list:
        matching_result.loc[:, 'CLNCL_SOR_CD_' + item] = (claim_merged['CLNCL_SOR_CD2'] == item) * 1
    
    # MBRSHP_SOR_CD variable
    claim_merged['MBRSHP_SOR_CD2'] = claim_merged['MBRSHP_SOR_CD'].str.replace(' ','')
    loop_list = ['808','815','809','886','867','824','868','823','199','202','889','5152'] # dropped 0 and UNK
    for item in loop_list:
        matching_result.loc[:, 'MBRSHP_SOR_CD_' + item] = (claim_merged['MBRSHP_SOR_CD2'] == item) * 1
    
    # UM_SRVC_STTS_CD variable
    claim_merged['UM_SRVC_STTS_CD2'] = claim_merged['UM_SRVC_STTS_CD'].str.replace(' ','')
    loop_list = ['A','C','N','O','P','V','J','R','X'] # dropped UNK, NA, NaN, ~01
    for item in loop_list:
        matching_result.loc[:, 'UM_SRVC_STTS_CD_' + item] = (claim_merged['UM_SRVC_STTS_CD2'] == item) * 1

    # output
    Y_col_list = ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'DTL_LINE_NBR', 'UM_CASE_NBR','RFRNC_NBR']
    Y_data = matching_result.loc[:, Y_col_list]
    X_data = matching_result.drop(columns=Y_col_list)
    col_locator = pd.DataFrame({'VAR':pd.Series(X_data.columns.values)})
    col_locator['col'] = range(X_data.shape[1])
    X_data = sparse.csr_matrix(X_data.values)
    #logger.debug('Create model data took %d seconds' % (time.time() - start_time), extra=extra)
    logger.debug('Elapsed time', extra=response.extra_tags(extra, {'Prepare_model_data': time.time() - start_time}, 0))
    start_time = time.time()

    m = X_data.shape[0]
    X_2 = X_data[np.array(range(0,m)),:]
    
    '''
    for idx in range(X_2.shape[0]):
        print(idx)
        arr = X_2[idx].toarray()
        print('################## ')
        for i, val in enumerate(arr[0]):
            if val != 0:
                print('(0,' + str(i) + ')\t' + str(val))
    
    with open("/Users/Af81392/git2/ds-cogx-api/um_report.txt", "+a") as f:
        f.write("################## " + str(matching_result["KEY_CHK_DCN_NBR"].iloc[0]) + "\n")
        for idx in range(X_2.shape[0]):
            arr = X_2[idx].toarray()
            #print('################## ')
            for i, val in enumerate(arr[0]):
                if val != 0:
                    f.write('(0,' + str(i) + ')\t' + str(val)+"\n")
        f.write("\n################## " + str(matching_result["KEY_CHK_DCN_NBR"].iloc[0])+"\n")
    ''' 
    try:
        usecases = app.config['usecases']
        models = []
        test_row_1 = {}
        for usecase in usecases:
            if (usecase['name'] == 'um'):
                models = usecase['models']
                break

        for model in models:
            binary_model = copy.deepcopy(model['binary'])
            pred_test = binary_model.predict_proba(X_2)[:, -1]
            Y_data['score'] = pred_test
            del binary_model
    except Exception as e:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=extra)
        return None,None,None

    #logger.debug('Prediction took %d seconds' % (time.time() - start_time), extra=extra)
    logger.debug('Elapsed time', extra=response.extra_tags(extra, {'model_execution': time.time() - start_time}, 0))
    # UM output
    start_time = time.time()
    Y_data_135 = Y_data.groupby(['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'DTL_LINE_NBR', 'UM_CASE_NBR', 'RFRNC_NBR'], as_index=False)['score'].max()
    Y_data_135 = Y_data_135.sort_values(['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR', 'score'], ascending=[True, True, True, False])
    Y_data_135['idx'] = Y_data_135.groupby(['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR']).cumcount()
    final_out = Y_data_135.groupby(['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR','UM_CASE_NBR'], as_index=False).agg({'idx': np.max}).rename(
        columns={'idx': 'num_potential_ums'})
    final_out['num_potential_ums'] = final_out['num_potential_ums'] + 1
    for i in range(3):
        idx = i + 1
        tmp = Y_data_135[Y_data_135['idx'] == i][['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR','RFRNC_NBR', 'score']].rename(
            columns={'RFRNC_NBR': 'Top_' + str(idx) + '_UM', 'score': 'Top_' + str(idx) + '_UM_Score'})
        final_out = pd.merge(final_out, tmp, how='left', on=['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR'])
    feature_data = pd.DataFrame(X_data.toarray(),columns=col_locator.VAR)
    feature_data = feature_data.reset_index(drop=True)
    score_feature_data = pd.concat([Y_data,feature_data],axis=1,join='inner')
    claim_merged['score'] = pred_test
    claim_merged['Tax_id_NA'] = matching_result['Tax_id_NA']
    # claim_merged.to_csv('claim_merged.csv',index=False)
    # final_out.to_csv('final_out.csv',index=False)
    # score_feature_data.to_csv('score_feature_data.csv',index=False)
    #logger.debug('Output result took %d seconds' % (time.time() - start_time), extra=extra)
    logger.debug('Elapsed time', extra=response.extra_tags(extra, {'Model_Result': time.time() - start_time}, 0))

    # print(matching_result)
    return final_out, score_feature_data, claim_merged

    
