import jsonschema
from jsonschema import validate
import logging
logger = logging.getLogger('logfile')


def assert_valid_data(data, schema, validator = 'Draft7Validator', tags={}):
    """ Checks whether the given data matches the schema """

    errors = []
    try:
        if validator == 'Draft7Validator':
            v = jsonschema.Draft7Validator(schema).is_valid(data, schema)

        else:
            # TODO :load validator module dynamically with validator name like  'module_name.validator name'
            # validator_arr = validator.split('.')
            # mod = __import__(validator_arr[0])
            # func = getattr(mod,validator_arr[1:],'Draft7Validator')
            v = jsonschema.Draft7Validator(schema).is_valid(data, schema)

        if (v != True):
            z = jsonschema.Draft7Validator(schema)
            for error in sorted(z.iter_errors(data), key=str):
                errors.append(str(error.schema.get('title')) + " - " + str(error.message))

    #TODO: Remove below validationError and we can also remove schemaerror handling once we have schema issue in @app.before_first_call
    except jsonschema.exceptions.ValidationError as ve:
        if ve.relative_schema_path[0] == 'required':
            errors.append({ve.message.split(' ')[0]: 'Required property'})
        elif ve.relative_path:
            # field: error_message
            errors.append({ve.relative_path[1]: ve.message})
            # Additional Field was found
        else:
            errors.append({ve.instance.keys()[0]: ve.message})

        logger.error("Validation error in payload : "+str(data) + " ERROR :"+str(errors), extra=tags)
        
    except jsonschema.exceptions.SchemaError as se:
        errors.append("schema error")
        logger.error("Schema error in file : " +schema +"Please check schema using check_schema methods before initiate api", extra=tags)
        errors.append("Scheme error in file " + schema) 

    except Exception as e:
        errors.append("Exception occur : " + str(e))
        logger.error("Exception occur : " + str(e), extra=tags)
        errors.append("Exception occured in validation")

    return errors