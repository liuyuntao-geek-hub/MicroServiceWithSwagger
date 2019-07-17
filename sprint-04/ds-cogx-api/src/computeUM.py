import time
import traceback,sys
from measure import measure_time_method
from computeBase import computeBase
import um


class computeUM(computeBase):

    def __init__(self, app, logger, extra):
        super().__init__(app, logger,extra)
        self.model_name = 'um'
        

    def transform(self, payload, model_mapping):
        return super().transform(payload, model_mapping)


    def validate(self, payload, validation_schema):
        return super().validate(payload, validation_schema)


    @measure_time_method
    def process_request(self, payload):
        result_row = {}
        self.generate_result_row(result_row,payload)
        start_time = time.time()
        self.validate(payload, 'um_validation')
        self.logger.debug('Elapsed time', extra=self.extra_tags({'validate': time.time() - start_time}, 0))

        if len(self.errors) > 0:
            # TODO boilerplate code need to move to base class, also not ideal to send messages to calling program
            #self.logger.debug("Invalid payload", extra=self.extra)
            #result_row['respCd'] = '702'
            #result_row['resDesc'] = 'Bad Request'
            self.generate_messages(result_row)
            self.logger.debug("Invalid payload", extra=self.extra_tags({'error_msg': result_row.get('messages', [])}, 0))
            return self.generate_desc(900, result_row)
            #return result_row

        start_time = time.time()
        try:
            claimDetails = payload.get('claimDetails', [])
            _um_transformed_payloads = []
            for claimDetail in claimDetails:
                payload['claimDetails'] = [claimDetail]
                _um_transformed_payloads.append(self.transform(payload, 'um_mapping'))
        except Exception as e:
            self.logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=self.extra)
            return self.generate_desc(901, result_row)

        try:
            _um_derived_payloads = []
            for _um_transformed_payload in _um_transformed_payloads:
                _um_derived_payloads.append((self.transform_DerivedFields(_um_transformed_payload['um_claim'], payload, derived_fields = ['memID','CLM_TYPE'])))

        except Exception as e:
            self.logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=self.extra)
            return self.generate_desc(902, result_row)

        self.logger.debug('Elapsed time', extra=self.extra_tags({'transform': time.time() - start_time}, 0))
        self.logger.debug("transformed", extra=self.extra_tags({'um_transformed': _um_derived_payloads}))
        result_row.update(um.run(self.app, self.logger, self.extra, _um_derived_payloads, payload))
        return result_row
