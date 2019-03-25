import configparser
from os.path import join, dirname,split
import json
import jsonschema
from jsonschema import validate


def process_string(key, extract_from):
    value = extract_from
    tokens = key.split('.')
    for token in tokens:
        array_tokens = token.split('#')
        if len(array_tokens) == 1:
            value = value.get(token, None)
        elif len(array_tokens) == 2:
            value = value.get(array_tokens[0], None)
            if (value is not None and len(value) >= int(array_tokens[1])):
                value = value[int(array_tokens[1])]
            else:
                value = {}
    return value


def transform(mapping, extract_from):
    mapping_keys = mapping.keys()
    for mapping_key in mapping_keys:
        # print(mapping_key)
        if (isinstance(mapping[mapping_key], str)):
            key = str(mapping[mapping_key])        
            mapping[mapping_key] = process_string(key, extract_from)
        elif (isinstance(mapping[mapping_key], list)):
            for elem in mapping[mapping_key]:
                transform(elem, extract_from)
        elif (isinstance(mapping[mapping_key], dict)):
            transform(mapping[mapping_key], extract_from)
    return mapping

def _load_json_schema(filename,folder_name='src-lib'):
    """ Loads the given schema file """
    try:
        relative_path = join(folder_name, filename)
        absolute_path = join(split(dirname(__file__))[0], relative_path)
        print(absolute_path)
        with open(absolute_path) as schema_file:
            return json.loads(schema_file.read())
    except Exception as e:
        print(e.__str__())

def assert_valid_schema(data, schema_file,folder_name):
    """ Checks whether the given data matches the schema """
    debug=True
    resp = []
    schema = _load_json_schema(schema_file,folder_name)
    try:
        if debug :print("ENTER TRY")
        x = validate(data, schema, format_checker=jsonschema.FormatChecker())
        if debug: print("EXIT TRY")

    except jsonschema.exceptions.ValidationError as ve:
        if debug:
            pass
           #sys.stderr.write("Record #{}: ERROR\n".format(ve))
           #sys.stderr.write(str(ve) + "\n")

    return resp

def make_resp(error_code,message):
    pass