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



with open('sample/ltr/mapping-uber-ltr.yml', 'r') as f:
    mapping = yaml.load(f)
fp = open('sample/ltr/formatted_uber.generated', 'w')
files = ['sample/ltr/formatted_input.generated']
with open(files[0], encoding='ISO-8859-1') as f:
    for cnt, line in enumerate(f):
        print(cnt)
        obj_original = json.loads(line)
        details_obj = obj_original['details']
        #print(type(details)) --list [{}.{}]
        details_transformed = []
        obj = None
        for detail in details_obj:
            #print("detail:",detail) -dict
            obj_original['details'] = [detail]
            obj = json_transform.transform(copy.deepcopy(mapping), obj_original)
            obj['ORGNL_ENTRY_DT'] = datetime.datetime.strptime(obj['ORGNL_ENTRY_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['SRVC_FROM_DT'] = datetime.datetime.strptime(obj['SRVC_FROM_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['SRVC_THRU_DT'] = datetime.datetime.strptime(obj['SRVC_THRU_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            '''
            if(obj['CLM_TYPE_CD']) == 'PROF':
                obj['CLM_TYPE_CD'] = 'MA'
            elif(obj['CLM_TYPE_CD']) == "INPT":
                obj['CLM_TYPE_CD'] = 'IA'
            elif(obj['CLM_TYPE_CD']) == "OUTPT":
                obj['CLM_TYPE_CD'] = 'OA'
            elif(obj['CLM_TYPE_CD']) == "SN":
                obj['CLM_TYPE_CD'] = 'SA'
            else:
                obj['CLM_TYPE_CD'] = obj['CLM_TYPE_CD']
            '''


            """
            if (PROV_IND not in ('D', 'N') or (
            ITS_HOME_IND == 'Y' and ITS_ORGNL_SCCF_NEW_NBR is not None and ITS_PARG_PROV_IND in ('P', 'Y'))):
                trans_data['PRVDR_STATUS'] = 'PAR'
            else:
                trans_data['PRVDR_STATUS'] = 'NON-PAR'
            
            
            """
            if (obj['PROV_IND'] not in ('D','N')) or (obj['ITS_HOME_IND'] == 'NON-PAR' and obj['ITS_ORGNL_SCCF_NEW_NBR'] is not None and obj['ITS_PARG_PROV_IND'] in ('P','Y')):
                obj['ITS_HOME_IND'] = 'Y'
            else:
                obj['ITS_HOME_IND'] = 'X'

            convert_to_null(obj)
            obj['claimDetails'][0]['BNFT_YEAR_CNTRCT_EFCTV_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['BNFT_YEAR_CNTRCT_EFCTV_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['BNFT_YEAR_CNTRCT_REV_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['BNFT_YEAR_CNTRCT_REV_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['MBR_CNTRCT_EFCTV_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['MBR_CNTRCT_EFCTV_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['MBR_CNTRCT_END_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['MBR_CNTRCT_END_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['SRVC_FROM_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['SRVC_FROM_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['SRVC_TO_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['SRVC_TO_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            bll_chrg_amt = obj['claimDetails'][0].get('BILLD_CHRGD_AMT', 0.00)
            obj['claimDetails'][0]['BILLD_CHRGD_AMT'] = float(bll_chrg_amt.replace(",", "."))
            med_apr_amt = obj['claimDetails'][0].get('MEDCRB_APRVD_AMT', 0.00)
            obj['claimDetails'][0]['MEDCRB_APRVD_AMT'] = float(med_apr_amt.replace(",", "."))
            med_coin_amt = obj['claimDetails'][0].get('MEDCRB_COINSRN_AMT', 0.00)
            obj['claimDetails'][0]['MEDCRB_COINSRN_AMT'] = float(med_coin_amt.replace(",", "."))
            med_ddc_amt = obj['claimDetails'][0].get('MEDCRB_DDCTBL_AMT', 0.00)
            obj['claimDetails'][0]['MEDCRB_DDCTBL_AMT'] = float(med_ddc_amt.replace(",", "."))
            med_paid_amt = obj['claimDetails'][0].get('MEDCRB_PAID_AMT', 0.00)
            obj['claimDetails'][0]['MEDCRB_PAID_AMT'] = float(med_paid_amt.replace(",", "."))
            details_transformed.append(*obj['claimDetails'])

        if(obj == None):
            obj = json_transform.transform(copy.deepcopy(mapping), obj_original)
            convert_to_null(obj)
            del obj['claimDetails']
        else:
            obj['claimDetails'] = details_transformed
        ntwk = obj.get('PROD_NTWK', None)
        if ntwk == 'FSA':
            ntwk = 'F'
        elif  ntwk == 'FFS':
            ntwk = 'H'
        elif ntwk == 'HMO':
            ntwk = 'V'
        elif ntwk == 'PPO':
            ntwk = 'K'
        elif ntwk == 'POS':
            ntwk = 'P'
        elif ntwk == 'EPO':
            ntwk = '7'
        obj['PRMRY_NTWK_CD'] = ntwk
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
            err_cds.append(edit.get("ERR_14_CD"))
            err_cds.append(edit.get("ERR_15_CD"))
            err_cds.append(edit.get("ERR_16_CD"))
            err_cds.append(edit.get("ERR_17_CD"))
            err_cds.append(edit.get("ERR_18_CD"))
            err_cds.append(edit.get("ERR_19_CD"))
            err_cds.append(edit.get("ERR_20_CD"))
            err_cds.append(edit.get("ERR_21_CD"))
            err_cds.append(edit.get("ERR_22_CD"))
            err_cds.append(edit.get("ERR_23_CD"))
            err_cds.append(edit.get("ERR_24_CD"))
            err_cds.append(edit.get("ERR_25_CD"))
            err_cds.append(edit.get("ERR_26_CD"))
            err_cds.append(edit.get("ERR_27_CD"))
            err_cds.append(edit.get("ERR_28_CD"))
            err_cds.append(edit.get("ERR_29_CD"))
            err_cds.append(edit.get("ERR_30_CD"))
            err_cds.append(edit.get("ERR_31_CD"))
            err_cds.append(edit.get("ERR_32_CD"))
        obj['ERR_CDS'] = list(set(err_cds))
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
                if i['ICD_CD'] is not None:
                    ICDS.append(i['ICD_CD'])
            obj['ICD_CDS'] = ICDS
        lmt_cls = obj.get('LMT_CLS', [])
        lmt_cls_cds = []
        for cls_cd in lmt_cls:
            if cls_cd['LMT_CLS_CD'] is not None:
                lmt_cls_cds.append(cls_cd['LMT_CLS_CD'])
        obj['LMT_CLS'] = lmt_cls_cds
        cob_seg_cd = obj.get('COB_SGMNT_CNT', 0)
        obj['COB_SGMNT_CNT'] = int(cob_seg_cd)
        cob_bsc_pay_cd = obj.get('COB_BSIC_PAYMNT_AMT', 0.00)
        obj['COB_BSIC_PAYMNT_AMT'] = float(cob_bsc_pay_cd.replace(",", "."))
        cb_mm_pay_cd = obj.get('COB_MM_PAYMNT_AMT', 0.00)
        obj['COB_MM_PAYMNT_AMT'] = float(cb_mm_pay_cd.replace(",", "."))
        mdcr_cnt = obj.get('MEDCR_CNT', 0.00)
        obj['MEDCR_CNT'] = float(mdcr_cnt.replace(",", "."))
        chrg_amt = obj.get('TOTL_CHRG_AMT', 0.00)
        obj['TOTL_CHRG_AMT'] = float(chrg_amt.replace(",", "."))
        pat_age = obj.get('PAT_AGE_NBR', 0)
        obj['PAT_AGE_NBR'] = int(pat_age)

        fp.write(json.dumps(obj) + '\n')

