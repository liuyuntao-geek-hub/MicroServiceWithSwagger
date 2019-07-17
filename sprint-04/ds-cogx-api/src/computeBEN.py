import time
import traceback,sys
from measure import measure_time_method
from computeBase import computeBase
import ben


class computeBEN(computeBase):

    def __init__(self, app, logger, extra):
        super().__init__(app, logger,extra)
        self.model_name = 'ben'
        

    def transform(self, payload, model_mapping):
        return super().transform(payload, model_mapping)


    def validate(self, payload, validation_schema):
        return super().validate(payload, validation_schema)


    @measure_time_method
    def process_request(self, payload):
        result_row = {}
        self.generate_result_row(result_row, payload)
        start_time = time.time()
        self.validate(payload, 'ben_validation')
        self.logger.debug('Elapsed time', extra=self.extra_tags({'validate': time.time() - start_time}, 0))

        if len(self.errors) > 0:
            self.generate_messages(result_row)
            self.logger.debug("Invalid payload", extra=self.extra_tags({'error_msg': result_row.get('messages', [])}, 0))
            return self.generate_desc(900, result_row)

        start_time = time.time()
        try:
            claimDetails = payload.get('claimDetails', [])
            _ben_transformed_payloads = []
            for claimDetail in claimDetails:
                payload['claimDetails'] = [claimDetail]
                _ben_transformed_payloads.append(self.transform(payload, 'ben_mapping'))
        except Exception as e:
            self.logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=self.extra)
            return self.generate_desc(901, result_row)

        try:
            _ben_derived_payloads = []
            for _ben_transformed_payload in _ben_transformed_payloads:
                _ben_derived_payloads.append((self.transform_DerivedFields(_ben_transformed_payload['ben_claim'], payload, derived_fields = ['PRVDR_STATUS','CLM_TYPE', 'PROD_NTWK'])))

        except Exception as e:
            self.logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=self.extra)
            return self.generate_desc(902, result_row)

        self.logger.debug('Elapsed time', extra=self.extra_tags({'transform': time.time() - start_time}, 0))
        self.logger.debug("transformed", extra=self.extra_tags({'ben_transformed': _ben_derived_payloads}))

        result_row.update(ben.run(self.app, self.logger, self.extra, _ben_derived_payloads, payload))
        return result_row