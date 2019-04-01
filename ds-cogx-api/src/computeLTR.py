import constants as const
import pandas as pd
import json
import copy
import scipy.sparse as sp
import numpy as np
from computeBase import computeBase


class computeLTR(computeBase):

    def __init__(self, app, logger, extra):
        super().__init__(app, logger,extra)
        self.error_codes = const.EDIT_DICT['ERR_CD'] 
        self.icd_codes = const.HEADER_DICT['ICD']
        self.mod_codes = const.DETAIL_DICT['MODIFIER_CD']

    def transform(self, payload, model_mapping):
        return super().transform(payload, model_mapping)
        #return self.UM_transform(obj,payload)


    def validate(self, payload, validation_schema):
        # return super().validate(payload, validation_schema)
        pass


    def process_request(self, payload):
        result_row = {}
        result_row['KEY_CHK_DCN_NBR'] = payload.get('KEY_CHK_DCN_NBR', None)
        result_row['CURNT_QUE_UNIT_NBR'] = payload.get('CURNT_QUE_UNIT_NBR', None)

        ''' TODO uncomment after validation is implemented - start
        self.validate(payload, 'ltr_validation')
        if len(self.errors) > 0:
            # TODO boilerplate code need to move to base class, also not ideal to send messages to calling program
            self.logger.debug("Valid payload", extra=self.extra)
            result_row['respCd'] = '100'
            result_row['resDesc'] = 'Invalid data sent'
            result_row['recommendationsPresent'] = False
            messages = self.generate_messages()
            result_row['messages'] = messages
            return result_row
        # TODO uncomment after validation is complete - end
        ''' 

        '''
        # Transforming each claimDetail at a time as transform does not support array yet
        claimDetails = payload.get('claimDetails', [])
        _ltr_transformed_payloads = []
        for claimDetail in claimDetails:
            payload['claimDetails'] = [claimDetail]
            _ltr_transformed_payloads.append(self.transform(payload, 'ltr_mapping'))

        _ltr_derived_payloads = []
        for _ltr_transformed_payload in _ltr_transformed_payloads:
            _ltr_derived_payloads.append((self.ltr_DerivedFields(_ltr_transformed_payload['ltr_claim'], payload)))

        self.logger.debug("Transformed", extra=self.extra_tags({'ltr_transformed': _ltr_derived_payloads}))
        '''

        field_mapping = {}
        test_row = {}  # return value
        field_mapping['MEDCRB_APPV_EQU_PAID'] = 0.0
        field_mapping['MEDCRB_TWO_ZERO'] = 0.0
        
        field_mapping['SRVC_OUT_BNFT'] = 0.0
        field_mapping['SRVC_OVERLAP_BNFT'] = 0.0
        field_mapping['SRVC_OUT_MBR'] = 0.0
        field_mapping['SRVC_OVERLAP_MBR'] = 0.0
        
        self.logger.debug("Input", extra=self.extra)
        for key in payload.keys():
            content = payload[key]
            if isinstance(content, dict):
                for k, v in content.items():
                    field_mapping[k.strip()] = v.strip()
                    self.logger.debug(k.strip(), extra=self.extra)
                    field_mapping[k.strip() + '_' + v.strip()] = 1.0
                    self.logger.debug(k.strip() + '_' + v.strip(), extra=self.extra)
                if key == 'header':
                    field_mapping = self.claim_header_processing(field_mapping)
                    test_row['KEY_CHK_DCN_NBR'] = field_mapping['KEY_CHK_DCN_NBR']
                    test_row['CURNT_QUE_UNIT_NBR'] = field_mapping['CURNT_QUE_UNIT_NBR']
            elif isinstance(content, list):
                for elem in content:
                    if isinstance(elem, dict):
                        for k, v in elem.items():
                            field_mapping[k.strip()] = v.strip()
                            # logger.debug(k.strip())
                            field_mapping[k.strip() + '_' + v.strip()] = 1.0
                            # logger.debug(k.strip() + '_' + v.strip())
                        if key == 'edits':
                            field_mapping = self.claim_edit_processing(field_mapping)
                        elif key == 'details':
                            field_mapping = self.claim_detail_processing(field_mapping)

        self.logger.debug(field_mapping, extra=self.extra)

        values = []
        cols = []
        reject_list = self.app.config['model_encodings']['reject_list']
        for idx, value in enumerate(reject_list):
            if field_mapping.get(value) is not None:
                # logger.debug(value)
                # print(value + ' -'  + str(idx-1) + ' - ' + str(float(field_mapping.get(value))))
                values.append(float(field_mapping.get(value)))
                cols.append(idx-1)
        self.logger.debug(values, extra=self.extra)
        self.logger.debug(cols, extra=self.extra)
                
        X = sp.csr_matrix((values, ([0]*len(values), cols)))
        m = X.shape[0]
        X_2 = X[np.array(range(0,m)),:]
        self.logger.debug(X_2, extra=self.extra)

        usecases = self.app.config['usecases']
        models = []
        test_row_1 = {}
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

        result_row['respCd'] = '200'
        result_row['resDesc'] = 'LTR Model Processed successfully'
        result_row['recommendationsPresent'] = True
        recommendations = []
        recommendation = {}
        recommendation['modelName'] = 'ltr'
        recommendation['actionCode'] = df_predict_top3.iloc[:, 0].to_dict()[0]
        recommendation['actionValue'] = df_pred_score_top3.iloc[:, 0].to_dict()[0]
        recommendations.append(recommendation)
        result_row['recommendations'] = recommendations
        
        return result_row


    def claim_edit_processing(self, field_mapping):
        for err_cd in self.error_codes:
            if field_mapping[err_cd] != '*':
                field_mapping['ERR_CD_' + field_mapping[err_cd]] = 1.0
        return field_mapping


    def claim_detail_processing(self, field_mapping):
        if (float(field_mapping.get('MEDCRB_APRVD_AMT', 0)) > 0 and 
            (float(field_mapping.get('MEDCRB_APRVD_AMT',  0)) == float(field_mapping.get('MEDCRB_PAID_AMT',  0))) ):        
            field_mapping['MEDCRB_APPV_EQU_PAID'] = 1.0
        if (float(field_mapping.get('MEDCRB_COINSRN_AMT', 0)) + float(field_mapping.get('MEDCRB_DDCTBL_AMT', 0))) == 0 :
            field_mapping['MEDCRB_TWO_ZERO'] = 1.0

        for mod_cd in self.mod_codes:
            if field_mapping[mod_cd] != '*':
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
        field_mapping['SRVC_OUT_BNFT'] = (field_mapping['SRVC_FROM_DT'] >= field_mapping['BNFT_YEAR_CNTRCT_REV_DT']) or (field_mapping['SRVC_TO_DT'] <= field_mapping['BNFT_YEAR_CNTRCT_EFCTV_DT'])
        field_mapping['SRVC_OVERLAP_BNFT'] = (field_mapping['SRVC_FROM_DT'] <= field_mapping['BNFT_YEAR_CNTRCT_REV_DT']) and (field_mapping['SRVC_TO_DT'] >= field_mapping['BNFT_YEAR_CNTRCT_EFCTV_DT'])
        field_mapping['SRVC_OUT_MBR'] = (field_mapping['SRVC_FROM_DT'] >= field_mapping['MBR_CNTRCT_END_DT']) or (field_mapping['SRVC_TO_DT'] <= field_mapping['MBR_CNTRCT_EFCTV_DT'])
        field_mapping['SRVC_OVERLAP_MBR'] = (field_mapping['SRVC_FROM_DT'] <= field_mapping['MBR_CNTRCT_END_DT']) and (field_mapping['SRVC_TO_DT'] <= field_mapping['MBR_CNTRCT_EFCTV_DT'])
        return field_mapping


    def claim_header_processing(self, field_mapping):
        for icd_cd in self.icd_codes:
            if field_mapping[icd_cd] != '*':
                field_mapping['ICD_' + field_mapping[icd_cd]] = 1.0
        return field_mapping


