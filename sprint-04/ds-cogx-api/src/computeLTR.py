import time
import ltr
import response
import copy
import traceback,sys
import constants as const
from computeBase import computeBase
from measure import measure_time_method


class computeLTR(computeBase):

    def __init__(self, app, logger, extra):
        super().__init__(app, logger,extra)
        self.model_name = 'ltr'
    

    def transform(self, payload, model_mapping):
        return super().transform(payload, model_mapping)
    

    def validate(self, payload, validation_schema):
        return super().validate(payload, validation_schema)


    @measure_time_method
    def process_request(self, payload):
        result_row = {}
        self.generate_result_row(result_row, payload)
        start_time = time.time()
        self.validate(payload, 'ltr_validation')
        self.logger.debug('Elapsed time', extra=response.extra_tags(self.extra, {'validate': time.time() - start_time}, 0))
        if len(self.errors) > 0:
            self.generate_messages(result_row)
            return self.generate_desc(900, result_row)
        if self.validate_business_ex_before(payload):
            return self.generate_desc(709,result_row)
        start_time = time.time()
        try:
            # Transforming each claimDetail at a time as transform does not support array yet
            err_cds = [] if payload.get('ERR_CDS') is None else payload.get('ERR_CDS')

            if None in err_cds:
                err_cds.remove(None)
            payload['ERR_CDS'] = err_cds
            claimDetails = payload.get('claimDetails', [])
            _ltr_transformed_payloads = []
            # placeholder to construct the input for model
            processed_payload = None
            if len(claimDetails) == 0:
                processed_payload = self.transform(payload, 'ltr_mapping')

            for claimDetail in claimDetails:
                payload['claimDetails'] = [claimDetail]
                processed_payload = self.transform(payload, 'ltr_mapping')
                self.transform_DerivedFields(processed_payload['details'][0], None, derived_fields=['MBR_CNTRCT_END_DT', 'BNFT_YEAR_CNTRCT_REV_DT'])
                _ltr_transformed_payloads.append(*processed_payload.get('details', []))

            processed_payload['details'] = _ltr_transformed_payloads
        except Exception as e:
            self.logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=self.extra)
            return self.generate_desc(901, result_row)

        try:
            self.transform_DerivedFields(processed_payload['header'], payload, derived_fields = ['CLM_TYPE','PRVDR_STATUS', 'ASO_FI', 'PROD_NTWK'])
            # Framework will not be able to populate fields above 13 so this is a temporary fix until i get to update the framework
            self.__populateAdditionalEdits__(payload.get('ERR_CDS', []), processed_payload.get('edits'))
        except Exception as e:
            self.logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=self.extra)
            return self.generate_desc(902, result_row)

        self.logger.debug('Elapsed time', extra=response.extra_tags(self.extra, {'transform': time.time() - start_time}, 0))
        self.logger.debug("transformed", extra=response.extra_tags(self.extra, {'ltr_transformed': processed_payload}))
        result_row.update(ltr.run(self.app, self.logger, self.extra, processed_payload, payload))
        return result_row
        

    def __populateAdditionalEdits__(self, err_cds, edits):
        if len(err_cds) > 13:
            edit = copy.deepcopy(edits[0])
            edit['ERR_1_CD'] = err_cds[13].strip() if len(err_cds) > 13 else None
            edit['ERR_2_CD'] = err_cds[14].strip() if len(err_cds) > 14 else None
            edit['ERR_3_CD'] = err_cds[15].strip() if len(err_cds) > 15 else None
            edit['ERR_4_CD'] = err_cds[16].strip() if len(err_cds) > 16 else None
            edit['ERR_5_CD'] = err_cds[17].strip() if len(err_cds) > 17 else None
            edit['ERR_6_CD'] = err_cds[18].strip() if len(err_cds) > 18 else None
            edit['ERR_7_CD'] = err_cds[19].strip() if len(err_cds) > 19 else None
            edit['ERR_8_CD'] = err_cds[20].strip() if len(err_cds) > 20 else None
            edit['ERR_9_CD'] = err_cds[21].strip() if len(err_cds) > 21 else None
            edit['ERR_10_CD'] = err_cds[22].strip() if len(err_cds) > 22 else None
            edit['ERR_11_CD'] = err_cds[23].strip() if len(err_cds) > 23 else None
            edit['ERR_12_CD'] = err_cds[24].strip() if len(err_cds) > 24 else None
            edit['ERR_13_CD'] = err_cds[25].strip() if len(err_cds) > 25 else None
            edits.append(edit)

        if len(err_cds) > 26:
            edit = copy.deepcopy(edits[0])
            edit['ERR_1_CD'] = err_cds[26].strip() if len(err_cds) > 26 else None
            edit['ERR_2_CD'] = err_cds[27].strip() if len(err_cds) > 27 else None
            edit['ERR_3_CD'] = err_cds[28].strip() if len(err_cds) > 28 else None
            edit['ERR_4_CD'] = err_cds[29].strip() if len(err_cds) > 29 else None
            edit['ERR_5_CD'] = err_cds[30].strip() if len(err_cds) > 30 else None
            edit['ERR_6_CD'] = err_cds[31].strip() if len(err_cds) > 31 else None
            edit['ERR_7_CD'] = err_cds[32].strip() if len(err_cds) > 32 else None
            edit['ERR_8_CD'] = err_cds[33].strip() if len(err_cds) > 33 else None
            edit['ERR_9_CD'] = err_cds[34].strip() if len(err_cds) > 34 else None
            edit['ERR_10_CD'] = err_cds[35].strip() if len(err_cds) > 35 else None
            edit['ERR_11_CD'] = err_cds[36].strip() if len(err_cds) > 36 else None
            edit['ERR_12_CD'] = err_cds[37].strip() if len(err_cds) > 37 else None
            edit['ERR_13_CD'] = err_cds[38].strip() if len(err_cds) > 38 else None
            edits.append(edit)

    def validate_business_ex_before(self,payload):
        GRP_NBR = str(payload.get('GRP_NBR',None)).upper()
        CLM_PAYMNT_PRCS_CD = str(payload.get('CLM_PAYMNT_PRCS_CD', None)).upper()
        KEY_CHK_DCN_NBR = str(payload.get('KEY_CHK_DCN_NBR', None)).upper()
        MBR_PROD_CLS_CD = str(payload.get('MBR_PROD_CLS_CD', None)).upper()

        if (GRP_NBR.startswith("ITS")
            or (CLM_PAYMNT_PRCS_CD == '15' and (KEY_CHK_DCN_NBR[6:8] not in ('48', '87', '08', '47', '49')) and (KEY_CHK_DCN_NBR[6:7] not in ('M', 'N')))
            or (MBR_PROD_CLS_CD in ('01', '02', '03', '04', '06', '08'))) :
            self.logger.info("Business exclusion occur before model execution",extra=self.extra)
            return True
        else:
            return False
