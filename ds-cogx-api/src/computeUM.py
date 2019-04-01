import constants as const
import pandas as pd
import copy
from scipy import sparse
from scipy.sparse import csr_matrix
import numpy as np
import sqlite3
import time


from computeBase import computeBase


class computeUM(computeBase):

    def __init__(self, app, logger, extra):
        super().__init__(app, logger,extra)


    def transform(self, payload, model_mapping):
        return super().transform(payload, model_mapping)
        #return self.UM_transform(obj,payload)


    def validate(self, payload, validation_schema):
        return super().validate(payload, validation_schema)


    def process_request(self, payload):
        result_row = {}
        result_row['KEY_CHK_DCN_NBR'] = payload.get('KEY_CHK_DCN_NBR', None)
        result_row['CURNT_QUE_UNIT_NBR'] = payload.get('CURNT_QUE_UNIT_NBR', None)


        self.validate(payload, 'um_validation')
        if len(self.errors) > 0:
            # TODO boilerplate code need to move to base class, also not ideal to send messages to calling program
            self.logger.debug("Valid payload", extra=self.extra)
            result_row['respCd'] = '100'
            result_row['resDesc'] = 'Invalid data sent'
            result_row['recommendationsPresent'] = False
            messages = self.generate_messages()
            result_row['messages'] = messages
            return result_row

        '''
        else:
            
            ##TODO: Add logic to return with the errors list to client ...where we need to add this?
            #Is self.errors field will check at the end? and give response accordingly
            self.logger.error("_UM_VALIDATION_ not return true for payload : ", extra=self.extra_tags(payload))
            
            
            return result_row
        '''

        claimDetails = payload.get('claimDetails', [])
        _um_transformed_payloads = []
        for claimDetail in claimDetails:
            payload['claimDetails'] = [claimDetail]
            _um_transformed_payloads.append(self.transform(payload, 'um_mapping'))
        
        #TODO : Need to add calling of the Um_DerivedFields() to add um specific derived fields.
        # UMtransformed_payload = UM_DerivedFields(UMtransformed_payload['um_claim'],payload)

        _um_derived_payloads = []
        for _um_transformed_payload in _um_transformed_payloads:
            _um_derived_payloads.append((self.Um_DerivedFields(_um_transformed_payload['um_claim'], payload)))

        self.logger.debug("Transformed", extra=self.extra_tags({'um_transformed': _um_derived_payloads}))

        start_time = time.time()
        claim_line_data = pd.DataFrame.from_dict(_um_derived_payloads, orient='columns')
        conn = sqlite3.connect(const.DB_PATH)
        memID = claim_line_data.loc[:, ['memID'] ].iloc[0][0]
        um_auth_data = pd.read_sql_query('SELECT * FROM UM WHERE SRC_SBSCRBR_ID = ?', conn, params=(memID, ))
        # print(um_auth_data)
        self.logger.debug('Number of UM rows found %d' % len(um_auth_data), extra=self.extra)
        final_out, score_feature_data, claim_merged = self.__UM_deploy__(um_auth_data, claim_line_data)

        result_row['respCd'] = '200'
        result_row['resDesc'] = 'UM Model Processed successfully'
        result_row['recommendationsPresent'] = True
        recommendations = []
        recommendation = {}
        recommendation['modelName'] = 'um'
        recommendation['actionCode'] = final_out['Top_1_UM'].iloc[0]
        recommendation['actionValue'] = final_out['Top_1_UM_Score'].iloc[0]
        recommendations.append(recommendation)
        result_row['recommendations'] = recommendations
        claim_merged = claim_merged.sort_values(['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR', 'score', 'Tax_id_NA'], ascending=[True, True, True, False, True])
        claim_merged = claim_merged.drop_duplicates(subset=['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'DTL_LINE_NBR']).reset_index(drop=True)
        claim_merged['-'] = '-'
        # claim_merged = claim_merged.loc[claim_merged.score >= threshold,:].reset_index(drop=True)
        claim_merged_output = claim_merged.loc[:,['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','DTL_LINE_NBR','UM_CASE_NBR', 'RFRNC_NBR','score']]
        claim_merged_output = claim_merged_output.sort_values(['KEY_CHK_DCN_NBR','score'], ascending=[True, False])
        claim_merged_output = claim_merged_output.drop_duplicates(subset=['KEY_CHK_DCN_NBR']).reset_index(drop=True)
        claim_merged_output['Header_compare_title'] = "Claim/Case Header"
        claim_merged_output['auth_detail_title'] = "Auth data"
        claim_merged_output['claim_detail_title'] = "Claim Data"
        claim_merged_output['Header_compare'] = claim_merged_output.apply(self.__subset_header__, args=(claim_merged,), axis=1)
        claim_merged_output['auth_detail'] = claim_merged_output.apply(self.__auth_detail__, args=(claim_merged,), axis=1)
        claim_merged_output['claim_detail'] = claim_merged_output.apply(self.__claim_detail__, args=(claim_merged,), axis=1)
        claim_merged_output = claim_merged_output.loc[:,['KEY_CHK_DCN_NBR','CURNT_QUE_UNIT_NBR','Header_compare_title','Header_compare','auth_detail_title','auth_detail','claim_detail_title','claim_detail']]

        result_row['respCd'] = '200'
        result_row['resDesc'] = 'UM Model Processed successfully'
        result_row['recommendationsPresent'] = True
        recommendations = []
        recommendation = {}
        recommendation['modelName'] = 'um'
        recommendation['actionCode'] = final_out['Top_1_UM'].iloc[0]
        recommendation['actionValue'] = final_out['Top_1_UM_Score'].iloc[0]
        recommendations.append(recommendation)        
        result_row['recommendations'] = recommendations
        model_insights = []
        model_insight = {}
        model_insight['modelName'] = 'um'
        model_insight['modelInsight'] =  claim_merged_output.to_json()
        model_insights.append(model_insight)
        result_row['modelInsights'] = model_insights

        return result_row



    def __clean_raw_data__(self, raw_data, string_cols):
        for col in string_cols:
            raw_data[col] = raw_data[col].apply(str)
        return raw_data


    # function for exact match
    # ignore '*'
    def __exact_match__(self, col_list1, col_list2, compare_table):
        output_array = np.zeros(compare_table.shape[0])
        for col_name1 in col_list1:
            compare_table[col_name1] = compare_table[col_name1].str.replace(' ', '')
            for col_name2 in col_list2:
                compare_table[col_name2] = compare_table[col_name2].str.replace(' ', '')
                output_array = output_array + np.array((compare_table[col_name1] == compare_table[col_name2]) & (compare_table[col_name1] != '*'))
        return (output_array > 0) * 1


    # function for date compare of overlap, partial overlap, no overlap
    def __date_match__(self, dateA1, dateA2, dateB1, dateB2, compare_table):
        no_overlap = np.array((compare_table[dateA1] > compare_table[dateB2]) | (compare_table[dateB1] > compare_table[dateA2]))
        complete_overlap = np.array(((compare_table[dateA1] >= compare_table[dateB1]) & (compare_table[dateA2] <= compare_table[dateB2])) | ((compare_table[dateB1] >= compare_table[dateA1]) & (compare_table[dateB2] <= compare_table[dateA2])))
        partial_overlap = np.array((~no_overlap) & (~complete_overlap))
        return (no_overlap) * 1, (complete_overlap) * 1, (partial_overlap) * 1


    def __subset_header__(self, x, claim_merged):
        claim_merged = claim_merged[(claim_merged.KEY_CHK_DCN_NBR == x.KEY_CHK_DCN_NBR) & (claim_merged.DTL_LINE_NBR == x.DTL_LINE_NBR)].reset_index(drop=True)
        claim_merged['SRC_PROV_NM'] = claim_merged['SRC_PROV_FRST_NM'] + ' ' + claim_merged['SRC_PROV_LAST_NM']
        claim_merged['SRC_PROV_NM_RP'] = claim_merged['SRC_PROV_FRST_NM_RP'] + ' ' + claim_merged['SRC_PROV_LAST_NM_RP']
        header_name = ['DCN/Case Number',  'Provider TaxID', 'Provider NPI', 'Provider Medicare#', 'Provider Name', 'Provider Second Name', 'Member Code', 'From Date', 'To Date', 'Claim Type/Case Code', 'Case Status']
        header_claim = ['KEY_CHK_DCN_NBR', 'PROV_TAX_ID', 'BILLG_NPI', '-', 'PROV_NM','PROV_SCNDRY_NM', 'PAT_MBR_CD', 'clm_from_dt', 'clm_to_dt', 'CLAIM_TYPE', '-']
        header_um = ['RFRNC_NBR', 'SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'SRC_PROV_NM', 'SRC_PROV_NM_RP', 'SRC_MBR_CD', 'AUTHRZD_SRVC_FROM_DT', 'AUTHRZD_SRVC_TO_DT', '-', 'SRC_UM_SRVC_STTS_CD']
        header_compare = {'Field': header_name,
                        'Claim': np.array(claim_merged[header_claim])[0],
                        'UM': np.array(claim_merged[header_um])[0]}
        header_compare = pd.DataFrame(header_compare)
        return header_compare


    def __auth_detail__(self, x, claim_merged):
        detail_compare = claim_merged.loc[claim_merged.KEY_CHK_DCN_NBR == x.KEY_CHK_DCN_NBR,['RFRNC_NBR', 'RQSTD_PROC_SRVC_CD', 'AUTHRZD_PROC_SRVC_CD', 'AUTHRZD_SRVC_FROM_DT', 'AUTHRZD_SRVC_TO_DT', 'AUTHRZD_QTY']].rename(columns={'RFRNC_NBR': 'UM Case Number', 'RQSTD_PROC_SRVC_CD':'Procedure Code', 'AUTHRZD_PROC_SRVC_CD':'HCPCS Code', 'AUTHRZD_SRVC_FROM_DT':'From Date', 'AUTHRZD_SRVC_TO_DT':'To Date', 'AUTHRZD_QTY':'Units'})
        detail_compare = detail_compare.drop_duplicates()
        return detail_compare


    def __claim_detail__(self, x, claim_merged):
        detail_compare = claim_merged.loc[claim_merged.KEY_CHK_DCN_NBR == x.KEY_CHK_DCN_NBR,['DTL_LINE_NBR','PROC_CD', 'HCPCS_CD', 'dtl_from_dt', 'dtl_to_dt', 'BILLD_SRVC_UNIT_QTY']].rename(columns={'DTL_LINE_NBR': 'Line #', 'PROC_CD':'Procedure Code', 'HCPCS_CD':'HCPCS Code', 'dtl_from_dt':'From Date', 'dtl_to_dt':'To Date', 'BILLD_SRVC_UNIT_QTY':'Units'})
        detail_compare = detail_compare.drop_duplicates()
        detail_compare = pd.DataFrame(detail_compare)
        detail_compare = pd.DataFrame(detail_compare)
        return detail_compare


    # function for column contain column
    # ignore '*'
    def __contain_match__(self, col_list1, col_list2, compare_table):
        output_array = np.zeros(compare_table.shape[0])
        for col_name1 in col_list1:
            if pd.isna(compare_table[col_name1].str.len().unique())[0]:
                continue
            for col_name2 in col_list2: #np.array(col_locator['VAR'].str.startswith("PROV_TAX_ID"))
                if pd.isna(compare_table[col_name2].str.len().unique()[0]):
                    continue
                output_array = output_array + np.array((compare_table.apply(lambda x: x[col_name1] in x[col_name2], axis=1)) & (compare_table[col_name1] != '*') & (compare_table[col_name2] != '*'))
        return (output_array > 0) * 1


    def __UM_deploy__(self, um_auth_data, claim_line_data):
        start_time = time.time()
        um_auth_data = um_auth_data.loc[:, ['SRC_SBSCRBR_ID', 'RFRNC_NBR', 'SRVC_LINE_NBR', 'PRMRY_DIAG_CD', 'SRC_MBR_CD', 'RQSTD_PLACE_OF_SRVC_CD', 'SRC_RQSTD_PLACE_OF_SRVC_CD', 'AUTHRZD_PLACE_OF_SRVC_CD', 'SRC_AUTHRZD_PLACE_OF_SRVC_CD', 'RQSTD_PROC_SRVC_CD', 'AUTHRZD_PROC_SRVC_CD', 'RQSTD_SRVC_FROM_DT', 'RQSTD_SRVC_TO_DT', 'AUTHRZD_SRVC_FROM_DT', 'AUTHRZD_SRVC_TO_DT', 'SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'prov_id_rp', 'RQSTD_QTY', 'AUTHRZD_QTY', 'SRC_PROV_FRST_NM_RP','SRC_PROV_LAST_NM_RP', 'SRC_PROV_FRST_NM', 'SRC_PROV_LAST_NM', 'SRC_UM_SRVC_STTS_CD']]
        um_auth_data = um_auth_data.drop_duplicates().reset_index(drop=True)

        claim_line_data = claim_line_data.loc[:, ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'action_code', 'DTL_LINE_NBR', 'UM_CASE_NBR', 'memID', 'PROV_NM', 'PROV_SCNDRY_NM', 'ICD_A_CD', 'ICD_B_CD', 'ICD_C_CD', 'ICD_D_CD', 'ICD_E_CD', 'ICD_OTHR_6_CD', 'ICD_OTHR_7_CD', 'ICD_OTHR_8_CD', 'ICD_OTHR_9_CD', 'ICD_OTHR_10_CD', 'ICD_OTHR_11_CD', 'ICD_OTHR_12_CD', 'ICD_OTHR_13_CD', 'ICD_OTHR_14_CD', 'ICD_OTHR_15_CD', 'ICD_OTHR_16_CD', 'ICD_OTHR_17_CD', 'ICD_OTHR_18_CD', 'ICD_OTHR_19_CD', 'ICD_OTHR_20_CD', 'PAT_MBR_CD', 'HCFA_PT_CD', 'POT_CD', 'PROC_CD', 'HCPCS_CD', 'clm_from_dt', 'clm_to_dt', 'dtl_from_dt', 'dtl_to_dt', 'PROV_TAX_ID', 'BILLD_SRVC_UNIT_QTY', 'CLAIM_TYPE', 'BILLG_NPI', 'TOS_TYPE_CD']]
        claim_line_data = claim_line_data.drop_duplicates().reset_index(drop=True)

        um_auth_data = um_auth_data.fillna('*')
        claim_line_data = claim_line_data.fillna('*')

        um_auth_data = self.__clean_raw_data__(um_auth_data, ['SRC_SBSCRBR_ID', 'RFRNC_NBR', 'SRVC_LINE_NBR', 'PRMRY_DIAG_CD', 'SRC_MBR_CD', 'RQSTD_PLACE_OF_SRVC_CD', 'SRC_RQSTD_PLACE_OF_SRVC_CD', 'AUTHRZD_PLACE_OF_SRVC_CD', 'SRC_AUTHRZD_PLACE_OF_SRVC_CD', 'RQSTD_PROC_SRVC_CD', 'AUTHRZD_PROC_SRVC_CD', 'RQSTD_SRVC_FROM_DT', 'RQSTD_SRVC_TO_DT', 'AUTHRZD_SRVC_FROM_DT', 'AUTHRZD_SRVC_TO_DT', 'SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'prov_id_rp', 'RQSTD_QTY', 'AUTHRZD_QTY', 'SRC_PROV_FRST_NM_RP','SRC_PROV_LAST_NM_RP', 'SRC_PROV_FRST_NM', 'SRC_PROV_LAST_NM', 'SRC_UM_SRVC_STTS_CD'])
        claim_line_data = self.__clean_raw_data__(claim_line_data, ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'action_code', 'DTL_LINE_NBR', 'UM_CASE_NBR', 'memID', 'PROV_NM', 'PROV_SCNDRY_NM', 'ICD_A_CD', 'ICD_B_CD', 'ICD_C_CD', 'ICD_D_CD', 'ICD_E_CD', 'ICD_OTHR_6_CD', 'ICD_OTHR_7_CD', 'ICD_OTHR_8_CD', 'ICD_OTHR_9_CD', 'ICD_OTHR_10_CD', 'ICD_OTHR_11_CD', 'ICD_OTHR_12_CD', 'ICD_OTHR_13_CD', 'ICD_OTHR_14_CD', 'ICD_OTHR_15_CD', 'ICD_OTHR_16_CD', 'ICD_OTHR_17_CD', 'ICD_OTHR_18_CD', 'ICD_OTHR_19_CD', 'ICD_OTHR_20_CD', 'PAT_MBR_CD', 'HCFA_PT_CD', 'POT_CD', 'PROC_CD', 'HCPCS_CD', 'clm_from_dt', 'clm_to_dt', 'dtl_from_dt', 'dtl_to_dt', 'PROV_TAX_ID', 'BILLD_SRVC_UNIT_QTY', 'CLAIM_TYPE', 'BILLG_NPI', 'TOS_TYPE_CD'])

        # merge claim line data and csv
        claim_merged = claim_line_data.merge(um_auth_data, how='inner', left_on='memID', right_on='SRC_SBSCRBR_ID')

        # print(claim_merged.columns)
        self.logger.debug('no of claim_merged rows %d' % len(claim_merged), extra=self.extra)

        # prepare output table
        matching_result = claim_merged.loc[:, ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'DTL_LINE_NBR', 'UM_CASE_NBR', 'RFRNC_NBR']]

        # diag code exact match
        col_list1 = ['ICD_A_CD', 'ICD_B_CD', 'ICD_C_CD', 'ICD_D_CD', 'ICD_E_CD', 'ICD_OTHR_6_CD', 'ICD_OTHR_7_CD', 'ICD_OTHR_8_CD', 'ICD_OTHR_9_CD', 'ICD_OTHR_10_CD', 'ICD_OTHR_11_CD', 'ICD_OTHR_12_CD', 'ICD_OTHR_13_CD', 'ICD_OTHR_14_CD', 'ICD_OTHR_15_CD', 'ICD_OTHR_16_CD', 'ICD_OTHR_17_CD', 'ICD_OTHR_18_CD', 'ICD_OTHR_19_CD', 'ICD_OTHR_20_CD']
        col_list2 = ['PRMRY_DIAG_CD']
        matching_result.loc[:, 'Diag_code_exact'] = self.__exact_match__(col_list1, col_list2, claim_merged)

        # member code exact match
        col_list1 = ['PAT_MBR_CD']
        col_list2 = ['SRC_MBR_CD']
        matching_result.loc[:, 'Member_code_exact'] = self.__exact_match__(col_list1, col_list2, claim_merged)

        # place of service exact match
        col_list1 = ['HCFA_PT_CD', 'POT_CD']
        # print(claim_merged.loc[:, col_list1])
        col_list2 = ['RQSTD_PLACE_OF_SRVC_CD', 'SRC_RQSTD_PLACE_OF_SRVC_CD', 'AUTHRZD_PLACE_OF_SRVC_CD', 'SRC_AUTHRZD_PLACE_OF_SRVC_CD']
        matching_result.loc[:, 'Place_of_service_exact'] = self.__exact_match__(col_list1, col_list2, claim_merged)

        # proc code exact match
        col_list1 = ['PROC_CD', 'HCPCS_CD']
        col_list2 = ['RQSTD_PROC_SRVC_CD', 'AUTHRZD_PROC_SRVC_CD']
        matching_result.loc[:, 'Proc_code_exact'] = self.__exact_match__(col_list1, col_list2, claim_merged)

        # date format change
        claim_merged['clm_from_dt2'] = pd.to_datetime(claim_merged['clm_from_dt'], format='%m/%d/%Y', errors='coerce')
        claim_merged['clm_to_dt2'] = pd.to_datetime(claim_merged['clm_to_dt'], format='%m/%d/%Y', errors='coerce')
        claim_merged['dtl_from_dt2'] = pd.to_datetime(claim_merged['dtl_from_dt'], format='%m/%d/%Y', errors='coerce')
        claim_merged['dtl_to_dt2'] = pd.to_datetime(claim_merged['dtl_to_dt'], format='%m/%d/%Y', errors='coerce')

        if '/' in claim_merged['RQSTD_SRVC_FROM_DT'][0]:
            claim_merged['RQSTD_SRVC_FROM_DT2'] = pd.to_datetime(claim_merged['RQSTD_SRVC_FROM_DT'], format='%m/%d/%Y', errors='coerce')
            claim_merged['RQSTD_SRVC_TO_DT2'] = pd.to_datetime(claim_merged['RQSTD_SRVC_TO_DT'], format='%m/%d/%Y', errors='coerce')
            claim_merged['AUTHRZD_SRVC_FROM_DT2'] = pd.to_datetime(claim_merged['AUTHRZD_SRVC_FROM_DT'], format='%m/%d/%Y', errors='coerce')
            claim_merged['AUTHRZD_SRVC_TO_DT2'] = pd.to_datetime(claim_merged['AUTHRZD_SRVC_TO_DT'], format='%m/%d/%Y', errors='coerce')
        elif '-' in claim_merged['RQSTD_SRVC_FROM_DT'][0]:
            claim_merged['RQSTD_SRVC_FROM_DT2'] = pd.to_datetime(claim_merged['RQSTD_SRVC_FROM_DT'], format='%Y-%m-%d', errors='coerce')
            claim_merged['RQSTD_SRVC_TO_DT2'] = pd.to_datetime(claim_merged['RQSTD_SRVC_TO_DT'], format='%Y-%m-%d', errors='coerce')
            claim_merged['AUTHRZD_SRVC_FROM_DT2'] = pd.to_datetime(claim_merged['AUTHRZD_SRVC_FROM_DT'], format='%Y-%m-%d', errors='coerce')
            claim_merged['AUTHRZD_SRVC_TO_DT2'] = pd.to_datetime(claim_merged['AUTHRZD_SRVC_TO_DT'], format='%Y-%m-%d', errors='coerce')
        claim_merged.loc[pd.isnull(claim_merged.loc[:, 'AUTHRZD_SRVC_FROM_DT2']), 'AUTHRZD_SRVC_FROM_DT2'] = claim_merged.loc[pd.isnull(claim_merged.loc[:, 'AUTHRZD_SRVC_FROM_DT2']), 'RQSTD_SRVC_FROM_DT2']
        claim_merged.loc[pd.isnull(claim_merged.loc[:, 'AUTHRZD_SRVC_TO_DT2']), 'AUTHRZD_SRVC_TO_DT2'] = claim_merged.loc[pd.isnull(claim_merged.loc[:, 'AUTHRZD_SRVC_TO_DT2']), 'RQSTD_SRVC_TO_DT2']

        # date overlap
        matching_result.loc[:, 'clm_RQSTD_no_overlap'], matching_result.loc[:, 'clm_RQSTD_complete_overlap'], matching_result.loc[:, 'clm_RQSTD_partial_overlap'] = self.__date_match__('clm_from_dt2', 'clm_to_dt2', 'RQSTD_SRVC_FROM_DT2', 'RQSTD_SRVC_TO_DT2', claim_merged)
        matching_result.loc[:, 'clm_AUTHRZD_no_overlap'], matching_result.loc[:, 'clm_AUTHRZD_complete_overlap'], matching_result.loc[:, 'clm_AUTHRZD_partial_overlap'] = self.__date_match__('clm_from_dt2', 'clm_to_dt2', 'AUTHRZD_SRVC_FROM_DT2', 'AUTHRZD_SRVC_TO_DT2', claim_merged)
        matching_result.loc[:, 'dtl_RQSTD_no_overlap'], matching_result.loc[:, 'dtl_RQSTD_complete_overlap'], matching_result.loc[:, 'dtl_RQSTD_partial_overlap'] = self.__date_match__('dtl_from_dt2', 'dtl_to_dt2', 'RQSTD_SRVC_FROM_DT2', 'RQSTD_SRVC_TO_DT2', claim_merged)
        matching_result.loc[:, 'dtl_AUTHRZD_no_overlap'], matching_result.loc[:, 'dtl_AUTHRZD_complete_overlap'], matching_result.loc[:, 'dtl_AUTHRZD_partial_overlap'] = self.__date_match__('dtl_from_dt2', 'dtl_to_dt2', 'AUTHRZD_SRVC_FROM_DT2', 'AUTHRZD_SRVC_TO_DT2', claim_merged)
        # dtl_from_dt - AUTHRZD_SRVC_FROM_DT difference
        matching_result.loc[:, 'dtl_AUTHRZD_from_dt'] = (claim_merged['dtl_from_dt2'] - claim_merged['AUTHRZD_SRVC_FROM_DT2']).dt.days
        matching_result.loc[pd.isnull(matching_result.loc[:, 'dtl_AUTHRZD_from_dt']), 'dtl_AUTHRZD_from_dt'] = -100
        # AUTHRZD_SRVC_TO_DT - dtl_to_dt difference
        matching_result.loc[:, 'AUTHRZD_dtl_to_dt'] = (claim_merged['AUTHRZD_SRVC_TO_DT2'] - claim_merged['dtl_to_dt2']).dt.days
        matching_result.loc[pd.isnull(matching_result.loc[:, 'AUTHRZD_dtl_to_dt']), 'AUTHRZD_dtl_to_dt'] = -100

        # tax id contain match
        col_list1 = ['PROV_TAX_ID', 'BILLG_NPI']
        col_list2 = ['SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'prov_id_rp']
        matching_result.loc[:, 'Tax_id_contain'] = self.__contain_match__(col_list1, col_list2, claim_merged)

        # PROV_NM contain match
        claim_merged['SRC_PROV_NM'] = claim_merged['SRC_PROV_FRST_NM'] + ' ' + claim_merged['SRC_PROV_LAST_NM']
        claim_merged['SRC_PROV_NM_RP'] = claim_merged['SRC_PROV_FRST_NM_RP'] + ' ' + claim_merged['SRC_PROV_LAST_NM_RP']
        claim_merged['SRC_PROV_NM'] = claim_merged['SRC_PROV_NM'].str.lower()
        claim_merged['SRC_PROV_NM_RP'] = claim_merged['SRC_PROV_NM_RP'].str.lower()
        claim_merged['PROV_NM2'] = claim_merged['PROV_NM'].str.lower()
        claim_merged['PROV_SCNDRY_NM2'] = claim_merged['PROV_SCNDRY_NM'].str.lower()
        col_list1 = ['PROV_NM2', 'PROV_SCNDRY_NM2']
        col_list2 = ['SRC_PROV_NM', 'SRC_PROV_NM_RP']
        matching_result.loc[:, 'PROV_NM_contain'] = self.__contain_match__(col_list1, col_list2, claim_merged)

        # status == APPRVC
        matching_result.loc[:, 'SRC_UM_SRVC_STTS_CD_APPRVC'] = np.array(claim_merged['SRC_UM_SRVC_STTS_CD'].str.startswith('APPRVD')) * 1

        # tax id is NA
        tax_id_NA_list = ['*','UNK','999999999999','UMSHELLPRAC','UMSHELLFAC','Unknown','############','999999999','UUUUU1']
        col_list2 = ['SRC_UM_PROV_ID', 'src_um_prov_id_rp', 'PROV_ID', 'prov_id_rp']
        for col_name2 in col_list2:
            for tax_id_NA_item in tax_id_NA_list:
                claim_merged[col_name2] = claim_merged[col_name2].str.replace(tax_id_NA_item, 'UNK')
        matching_result.loc[:, 'Tax_id_NA'] = np.array(claim_merged.SRC_UM_PROV_ID.str.startswith('UNK') & claim_merged.src_um_prov_id_rp.str.startswith('UNK') & claim_merged.PROV_ID.str.startswith('UNK') & claim_merged.prov_id_rp.str.startswith('UNK')) * 1

        # TOS_TYPE_CD
        TOS_TYPE_CD_list = ['OPH','PHT','DXL','PSY','SUP','HHC','RAB','SRG','PHO','DME','SPC','RAD','MWH','PRV','HSP','CHE','PAN','AMB','SPT','PAS','SNF','AIR','ERP','CON','DEN']
        for TOS_TYPE_CD_item in TOS_TYPE_CD_list:
            matching_result.loc[:, 'TOS_TYPE_CD_'+TOS_TYPE_CD_item] = np.array(claim_merged['TOS_TYPE_CD']==TOS_TYPE_CD_item) * 1

        # qty format change
        claim_merged['BILLD_SRVC_UNIT_QTY'] = pd.to_numeric(claim_merged['BILLD_SRVC_UNIT_QTY'], downcast='float', errors='coerce')
        claim_merged['RQSTD_QTY'] = pd.to_numeric(claim_merged['RQSTD_QTY'], downcast='float', errors='coerce')
        claim_merged['AUTHRZD_QTY'] = pd.to_numeric(claim_merged['AUTHRZD_QTY'], downcast='float', errors='coerce')
        # qty difference
        matching_result.loc[:, 'Qty_diff_RQSTD'] = claim_merged['RQSTD_QTY'] - claim_merged['BILLD_SRVC_UNIT_QTY']
        matching_result.loc[:, 'Qty_diff_AUTHRZD'] = claim_merged['AUTHRZD_QTY'] - claim_merged['BILLD_SRVC_UNIT_QTY']

        # claim_type variable
        matching_result.loc[:, 'CLAIM_TYPE_INPT'] = (claim_merged['CLAIM_TYPE'] == 'INPT') * 1
        matching_result.loc[:, 'CLAIM_TYPE_PROF'] = (claim_merged['CLAIM_TYPE'] == 'PROF') * 1
        matching_result.loc[:, 'CLAIM_TYPE_SN'] = (claim_merged['CLAIM_TYPE'] == 'SN') * 1
        matching_result.loc[:, 'CLAIM_TYPE_OUTPT'] = (claim_merged['CLAIM_TYPE'] == 'OUTPT') * 1

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
        matching_result.loc[:,['clm_AUTHRZD_from_dt','AUTHRZD_clm_to_dt','dtl_RQSTD_from_dt','RQSTD_dtl_to_dt','clm_RQSTD_from_dt','RQSTD_clm_to_dt']] = matching_result.loc[:,['clm_AUTHRZD_from_dt','AUTHRZD_clm_to_dt','dtl_RQSTD_from_dt','RQSTD_dtl_to_dt','clm_RQSTD_from_dt','RQSTD_clm_to_dt']].fillna(-100)

        # output
        Y_col_list = ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'DTL_LINE_NBR', 'UM_CASE_NBR','RFRNC_NBR']
        Y_data = matching_result.loc[:, Y_col_list]
        X_data = matching_result.drop(columns=Y_col_list)
        col_locator = pd.DataFrame({'VAR':pd.Series(X_data.columns.values)})
        col_locator['col'] = range(X_data.shape[1])
        X_data = sparse.csr_matrix(X_data.values)
        self.logger.debug('Create model data took %d seconds' % (time.time() - start_time), extra=self.extra)

        start_time = time.time()

        X_data = X_data[:, np.array((col_locator['VAR'] != 'Diag_code_exact') & (col_locator['VAR'] != 'Place_of_service_exact') & (~col_locator['VAR'].str.endswith('overlap')))]
        col_locator = col_locator.loc[np.array((col_locator['VAR'] != 'Diag_code_exact') & (col_locator['VAR'] != 'Place_of_service_exact') & (~col_locator['VAR'].str.endswith('overlap'))),:].reset_index(drop=True)

        usecases = self.app.config['usecases']
        models = []
        test_row_1 = {}
        for usecase in usecases:
            if (usecase['name'] == 'um'):
                models = usecase['models']
                break

        for model in models:
            binary_model = copy.deepcopy(model['binary'])
            pred_test = binary_model.predict_proba(X_data)[:, -1]
            Y_data['score'] = pred_test
            del binary_model

        self.logger.debug('Prediction took %d seconds' % (time.time() - start_time), extra=self.extra)

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
        self.logger.debug('Output result took %d seconds' % (time.time() - start_time), extra=self.extra)

        # print(matching_result)
        return final_out, score_feature_data, claim_merged


    #payload contains all the fields coming from Uber req document
    def Um_DerivedFields(self,trans_data,payload,derived_fields = ['memID','CLM_TYPE'],*args):
        """
        REQ:
        1. Input of this function is the transform data after vlidation and payload
        2. The following is the match logic that should be included:
            #CLM_TYPE
            CASE WHEN CLM_TYPE_CD in ('MA', 'PA', 'PC', 'MM', 'PM') then 'PROF'
            WHEN CLM_TYPE_CD in ('IA', 'IC', 'ID', 'OA', 'OC', 'OD', 'SA', 'SC') then 'INST'
            ELSE CLM_TYPE_CD END AS CLAIM_TYPE
            #PRVDR_STATUS
            CASE
	        WHEN (ITS_HOME_IND = 'Y' AND ITS_ORGNL_SCCF_NEW_NBR <> '*' AND  ITS_PARG_PROV_IND in ('P' , 'Y')) OR  PROV_IND NOT IN ('D','N') THEN 'PAR'
	        ELSE 'NON-PAR'
	        END AS PRVDR_STATUS
	        #memID
	        memID = CONCAT(SBSCRBR_CERTFN_1_NBR,SBSCRBR_CERTFN_1_NBR,SBSCRBR_CERTFN_1_NBR)
        :param transform dictonary with the help of mapping file, payload:
        :return: UMtransformed_payload with correct derived fields values
        """

        #memID :
        if 'memID' in derived_fields:
            SBSCRBR_CERTFN_1_NBR = str(payload.get('SBSCRBR_CERTFN_1_NBR'))
            SBSCRBR_CERTFN_2_NBR = str(payload.get('SBSCRBR_CERTFN_2_NBR'))
            SBSCRBR_CERTFN_3_NBR = str(payload.get('SBSCRBR_CERTFN_3_NBR'))

            trans_data['memID'] =  SBSCRBR_CERTFN_1_NBR+SBSCRBR_CERTFN_2_NBR+SBSCRBR_CERTFN_3_NBR


        #CLM_TYPE :
        if 'CLM_TYPE' in derived_fields:
            CLM_TYPE_CD = str(payload.get('CLM_TYPE_CD'))

            if CLM_TYPE_CD in  ('MA', 'PA', 'PC', 'MM', 'PM'):
                trans_data['CLAIM_TYPE'] = 'PROF'
            elif CLM_TYPE_CD in ('IA', 'IC', 'ID'):
                trans_data['CLAIM_TYPE'] = 'INPT'
            elif CLM_TYPE_CD in ('OA', 'OC', 'OD'):
                trans_data['CLAIM_TYPE'] = 'OUTPT'
            elif CLM_TYPE_CD in ( 'SA', 'SC'):
                trans_data['CLAIM_TYPE'] = 'SN'
            else:
                trans_data['CLAIM_TYPE'] = CLM_TYPE_CD


        #PRVDR_STATUS :
        #IF fields are not available we are giving * as default values
        if 'PRVDR_STATUS' in derived_fields:
            PROV_IND = str(payload.get('PROV_IND',"*"))
            ITS_HOME_IND = str(payload.get('ITS_HOME_IND',"*"))
            ITS_ORGNL_SCCF_NEW_NBR = str(payload.get('ITS_ORGNL_SCCF_NEW_NBR',"*"))
            ITS_PARG_PROV_IND = str(payload.get('ITS_PARG_PROV_IND',"*"))

            if (PROV_IND not in ('D','N') or (ITS_HOME_IND == 'Y' and  ITS_ORGNL_SCCF_NEW_NBR not in ('*' and ITS_PARG_PROV_IND in ('P' , 'Y')))):
                trans_data['PRVDR_STATUS'] = 'PAR'
            else:
                trans_data['PRVDR_STATUS'] = 'NON-PAR'

        return trans_data



    def __UM_validate__(self, payload):

        return True
