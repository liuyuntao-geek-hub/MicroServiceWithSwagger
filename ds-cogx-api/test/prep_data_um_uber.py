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


with open('sample/um/mapping-uber.yml', 'r') as f:
    mapping = yaml.load(f)

fp = open('sample/um/formatted_uber.generated', 'w')
files = ['sample/um/formatted_input.generated']
with open(files[0], encoding='ISO-8859-1') as f:
    for cnt, line in enumerate(f):
        obj = json_transform.transform(copy.deepcopy(mapping), json.loads(line)) 
        icd = obj.get('icdPrimaryCds', None)
        if (icd is not None):
            print(type(obj['icdPrimaryCds']))
            icds = obj['icdPrimaryCds'][5:]
            obj['icdPrimaryCds'] = obj['icdPrimaryCds'][0:5]
            ICDS = []
            for i in icds:
                ICDS.append(i['ICD_CD'])
            obj['ICD_CDS'] = ICDS
        age = obj.get('PAT_MBR_CD', '0')
        obj['PAT_MBR_CD'] =  int(age)
        id = obj.get('SBSCRBR_CERTFN_1_NBR', '000000000')
        obj['SBSCRBR_CERTFN_1_NBR'] = id[0:3]
        obj['SBSCRBR_CERTFN_2_NBR'] = id[3:5]
        obj['SBSCRBR_CERTFN_3_NBR'] = id[5:]
        obj['SRVC_FROM_DT'] = datetime.datetime.strptime(obj['SRVC_FROM_DT'], '%m/%d/%Y').strftime('%Y-%m-%d')
        obj['SRVC_THRU_DT'] = datetime.datetime.strptime(obj['SRVC_THRU_DT'], '%m/%d/%Y').strftime('%Y-%m-%d')
        obj['CLM_TYPE_CD'] = 'MM'
        obj['BYPS_CDS'] = []
        
        qty = obj['claimDetails'][0].get('BILLD_SRVC_UNIT_QTY', 0)
        obj['claimDetails'][0]['KEY_CHK_DCN_NBR'] = obj['KEY_CHK_DCN_NBR']        
        obj['claimDetails'][0]['UM_PROV_SQNC_NBR'] = '01'
        obj['claimDetails'][0]['DTL_LINE_NBR'] = '01'
        if obj['claimDetails'][0]['SRVC_FROM_DT'] == 'None':
            obj['claimDetails'][0]['SRVC_FROM_DT'] = obj['SRVC_FROM_DT']
        else:
            obj['claimDetails'][0]['SRVC_FROM_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['SRVC_FROM_DT'], '%m/%d/%Y').strftime('%Y-%m-%d')

        if obj['claimDetails'][0]['SRVC_TO_DT'] == 'None':
            obj['claimDetails'][0]['SRVC_TO_DT'] = obj['SRVC_THRU_DT']
        else:
            obj['claimDetails'][0]['SRVC_TO_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['SRVC_TO_DT'], '%m/%d/%Y').strftime('%Y-%m-%d')
        
        obj['claimDetails'][0]['BILLD_SRVC_UNIT_QTY'] = int(qty)
        fp.write(json.dumps(obj) + '\n')
