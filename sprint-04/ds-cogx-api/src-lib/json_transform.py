import configparser

def process_string(key, extract_from):
    value = extract_from
    tokens = key.split('.')
    for token in tokens:
        if value is None:
            return None
        array_tokens = token.split('#')
        if len(array_tokens) == 1:
            if str(array_tokens[0]).isdigit() :
                if (value is not None and len(value) > int(array_tokens[0])):
                    value = str(value[int(array_tokens[0])])
                else:
                    value = None
            else:
                value = value.get(array_tokens[0], None)
        elif len(array_tokens) == 2:
            value = value.get(array_tokens[0], None)
            if (value is not None and len(value) > int(array_tokens[1])):
                value = value[int(array_tokens[1])]

            else:
                value = None
        #print("token : "+str(token) +" Array_token :"+str(array_tokens) +" value : "+str(value))


    if isinstance(value, str):
        value = value.strip()

    return value


def transform(mapping, extract_from):
    '''

    :param mapping: mapping yml file contains all the fields mapping with payload
    :param extract_from: payload
    :return: transform dict

    mapping yml file :
    left_Attribute_name : payload_mapping_attribute_name
    sample notations :
    CURNT_NUM : CURNT_QUE_UNIT_NBR --> Need CURRNT_NUM in transform output using payload.get('CURNT_QUE_UNIT_NBR')
    ICD_A_CD: icdPrimaryCds#1.ICD_CD --> Need ICD_A_CD in transform output from payload having below structure :
            icdPrimaryCds": [
            {
                "ICD_CD": "Z3801",
                "OTHER_FIELDS":"XYZ"
            },
            {
                "ICD_CD": "P220"  <-----ICD_CD at index on 1 from array icdPrimaryCds
            }]
    ICD_OTHR_8_CD: ICD_CDS.3  --> NEED ICD_OTHR_6_CD in transform output from payload having below structure :
            "ICD_CDS": [
                "*",
                "*",
                "*",
                "*",    <--- index on 3 from array ICD_CDS
                "*"
            ],
    limitClass_1: icdPrimaryCds#0.limitClass.0 -->
     icdPrimaryCds": [
    {
      "ICD_CD": "Z3801",
      "limitClass": [
        "1",
        "2"
      ]
    }
    ]
    '''
    mapping_keys = mapping.keys()
    for mapping_key in mapping_keys:
        if (isinstance(mapping[mapping_key], str)):
            key = str(mapping[mapping_key])        
            mapping[mapping_key] = process_string(key, extract_from)
        elif (isinstance(mapping[mapping_key], list)):
            for elem in mapping[mapping_key]:
                transform(elem, extract_from)
        elif (isinstance(mapping[mapping_key], dict)):
            transform(mapping[mapping_key], extract_from)

    return mapping


