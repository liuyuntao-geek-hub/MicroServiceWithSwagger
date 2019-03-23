import configparser


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