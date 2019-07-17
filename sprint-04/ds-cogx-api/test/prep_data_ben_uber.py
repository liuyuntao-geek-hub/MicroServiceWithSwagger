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

with open('sample/ben/mapping-uber.yml', 'r') as f:
    mapping = yaml.load(f)



fp = open('sample/ben/formatted_uber.generated', 'w')
files = ['sample/ben/formatted_input.generated']
#fp = open('sample/ben/formatted_uber.gen', 'w')
#files = ['sample/ben/formatted_input.gen']
with open(files[0], encoding='ISO-8859-1') as f:
    claim_nbr = '-xyz'
    obj_first = None
    
    for cnt, line in enumerate(f): 
        obj = json_transform.transform(copy.deepcopy(mapping), json.loads(line))      
        convert_to_null(obj)
        prov_lcns = obj['PROV_LCNS_ALPH_CD']
        prov_lcns_splits = prov_lcns.split()
        obj['PROV_LCNS_ALPH_CD'] = None
        if len(prov_lcns_splits) > 1:
            if (makeNull(prov_lcns_splits[0]) is not None):
                obj['PROV_LCNS_ALPH_CD'] = prov_lcns_splits[0]
                obj['PROV_LCNS_NMRC_CD'] = prov_lcns_splits[1]
        else:
            obj['PROV_LCNS_NMRC_CD'] = prov_lcns_splits[0]
            obj['PROV_LCNS_ALPH_CD'] = prov_lcns_splits[0]
        lmt_cls = obj.get('icdPrimaryCds', None)
        if (lmt_cls is not None):
            lmt_cls_array = []
            for idx, icdPrimaryCd in enumerate(obj['icdPrimaryCds']):
                if makeNull(icdPrimaryCd['LMT_CLS']) is not None:
                    lmt_cls_array.append(makeNull(icdPrimaryCd['LMT_CLS']))
            obj['LMT_CLS'] = lmt_cls_array
            del obj['icdPrimaryCds']
        err_cds = obj.get('ERR_CDS', None)
        if (err_cds is not None):     
            err_cds_array = []       
            for idx, err_cd in enumerate(obj['ERR_CDS']):
                if makeNull(err_cd['ERR_CD']) is not None:
                    err_cds_array.append(makeNull(err_cd['ERR_CD']))
            obj['ERR_CDS'] = err_cds_array
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
        
        '''
        if PRMRY_NTWK_CD is None:
                trans_data['PROD_NTWK'] = None

            elif PRMRY_NTWK_CD in ('F', '0'):
                trans_data['PROD_NTWK'] = 'FSA'
            elif PRMRY_NTWK_CD in ('1', 'H', 'D', '9'):
                trans_data['PROD_NTWK'] = 'FFS'
            elif PRMRY_NTWK_CD in ('2', 'V', '8'):
                trans_data['PROD_NTWK'] = 'HMO'
            elif PRMRY_NTWK_CD in ('3', '5', 'A', 'C', 'I', 'K', 'O', 'U', 'E', 'S', 'R'):
                trans_data['PROD_NTWK'] = 'PPO'
            elif PRMRY_NTWK_CD in ('4','6','P'):
                trans_data['PROD_NTWK'] = 'POS'
            elif PRMRY_NTWK_CD in ('7'):
                trans_data['PROD_NTWK'] = 'EPO'
            else:
                trans_data['PROD_NTWK'] = PRMRY_NTWK_CD
        '''
        ntwk = makeNull(obj['PRMRY_NTWK_CD'])
        if ntwk is not None:
            if ntwk == 'FSA':
                obj['PRMRY_NTWK_CD'] = 'F'
            elif ntwk == 'FFS':
                obj['PRMRY_NTWK_CD'] = '1'
            elif ntwk == 'HMO':
                obj['PRMRY_NTWK_CD'] = '2'
            elif ntwk == 'PPO':
                obj['PRMRY_NTWK_CD'] = '3'
            elif ntwk == 'POS':
                obj['PRMRY_NTWK_CD'] = '4'
            elif ntwk == 'EPO':
                obj['PRMRY_NTWK_CD'] = '7'

        '''
        PROV_IND = payload.get('PROV_IND', None)
        ITS_HOME_IND = payload.get('ITS_HOME_IND', None)
        ITS_ORGNL_SCCF_NEW_NBR = payload.get('ITS_ORGNL_SCCF_NEW_NBR', None)
        ITS_PARG_PROV_IND = payload.get('ITS_PARG_PROV_IND', None)

        if (PROV_IND not in ('D', 'N') or (
                ITS_HOME_IND == 'Y' and ITS_ORGNL_SCCF_NEW_NBR is not None and ITS_PARG_PROV_IND in ('P', 'Y'))):
            trans_data['PRVDR_STATUS'] = 'PAR'
        else:
            trans_data['PRVDR_STATUS'] = 'NON-PAR'
        '''
        
        status = makeNull(obj['PRVDR_STATUS'])
        del obj['PRVDR_STATUS']
        if status is not None:
            if status == 'PAR':
                # dummy value
                obj['PROV_IND'] = 'Y'
                # obj['ITS_HOME_IND'] = 'Y'
                # obj['ITS_ORGNL_SCCF_NEW_NBR'] = 'Y'
                # obj['ITS_PARG_PROV_IND'] = 'Y'
            else: 
                obj['PROV_IND'] = 'D'

        if (obj['KEY_CHK_DCN_NBR'] != claim_nbr):
            if (obj_first is not None):
                fp.write(json.dumps(obj_first) + '\n')
            obj_first = obj
            claim_nbr = obj['KEY_CHK_DCN_NBR']
        else:
            obj_first['ERR_CDS'] = list(set(obj_first.get('ERR_CDS',[].extend(obj.get('ERR_CDS', [])))))
            obj_first.get('claimDetails', []).append(*obj.get('claimDetails')) 
    fp.write(json.dumps(obj_first) + '\n')
    fp.flush()
    fp.close()