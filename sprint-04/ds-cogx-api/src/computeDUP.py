from pandas.io.json import json_normalize

from computeBase import computeBase
from measure import measure_time_method
import response
import time,copy
import traceback,sys
import dup


class computeDUP(computeBase):

    def __init__(self, app, logger, extra):
        super().__init__(app, logger, extra)
        self.model_name = 'dup'

    def transform(self, payload, model_mapping):
        return super().transform(payload, model_mapping)

    def validate(self, payload, validation_schema):
        return super().validate(payload, validation_schema)

    @measure_time_method
    def process_request(self, payload):
        result_row = {}
        self.generate_result_row(result_row, payload)
        start_time = time.time()
        self.validate(payload, 'dup_validation')
        self.logger.debug('Elapsed time',
                          extra=response.extra_tags(self.extra, {'validate': time.time() - start_time}, 0))
        if len(self.errors) > 0:
            self.generate_messages(result_row)
            return self.generate_desc(900, result_row)
        start_time = time.time()
        try:
            # Transforming each claimDetail at a time as transform does not support array yet
            err_cds = [] if payload.get('ERR_CDS') is None else payload.get('ERR_CDS')
            if None in err_cds:
                err_cds.remove(None)
            payload['ERR_CDS'] = err_cds

            payload_cp =  copy.deepcopy(payload)

            claimDetails = payload.get('claimDetails', [])
            _dup_transformed_details_list = []

            processed_payload = None
            if len(claimDetails) == 0:
                processed_payload = self.transform(payload, 'dup_mapping')
            else:
                for claimDetail in claimDetails:
                    payload['claimDetails'] = [claimDetail]
                    processed_payload = self.transform(payload, 'dup_mapping')
                    self.transform_DerivedFields(processed_payload['details'][0], None, derived_fields=['MBR_CNTRCT_END_DT', 'BNFT_YEAR_CNTRCT_REV_DT'])
                    _dup_transformed_details_list.append(*processed_payload.get('details', []))

                processed_payload['details'] = _dup_transformed_details_list
                payload['claimDetails'] = claimDetails


        except Exception as e:
            self.logger.error(
                (''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),
                extra=self.extra)
            return self.generate_desc(901, result_row)

        try:
            self.transform_DerivedFields(processed_payload['header'], payload,
                                         derived_fields=['CLM_TYPE', 'PRVDR_STATUS', 'ASO_FI', 'PROD_NTWK', 'MEMID'])
            # Framework will not be able to populate fields above 13 so this is a temporary fix until i get to update the framework
            self.__populateAdditionalEdits__(payload.get('ERR_CDS', []), processed_payload.get('edits'))
        except Exception as e:
            self.logger.error(
                (''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),
                extra=self.extra)
            return self.generate_desc(902, result_row)

        self.logger.debug('Elapsed time',
                          extra=response.extra_tags(self.extra, {'transform': time.time() - start_time}, 0))
        self.logger.debug("transformed", extra=response.extra_tags(self.extra, {'dup_transformed': processed_payload}))
        #result_row.update(dup.run(self.app, self.logger, self.extra, payload_cp, payload_cp))
        result_row.update(dup.run(self.app, self.logger, self.extra, processed_payload, payload_cp))
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
