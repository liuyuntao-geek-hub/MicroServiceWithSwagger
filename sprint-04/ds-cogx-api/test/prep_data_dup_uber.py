import yaml
import os
import sys
import json
import configparser
import copy
import datetime

PATH = os.path.dirname(os.path.abspath(__file__))
APP_PATH = os.path.split(PATH)[0]
sys.path.append(APP_PATH + "/src-lib/")
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


with open('sample/dup/mapping-uber-dup.yml', 'r') as f:
    mapping = yaml.load(f)
fp = open('sample/dup/formatted_uber.generated', 'w')
files = ['sample/dup/formatted_input.generated']
with open(files[0], encoding='ISO-8859-1') as f:
    x =0
    for cnt, line in enumerate(f):
        if x ==-11:
            break
        x=x+1
        print(cnt)
        obj_original = json.loads(line)
        details_obj = obj_original['details']
        # print(type(details)) --list [{}.{}]
        details_transformed = []
        obj = None
        for detail in details_obj:
            # print("detail:",detail) -dict
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
            if (obj['PROV_IND'] not in ('D', 'N')) or (
                    obj['ITS_HOME_IND'] == 'NON-PAR' and obj['ITS_ORGNL_SCCF_NEW_NBR'] is not None and obj[
                'ITS_PARG_PROV_IND'] in ('P', 'Y')):
                obj['ITS_HOME_IND'] = 'Y'
            else:
                obj['ITS_HOME_IND'] = 'X'
            convert_to_null(obj)
            qty = obj['claimDetails'][0].get('BILLD_SRVC_UNIT_QTY', 0)
            obj['claimDetails'][0]['BILLD_SRVC_UNIT_QTY'] = int(qty)
            obj['claimDetails'][0]['MBR_CNTRCT_EFCTV_DT'] = datetime.datetime.strptime(
                obj['claimDetails'][0]['MBR_CNTRCT_EFCTV_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['BNFT_YEAR_CNTRCT_EFCTV_DT'] = datetime.datetime.strptime(
                obj['claimDetails'][0]['BNFT_YEAR_CNTRCT_EFCTV_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['BNFT_YEAR_CNTRCT_REV_DT'] = datetime.datetime.strptime(
                obj['claimDetails'][0]['BNFT_YEAR_CNTRCT_REV_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['MBR_CNTRCT_EFCTV_DT'] = datetime.datetime.strptime(
                obj['claimDetails'][0]['MBR_CNTRCT_EFCTV_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['MBR_CNTRCT_END_DT'] = datetime.datetime.strptime(
                obj['claimDetails'][0]['MBR_CNTRCT_END_DT'], '%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['CLM_CMPLTN_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['CLM_CMPLTN_DT'],'%m/%d/%Y').strftime('%m/%d/%Y')
            obj['claimDetails'][0]['SRVC_FROM_DT'] = datetime.datetime.strptime(obj['claimDetails'][0]['SRVC_FROM_DT'],'%m/%d/%Y').strftime('%m/%d/%Y')
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

        if (obj == None):
            obj = json_transform.transform(copy.deepcopy(mapping), obj_original)
            convert_to_null(obj)
            del obj['claimDetails']
        else:
            obj['claimDetails'] = details_transformed
        '''
                if 'PROD_NTWK' in [x.upper() for x in derived_fields]:
            PRMRY_NTWK_CD = payload.get('PRMRY_NTWK_CD', 'None')
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
        ntwk = obj.get('PROD_NTWK', None)
        if ntwk == 'FSA':
            ntwk = 'F'
        elif ntwk == 'FFS':
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
        age = obj.get('PAT_MBR_CD', '0')
        obj['PAT_MBR_CD'] =  int(age)
        cob_seg_cd = obj.get('COB_SGMNT_CNT', 0)
        obj['COB_SGMNT_CNT'] = int(cob_seg_cd)
        bsc_pay_cd = obj.get('BSIC_PAYMNT_AMT', 0.00)
        obj['BSIC_PAYMNT_AMT'] = float(bsc_pay_cd.replace(",", "."))
        cob_bsc_pay_cd = obj.get('COB_BSIC_PAYMNT_AMT', 0.00)
        obj['COB_BSIC_PAYMNT_AMT'] = float(cob_bsc_pay_cd.replace(",", "."))
        cb_mm_pay_cd = obj.get('COB_MM_PAYMNT_AMT', 0.00)
        obj['COB_MM_PAYMNT_AMT'] = float(cb_mm_pay_cd.replace(",", "."))
        mm_pay = obj.get('MM_PAY_AMT', 0.00)
        obj['MM_PAY_AMT'] = float(mm_pay.replace(",", "."))
        mdcr_cnt = obj.get('MEDCR_CNT', 0.00)
        obj['MEDCR_CNT'] = float(mdcr_cnt.replace(",", "."))
        chrg_amt = obj.get('TOTL_CHRG_AMT', 0.00)
        obj['TOTL_CHRG_AMT'] = float(chrg_amt.replace(",", "."))
        pat_age = obj.get('PAT_AGE_NBR', 0)
        obj['PAT_AGE_NBR'] = int(pat_age)
        ssn = obj['MEMBER_SSN']
        if len(ssn) == 9:
            obj['SBSCRBR_CERTFN_1_NBR'] = ssn[0:3]
            obj['SBSCRBR_CERTFN_2_NBR'] = ssn[3:5]
            obj['SBSCRBR_CERTFN_3_NBR'] = ssn[5:9]
        del obj['MEMBER_SSN']

        fp.write(json.dumps(obj) + '\n')

