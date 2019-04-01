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


with open('sample/ltr/mapping-uber-ltr.yml', 'r') as f:
    mapping = yaml.load(f)
fp = open('sample/ltr/formatted_uber.generated', 'w')
files = ['sample/ltr/formatted_input.generated']
with open(files[0], encoding='ISO-8859-1') as f:
    for cnt, line in enumerate(f):
        #print(cnt)
        obj_original = json.loads(line)
        details_obj = obj_original['details']
        #print(type(details)) --list [{}.{}]
        details_transformed = []
        obj = None
        for detail in details_obj:
            #print("detail:",detail) -dict
            obj_original['details'] = [detail]
            obj = json_transform.transform(copy.deepcopy(mapping), obj_original)
            details_transformed.append(*obj['claimDetails'])
        if(obj == None):
            obj = json_transform.transform(copy.deepcopy(mapping), obj_original)
            del obj['claimDetails']
        else:
            obj['claimDetails'] = details_transformed
        edits = obj_original.get('edits', [])
        err_cds = []
        for edit in edits:
            err_cds.append(edit.get("ERR_1_CD"))
            err_cds.append(edit.get("ERR_2_CD"))
            err_cds.append(edit.get("ERR_3_CD"))
            err_cds.append(edit.get("ERR_4_CD"))
            err_cds.append(edit.get("ERR_5_CD"))
            err_cds.append(edit.get("ERR_6_CD"))
            err_cds.append(edit.get("ERR_7_CD"))
            err_cds.append(edit.get("ERR_8_CD"))
            err_cds.append(edit.get("ERR_9_CD"))
            err_cds.append(edit.get("ERR_10_CD"))
            err_cds.append(edit.get("ERR_11_CD"))
            err_cds.append(edit.get("ERR_12_CD"))
            err_cds.append(edit.get("ERR_13_CD"))
        obj['ERR_CDS'] = list(set(err_cds))
        icd = obj.get('icdPrimaryCds', None)
        if (icd is not None):
            #print(type(obj_original['icdPrimaryCds']))
            icds = obj['icdPrimaryCds'][5:]
            obj['icdPrimaryCds'] = obj['icdPrimaryCds'][0:5]
            ICDS = []
            for i in icds:
                ICDS.append(i['ICD_CD'])
            obj['ICD_CDS'] = ICDS
        obj['SRVC_FROM_DT'] = datetime.datetime.strptime(obj['SRVC_FROM_DT'], '%m/%d/%Y').strftime('%Y-%m-%d')
        obj['SRVC_THRU_DT'] = datetime.datetime.strptime(obj['SRVC_THRU_DT'], '%m/%d/%Y').strftime('%Y-%m-%d')
        obj['ORGNL_ENTRY_DT'] = datetime.datetime.strptime(obj['ORGNL_ENTRY_DT'], '%m/%d/%Y').strftime('%Y-%m-%d')
        fp.write(json.dumps(obj) + '\n')

