import copy
import json_transform
import json_validate


class computeBase():

    def __init__(self, app, logger, extra):
        self.app = app
        self.logger = logger
        self.extra = extra
        self.errors = []

    
    def validate(self, payload, validation_schema):
        payload_list = []
        if(str(type(payload)) == "<class 'dict'>"):
            payload_list = [payload]
        elif (str(type(payload)) == "<class 'list'>"):
            for item in payload:
                payload_list.append(item)

        self.errors = json_validate.assert_valid_data(payload_list, self.app.config['validations'][validation_schema], validator='Draft7Validator', tags=self.extra)
        if len(self.errors) > 0:
            self.logger.debug('Errors', extra=self.extra_tags({'messages': self.errors}))
        
    
    def transform(self, payload, model_mapping):
        mapping = copy.deepcopy(self.app.config['mappings'][model_mapping])
        transformed_obj = json_transform.transform(mapping, payload)
        return transformed_obj

    
    def extra_tags(self, tags):
        extra_updated = copy.deepcopy(self.extra)
        extra_updated.update({'payload': tags})
        return extra_updated


    def generate_messages(self):
        messages = []
        for error in self.errors:
            msg = {}
            msg['msgCd'] = 100
            msg['msgDesc'] = error
            messages.append(msg)
        return messages