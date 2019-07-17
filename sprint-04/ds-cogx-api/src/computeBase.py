import copy
import json_transform
import json_validate

from constants import RESP_CD_MAP
import response


class computeBase():

    def __init__(self, app, logger, extra):
        self.app = app
        self.logger = logger
        self.extra = extra
        self.errors = []
        self.model_name = ' '

    
    def validate(self, payload, validation_schema):
        payload_list = []
        if isinstance(payload,dict):
            payload_list = [payload]
        elif isinstance(payload,list):
            for item in payload:
                payload_list.append(item)
        self.errors = json_validate.assert_valid_data(payload_list, self.app.config['validations'][validation_schema], validator='Draft7Validator', tags=self.extra)
        if len(self.errors) > 0:
            self.logger.debug('Errors', extra=self.extra_tags({'messages': self.errors}))
        
    
    def transform(self, payload, model_mapping):
        mapping = copy.deepcopy(self.app.config['mappings'][model_mapping])
        transformed_obj = json_transform.transform(mapping, payload)
        return transformed_obj


    def extra_tags(self, tags, flag=1):
        return response.extra_tags(self.extra, tags, flag)        


    def generate_result_row(self, result_row, payload):
        response.generate_result_row(result_row, payload)


    def generate_messages(self, result_row):
        response.generate_messages(self.model_name, self.errors, result_row)


    def generate_desc(self, respCd, result_row, payload):
        return response.generate_desc(respCd, self.model_name, result_row, payload)
        

    #payload contains all the fields coming from Uber req document
    def transform_DerivedFields(self, trans_data, payload, *args, derived_fields=[]):
        # memID :
        if 'MEMID' in [x.upper() for x in derived_fields]:
            SBSCRBR_CERTFN_1_NBR = payload.get('SBSCRBR_CERTFN_1_NBR', None)
            SBSCRBR_CERTFN_2_NBR = payload.get('SBSCRBR_CERTFN_2_NBR', None)
            SBSCRBR_CERTFN_3_NBR = payload.get('SBSCRBR_CERTFN_3_NBR', None)

            if (SBSCRBR_CERTFN_1_NBR is None or SBSCRBR_CERTFN_2_NBR is None or SBSCRBR_CERTFN_3_NBR is None):
                trans_data['memID'] = None
            else:
                trans_data['memID'] = SBSCRBR_CERTFN_1_NBR + SBSCRBR_CERTFN_2_NBR + SBSCRBR_CERTFN_3_NBR

        # CLM_TYPE :
        if 'CLM_TYPE' in [x.upper() for x in derived_fields]:
            CLM_TYPE_CD = payload.get('CLM_TYPE_CD', None)

            if CLM_TYPE_CD in ('MA', 'PA', 'PC', 'MM', 'PM'):
                trans_data['CLAIM_TYPE'] = 'PROF'
            elif CLM_TYPE_CD in ('IA', 'IC', 'ID'):
                trans_data['CLAIM_TYPE'] = 'INPT'
            elif CLM_TYPE_CD in ('OA', 'OC', 'OD'):
                trans_data['CLAIM_TYPE'] = 'OUTPT'
            elif CLM_TYPE_CD in ('SA', 'SC'):
                trans_data['CLAIM_TYPE'] = 'SN'
            else:
                trans_data['CLAIM_TYPE'] = CLM_TYPE_CD

        # PRVDR_STATUS :
        # IF fields are not available we are giving * as default values
        if 'PRVDR_STATUS' in [x.upper() for x in derived_fields]:
            PROV_IND = payload.get('PROV_IND', None)
            ITS_HOME_IND = payload.get('ITS_HOME_IND', None)
            ITS_ORGNL_SCCF_NEW_NBR = payload.get('ITS_ORGNL_SCCF_NEW_NBR', None)
            ITS_PARG_PROV_IND = payload.get('ITS_PARG_PROV_IND', None)

            if (PROV_IND not in ('D', 'N') or (
                    ITS_HOME_IND == 'Y' and ITS_ORGNL_SCCF_NEW_NBR is not None and ITS_PARG_PROV_IND in ('P', 'Y'))):
                trans_data['PRVDR_STATUS'] = 'PAR'
            else:
                trans_data['PRVDR_STATUS'] = 'NON-PAR'

        # PROD_NTWK : alternative use hashing to make it faster
        if 'PROD_NTWK' in [x.upper() for x in derived_fields]:
            PRMRY_NTWK_CD = payload.get('PRMRY_NTWK_CD', 'None')
            if PRMRY_NTWK_CD is None:
                trans_data['PROD_NTWK'] = None

            elif PRMRY_NTWK_CD in ('F', '0'):
                trans_data['PROD_NTWK'] = 'FSA'
            elif PRMRY_NTWK_CD in ('1', 'H', 'D', '9'):
                trans_data['PROD_NTWK'] = 'FFS'
            elif PRMRY_NTWK_CD in ('2', 'V', '8'):
                trans_data['PROD_NTWK'] = 'HMO'
            elif PRMRY_NTWK_CD in ('3', '5', 'A', 'C', 'I', 'K', 'O', 'U', 'E', 'S', 'R'):
                trans_data['PROD_NTWK'] = 'PPO'
            elif PRMRY_NTWK_CD in ('4','6','P'):
                trans_data['PROD_NTWK'] = 'POS'
            elif PRMRY_NTWK_CD in ('7'):
                trans_data['PROD_NTWK'] = 'EPO'
            else:
                trans_data['PROD_NTWK'] = PRMRY_NTWK_CD

        if 'ASO_FI' in [x.upper() for x in derived_fields]:
            MK_FUND_TYPE_CD = payload.get('MK_FUND_TYPE_CD', None)
            
            if MK_FUND_TYPE_CD in ('E','G','H','P','Q','R','S','T','U','V','9','W','K','Y'):
                trans_data['ASO_FI'] = 'ASO'
            elif MK_FUND_TYPE_CD in ('1','2','3','A','B','J','7','C','4','F','5'):
                trans_data['ASO_FI'] = 'FULLY_INSURD'
            else:
                trans_data['ASO_FI'] = 'OTHER'

        if 'MBR_CNTRCT_END_DT' in [x.upper() for x in derived_fields]:
            MBR_CNTRCT_END_DT = trans_data.get('MBR_CNTRCT_END_DT', 'None')
            if MBR_CNTRCT_END_DT == '00/00/0000':
                MBR_CNTRCT_END_DT = '01/01/1970'
            elif MBR_CNTRCT_END_DT == '99/99/9999':
                MBR_CNTRCT_END_DT = '12/31/2049'
            trans_data['MBR_CNTRCT_END_DT'] = MBR_CNTRCT_END_DT

        if 'BNFT_YEAR_CNTRCT_REV_DT' in [x.upper() for x in derived_fields]:
            BNFT_YEAR_CNTRCT_REV_DT = trans_data.get('BNFT_YEAR_CNTRCT_REV_DT', 'None')
            if BNFT_YEAR_CNTRCT_REV_DT == '00/00/0000':
                BNFT_YEAR_CNTRCT_REV_DT = '01/01/1970'
            elif BNFT_YEAR_CNTRCT_REV_DT == '99/99/9999':
                BNFT_YEAR_CNTRCT_REV_DT = '12/31/2049'
            trans_data['BNFT_YEAR_CNTRCT_REV_DT'] = BNFT_YEAR_CNTRCT_REV_DT

        return trans_data
