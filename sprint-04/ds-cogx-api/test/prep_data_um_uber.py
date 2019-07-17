import yaml
import os
import sys
import json
import configparser
import copy
import datetime
PATH = os.path.dirname(os.path.abspath(__file__))
APP_PATH = os.path.split(PATH)[0]
sys.path.append(APP_PATH+"/src-lib/")
import json_transform


def makeNull(inp):
    if inp is not None and isinstance(inp, str):
        inp = inp.strip()
        if inp == '*' or len(inp) == 0:
            return None
        return inp
    return inp


def convert_to_null(obj):
    if isinstance(obj, dict):
        for k, v in obj.items():
            if isinstance(v, list):
                for e in v:
                    convert_to_null(e)
            elif isinstance(v, dict):
                convert_to_null(v)
            else:
                obj[k] = makeNull(v)

with open('sample/um/mapping-uber.yml', 'r') as f:
    mapping = yaml.load(f)

print(json.dumps(mapping, indent=2))


fp = open('sample/um/formatted_uber.generated', 'w')
files = ['sample/um/formatted_input.generated']
with open(files[0], encoding='ISO-8859-1') as f:
    claim_nbr = '-xyz'
    obj_first = None
    for cnt, line in enumerate(f):     
        print(cnt)   
        obj = json_transform.transform(copy.deepcopy(mapping), json.loads(line))         
        convert_to_null(obj)
        icd = obj.get('icdPrimaryCds', None)
        if (icd is not None):
            icds = obj['icdPrimaryCds'][5:]
            obj['icdPrimaryCds'] = obj['icdPrimaryCds'][0:5]
            idx_1 = 5
            for idx, icdPrimaryCd in enumerate(obj['icdPrimaryCds']):
                if icdPrimaryCd['ICD_CD'] is None:
                    idx_1 = idx
                    break
            obj['icdPrimaryCds'] = obj['icdPrimaryCds'][0:idx_1]
            ICDS = []
            for i in icds:
                if i['ICD_CD'] is not None :
                    ICDS.append(i['ICD_CD'])
            obj['ICD_CDS'] = ICDS
        age = obj.get('PAT_MBR_CD', '0')
        obj['PAT_MBR_CD'] =  int(age)
        id = obj.get('SBSCRBR_CERTFN_1_NBR', '000000000')
        obj['SBSCRBR_CERTFN_1_NBR'] = id[0:3]
        obj['SBSCRBR_CERTFN_2_NBR'] = id[3:5]
        obj['SBSCRBR_CERTFN_3_NBR'] = id[5:]
        if obj['CLAIM_TYPE'] == 'PROF':
            obj['CLM_TYPE_CD'] = 'MA'
        elif obj['CLAIM_TYPE'] == 'INPT':
            obj['CLM_TYPE_CD'] = 'IA'
        elif obj['CLAIM_TYPE'] == 'OUTPT':
            obj['CLM_TYPE_CD'] = 'OA'
        elif obj['CLAIM_TYPE'] == 'SN':
            obj['CLM_TYPE_CD'] = 'SA'
        else:
            obj['CLM_TYPE_CD'] = obj['CLAIM_TYPE']
        obj['BYPS_CDS'] = []
        obj['SRVC_FROM_DT'] = datetime.datetime.strptime(obj['SRVC_FROM_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
        obj['SRVC_THRU_DT'] = datetime.datetime.strptime(obj['SRVC_THRU_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
        
        qty = obj['claimDetails'][0].get('BILLD_SRVC_UNIT_QTY', 0)
        obj['claimDetails'][0]['KEY_CHK_DCN_NBR'] = obj['KEY_CHK_DCN_NBR']
        obj['claimDetails'][0]['SRVC_FROM_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['SRVC_FROM_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
        obj['claimDetails'][0]['SRVC_TO_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['SRVC_TO_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
        obj['claimDetails'][0]['BILLD_SRVC_UNIT_QTY'] = int(qty)

        if (obj['KEY_CHK_DCN_NBR'] != claim_nbr):
            if (obj_first is not None):
                fp.write(json.dumps(obj_first) + '\n')
            obj_first = obj
            claim_nbr = obj['KEY_CHK_DCN_NBR']
        else:
            obj_first.get('claimDetails', []).append(*obj.get('claimDetails'))
        
    fp.write(json.dumps(obj_first) + '\n')
    fp.flush()