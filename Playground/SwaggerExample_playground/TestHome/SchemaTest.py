import socket
import sys
import json
import jsonschema
from jsonschema import validate
from os.path import join, dirname
from flask import jsonify, request, make_response, Flask


debug=True



app = Flask(__name__)

@app.route('/processUM', methods=['POST'])
def um_validation_api():
    resp = {}

    try:

        payload = request.get_json(silent=True)
        #payload = request.data
        if debug : print(payload)
        schema = _load_json_schema('UM_Schema.json')
        print(schema)
        print(type(payload))
        list =[]
        list.append(str(payload))
        #payload = '{"userId":"Not_used_in_UM","KEY_CHK_DCN_NBR":"18345CO6693","KEY_CHK_DCN_CENTRY_CD":"Not_used_in_UM","KEY_CHK_DCN_ITEM_CD":"Not_used_in_UM","BHVRL_HLTH_ACS_PROV_IND":"Not_used_in_UM","ENCNTR_IND":"Not_used_in_UM","ITS_HOME_IND":"Y","ITS_PARG_PROV_IND":"Y","MBU_CD":"Not_used_in_UM","MK_FUND_TYPE_CD":"Not_used_in_UM","PRMRY_NTWK_CD":"Not_used_in_UM","PROV_IND":"Not_used_in_UM","ATCHMNT_IND":"Not_used_in_UM","BYPS_CDS":"SRUM* **","CLM_TYPE_CD":"INPT","DTL_LINE_NBR":"01","EDI_CLM_FLNG_IND":"Not_used_in_UM","PROV_ST_CD":"Not_used_in_UM","SBSCRBR_ST_CD":"Not_used_in_UM","SRC_CD":"Not_used_in_UM","SRVC_TYPE_CURNT_CD":"Not_used_in_UM","SRVC_TYPE_ORGNL_CD":"Not_used_in_UM","PROV_LCNS_ALPH_CD":"Not_used_in_UM","PROV_SPCLTY_CD":"Not_used_in_UM","ORGNL_ENTRY_DT":"Not_used_in_UM","SRVC_FROM_DT":"10/19/2018","SRVC_THRU_DT":"12/6/2018","BSIC_PAYMNT_AMT":"Not_used_in_UM","COB_BSIC_PAYMNT_AMT":"Not_used_in_UM","COB_MM_PAYMNT_AMT":"Not_used_in_UM","DDCTBL_AMT":"Not_used_in_UM","MM_PAY_AMT":"Not_used_in_UM","RJCT_AMT":"Not_used_in_UM","TOTL_CHRG_AMT":"Not_used_in_UM","TOTL_NON_ELGBL_AMT":"Not_used_in_UM","WRTOF_AMT":"Not_used_in_UM","PAT_MBR_CD":"50","PAT_AGE_NBR":"Not_used_in_UM","COB_SGMNT_CNT":"Not_used_in_UM","MEDCR_CNT":"Not_used_in_UM","LPP_INT_AMT":"Not_used_in_UM","PROV_CITY_NM":"Not_used_in_UM","SBSCRBR_CITY_NM":"Not_used_in_UM","ITS_ORGNL_SCCF_NEW_NBR":"*","PROV_NM":"GOODSAMARITANHOSPITAL","PROV_SCNDRY_NM":"SAMARITAN HOSPITAL","ERR_CDS":"Not_used_in_UM","PROV_ZIP_4_CD":"Not_used_in_UM","SBSCRBR_ZIP_4_CD":"Not_used_in_UM","PROV_ZIP_5_CD":"Not_used_in_UM","SBSCRBR_ZIP_5_CD":"Not_used_in_UM","icdPrimaryCds":"Not_used_in_UM","ICD_CD":["Z3801","P220","P612","P711","P284"],"limitClass":"Not_used_in_UM","FULL_MNGMNT_BU_CD":"Not_used_in_UM","PROV_LCNS_NMRC_CD":"Not_used_in_UM","PROV_TAX_ID":"621763090","ICD_CDS":"Not_used_in_UM","SBSCRBR_CERTFN_1_NBR":"619","SBSCRBR_CERTFN_2_NBR":"543","SBSCRBR_CERTFN_3_NBR":"610","CURNT_QUE_UNIT_NBR":"F6","claimDetails":"Not_used_in_UM","MBR_CNTRCT_TYPE_CD":"Not_used_in_UM","POT_CD":"1","UM_RQRD_IND":"Y","HCFA_PT_CD":"21","MDFR_1_CD":"*","MDFR_2_CD":"Not_used_in_UM","MDFR_3_CD":"Not_used_in_UM","TRTMNT_TYPE_CD":"Not_used_in_UM","UM_PROV_SQNC_NBR":"*","FNL_PROC_SRVC_CLS_1_CD":"Not_used_in_UM","MBR_CNTRCT_CVRG_CD":"Not_used_in_UM","PROC_SRVC_CLS_1_CD":"Not_used_in_UM","PROC_SRVC_CLS_2_CD":"Not_used_in_UM","PROC_SRVC_CLS_3_CD":"Not_used_in_UM","BNFT_YEAR_CNTRCT_EFCTV_DT":"Not_used_in_UM","BNFT_YEAR_CNTRCT_REV_DT":"Not_used_in_UM","CLM_CMPLTN_DT":"12/31/9999","MBR_CNTRCT_EFCTV_DT":"Not_used_in_UM","MBR_CNTRCT_END_DT":"Not_used_in_UM","SRVC_TO_DT":"12/6/2018","BILLD_CHRGD_AMT":"Not_used_in_UM","MEDCRB_APRVD_AMT":"Not_used_in_UM","MEDCRB_COINSRN_AMT":"Not_used_in_UM","MEDCRB_DDCTBL_AMT":"Not_used_in_UM","MEDCRB_PAID_AMT":"Not_used_in_UM","UNITS_OCR_NBR":"Not_used_in_UM","BILLD_SRVC_UNIT_QTY":"1","MBR_CNTRCT_CD":"Not_used_in_UM","PN_CD":"IND1","HCPCS_CD":"*","PROC_CD":"01711","TOS_TYPE_CD":"RAB","UM_CASE_NBR":"*","nationalSegment1":"Not_used_in_UM","TAX_LIC_SPLTYCDE":"Not_used_in_UM","nationalSegment2":"Not_used_in_UM","TYPE_OF_BILL_CD":"Not_used_in_UM","BILLG_NPI":"1437103777","nationalSegment3":"Not_used_in_UM","CLM_PATH_IND":"Not_used_in_UM","CLM_FILE_COB_IND":"Not_used_in_UM","BILLG_TXNMY_CD":"Not_used_in_UM","RNDRG_TXNMY_CD":"Not_used_in_UM","RNDRG_TAX_ID":"Not_used_in_UM"}'
        validate(list,schema)

        #um_transform(payload)
        #resp = "WORKING FINE"
        #x = um_transform(payload)

        resp = "SCHEMA IS FINE : " #+ str(x)
    except jsonschema.exceptions.ValidationError as ve:
        print("IN EXCEPT")
        if debug:
            print("IN debug loop")
            sys.stderr.write("Record #{}: ERROR\n".format(ve))
            sys.stderr.write(str(ve) + "\n")
        resp['error'] = str(sys.exc_info()[1]).replace('\n', ' ')
            #resp['error'] = str("Record #{}: ERROR\n".format(ve))
    except jsonschema.SchemaError as se:
        print("IN EX")
        sys.stderr.write(str(se) + "\n")
        resp['error'] = str(se).replace('\n', ' ')
    except Exception as e:
        print("HERE")
        resp['error'] = e.__str__()
    finally:
        return make_response(str(resp), 200)


schema2 = {
  "definitions": {},
  "$schema": "http://json-schema.org/draft-07/schema#",
  "$id": "http://example.com/root.json",
  "type": "array",
  "title": "The Root Schema",
  "items": {
    "$id": "#/items",
    "type": "object",
    "title": "The Items Schema",
    "required": [
      "KEY_CHK_DCN_NBR",
      "PROV_NM"
    ],
    "properties": {
      "KEY_CHK_DCN_NBR": {
        "$id": "#/items/properties/KEY_CHK_DCN_NBR",
        "type": "string",
        "title": "The Key_chk_dcn_nbr Schema",
        "default": "",
        "examples": [
          "1234C03894"
        ],
        "pattern": "^(.*)$"
      },
      "PROV_NM": {
        "$id": "#/items/properties/PROV_NM",
        "type": "integer",
        "title": "The PROV_NM Schema",
        "examples": [
          "39845738"
        ],
        "pattern": "^([0-9][0-9]*)$"
      },
      "DATE_VALUE": {
        "$id": "#/items/properties/DATE_VALUE",
        "type": "string",
        "title": "The DATE_VALUE Schema",
        "examples": [
          "12/6/2018"
        ],
        "pattern": "^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9]|[1-9])/[0-9]{4}$"
        #[\d]{1,2}/[\d]{1,2}/[\d]{4}"
      },
      "PROV_SCNDRY_NM": {
        "$id": "#/items/properties/PROV_SCNDRY_NM",
        "type": "string",
        "minLength": 1,
        "maxLength": 25,
        "title": "The PROV_SCNDRY_NM Schema",
        "default": " ",
        "examples": [
          "*","SAMARITAN HOSPITAL"
        ],
        "pattern": "^([A-Za-z0-9 ]*)$"
      },
    }
  }
}
#case sensitive for column name, through error only for required ones as the don't match, allow extra columns
#are we handling max_content_length via app.config
testdata = [{"userId":"Not_used_in_UM","KEY_CHK_DCN_NBR":"18345CO6693","KEY_CHK_DCN_CENTRY_CD":"Not_used_in_UM","KEY_CHK_DCN_ITEM_CD":"Not_used_in_UM","BHVRL_HLTH_ACS_PROV_IND":"Not_used_in_UM","ENCNTR_IND":"Not_used_in_UM","ITS_HOME_IND":"Y","ITS_PARG_PROV_IND":"Y","MBU_CD":"Not_used_in_UM","MK_FUND_TYPE_CD":"Not_used_in_UM","PRMRY_NTWK_CD":"Not_used_in_UM","PROV_IND":"Not_used_in_UM","ATCHMNT_IND":"Not_used_in_UM","BYPS_CDS":"SRUM* **","CLM_TYPE_CD":"INPT","DTL_LINE_NBR":"01","EDI_CLM_FLNG_IND":"Not_used_in_UM","PROV_ST_CD":"Not_used_in_UM","SBSCRBR_ST_CD":"Not_used_in_UM","SRC_CD":"Not_used_in_UM","SRVC_TYPE_CURNT_CD":"Not_used_in_UM","SRVC_TYPE_ORGNL_CD":"Not_used_in_UM","PROV_LCNS_ALPH_CD":"Not_used_in_UM","PROV_SPCLTY_CD":"Not_used_in_UM","ORGNL_ENTRY_DT":"Not_used_in_UM","SRVC_FROM_DT":"10/19/2018","SRVC_THRU_DT":"12/6/2018","BSIC_PAYMNT_AMT":"Not_used_in_UM","COB_BSIC_PAYMNT_AMT":"Not_used_in_UM","COB_MM_PAYMNT_AMT":"Not_used_in_UM","DDCTBL_AMT":"Not_used_in_UM","MM_PAY_AMT":"Not_used_in_UM","RJCT_AMT":"Not_used_in_UM","TOTL_CHRG_AMT":"Not_used_in_UM","TOTL_NON_ELGBL_AMT":"Not_used_in_UM","WRTOF_AMT":"Not_used_in_UM","PAT_MBR_CD":"50","PAT_AGE_NBR":"Not_used_in_UM","COB_SGMNT_CNT":"Not_used_in_UM","MEDCR_CNT":"Not_used_in_UM","LPP_INT_AMT":"Not_used_in_UM","PROV_CITY_NM":"Not_used_in_UM","SBSCRBR_CITY_NM":"Not_used_in_UM","ITS_ORGNL_SCCF_NEW_NBR":"*","PROV_NM":"GOODSAMARITANHOSPITAL","PROV_SCNDRY_NM":"SAMARITAN HOSPITAL","ERR_CDS":"Not_used_in_UM","PROV_ZIP_4_CD":"Not_used_in_UM","SBSCRBR_ZIP_4_CD":"Not_used_in_UM","PROV_ZIP_5_CD":"Not_used_in_UM","SBSCRBR_ZIP_5_CD":"Not_used_in_UM","icdPrimaryCds":"Not_used_in_UM","ICD_CD":["Z3801","P220","P612","P711","P284"],"limitClass":"Not_used_in_UM","FULL_MNGMNT_BU_CD":"Not_used_in_UM","PROV_LCNS_NMRC_CD":"Not_used_in_UM","PROV_TAX_ID":"621763090","ICD_CDS":"Not_used_in_UM","SBSCRBR_CERTFN_1_NBR":"619","SBSCRBR_CERTFN_2_NBR":"543","SBSCRBR_CERTFN_3_NBR":"610","CURNT_QUE_UNIT_NBR":"F6","claimDetails":"Not_used_in_UM","MBR_CNTRCT_TYPE_CD":"Not_used_in_UM","POT_CD":"1","UM_RQRD_IND":"Y","HCFA_PT_CD":"21","MDFR_1_CD":"*","MDFR_2_CD":"Not_used_in_UM","MDFR_3_CD":"Not_used_in_UM","TRTMNT_TYPE_CD":"Not_used_in_UM","UM_PROV_SQNC_NBR":"*","FNL_PROC_SRVC_CLS_1_CD":"Not_used_in_UM","MBR_CNTRCT_CVRG_CD":"Not_used_in_UM","PROC_SRVC_CLS_1_CD":"Not_used_in_UM","PROC_SRVC_CLS_2_CD":"Not_used_in_UM","PROC_SRVC_CLS_3_CD":"Not_used_in_UM","BNFT_YEAR_CNTRCT_EFCTV_DT":"Not_used_in_UM","BNFT_YEAR_CNTRCT_REV_DT":"Not_used_in_UM","CLM_CMPLTN_DT":"12/31/9999","MBR_CNTRCT_EFCTV_DT":"Not_used_in_UM","MBR_CNTRCT_END_DT":"Not_used_in_UM","SRVC_TO_DT":"12/6/2018","BILLD_CHRGD_AMT":"Not_used_in_UM","MEDCRB_APRVD_AMT":"Not_used_in_UM","MEDCRB_COINSRN_AMT":"Not_used_in_UM","MEDCRB_DDCTBL_AMT":"Not_used_in_UM","MEDCRB_PAID_AMT":"Not_used_in_UM","UNITS_OCR_NBR":"Not_used_in_UM","BILLD_SRVC_UNIT_QTY":"1","MBR_CNTRCT_CD":"Not_used_in_UM","PN_CD":"IND1","HCPCS_CD":"*","PROC_CD":"01711","TOS_TYPE_CD":"RAB","UM_CASE_NBR":"*","nationalSegment1":"Not_used_in_UM","TAX_LIC_SPLTYCDE":"Not_used_in_UM","nationalSegment2":"Not_used_in_UM","TYPE_OF_BILL_CD":"Not_used_in_UM","BILLG_NPI":"1437103777","nationalSegment3":"Not_used_in_UM","CLM_PATH_IND":"Not_used_in_UM","CLM_FILE_COB_IND":"Not_used_in_UM","BILLG_TXNMY_CD":"Not_used_in_UM","RNDRG_TXNMY_CD":"Not_used_in_UM","RNDRG_TAX_ID":"Not_used_in_UM"}]
testdata_trans = '{"userId":"Not_used_in_UM","KEY_CHK_DCN_NBR":"18345CO6693","KEY_CHK_DCN_CENTRY_CD":"Not_used_in_UM","KEY_CHK_DCN_ITEM_CD":"Not_used_in_UM","BHVRL_HLTH_ACS_PROV_IND":"Not_used_in_UM","ENCNTR_IND":"Not_used_in_UM","ITS_HOME_IND":"Y","ITS_PARG_PROV_IND":"Y","MBU_CD":"Not_used_in_UM","MK_FUND_TYPE_CD":"Not_used_in_UM","PRMRY_NTWK_CD":"Not_used_in_UM","PROV_IND":"Not_used_in_UM","ATCHMNT_IND":"Not_used_in_UM","BYPS_CDS":"SRUM* **","CLM_TYPE_CD":"INPT","DTL_LINE_NBR":"01","EDI_CLM_FLNG_IND":"Not_used_in_UM","PROV_ST_CD":"Not_used_in_UM","SBSCRBR_ST_CD":"Not_used_in_UM","SRC_CD":"Not_used_in_UM","SRVC_TYPE_CURNT_CD":"Not_used_in_UM","SRVC_TYPE_ORGNL_CD":"Not_used_in_UM","PROV_LCNS_ALPH_CD":"Not_used_in_UM","PROV_SPCLTY_CD":"Not_used_in_UM","ORGNL_ENTRY_DT":"Not_used_in_UM","SRVC_FROM_DT":"10/19/2018","SRVC_THRU_DT":"12/6/2018","BSIC_PAYMNT_AMT":"Not_used_in_UM","COB_BSIC_PAYMNT_AMT":"Not_used_in_UM","COB_MM_PAYMNT_AMT":"Not_used_in_UM","DDCTBL_AMT":"Not_used_in_UM","MM_PAY_AMT":"Not_used_in_UM","RJCT_AMT":"Not_used_in_UM","TOTL_CHRG_AMT":"Not_used_in_UM","TOTL_NON_ELGBL_AMT":"Not_used_in_UM","WRTOF_AMT":"Not_used_in_UM","PAT_MBR_CD":"50","PAT_AGE_NBR":"Not_used_in_UM","COB_SGMNT_CNT":"Not_used_in_UM","MEDCR_CNT":"Not_used_in_UM","LPP_INT_AMT":"Not_used_in_UM","PROV_CITY_NM":"Not_used_in_UM","SBSCRBR_CITY_NM":"Not_used_in_UM","ITS_ORGNL_SCCF_NEW_NBR":"*","PROV_NM":"GOODSAMARITANHOSPITAL","PROV_SCNDRY_NM":"SAMARITAN HOSPITAL","ERR_CDS":"Not_used_in_UM","PROV_ZIP_4_CD":"Not_used_in_UM","SBSCRBR_ZIP_4_CD":"Not_used_in_UM","PROV_ZIP_5_CD":"Not_used_in_UM","SBSCRBR_ZIP_5_CD":"Not_used_in_UM","icdPrimaryCds":"Not_used_in_UM","ICD_CD":["Z3801","P220","P612","P711","P284"],"limitClass":"Not_used_in_UM","FULL_MNGMNT_BU_CD":"Not_used_in_UM","PROV_LCNS_NMRC_CD":"Not_used_in_UM","PROV_TAX_ID":"621763090","ICD_CDS":"Not_used_in_UM","SBSCRBR_CERTFN_1_NBR":"619","SBSCRBR_CERTFN_2_NBR":"543","SBSCRBR_CERTFN_3_NBR":"610","CURNT_QUE_UNIT_NBR":"F6","claimDetails":"Not_used_in_UM","MBR_CNTRCT_TYPE_CD":"Not_used_in_UM","POT_CD":"1","UM_RQRD_IND":"Y","HCFA_PT_CD":"21","MDFR_1_CD":"*","MDFR_2_CD":"Not_used_in_UM","MDFR_3_CD":"Not_used_in_UM","TRTMNT_TYPE_CD":"Not_used_in_UM","UM_PROV_SQNC_NBR":"*","FNL_PROC_SRVC_CLS_1_CD":"Not_used_in_UM","MBR_CNTRCT_CVRG_CD":"Not_used_in_UM","PROC_SRVC_CLS_1_CD":"Not_used_in_UM","PROC_SRVC_CLS_2_CD":"Not_used_in_UM","PROC_SRVC_CLS_3_CD":"Not_used_in_UM","BNFT_YEAR_CNTRCT_EFCTV_DT":"Not_used_in_UM","BNFT_YEAR_CNTRCT_REV_DT":"Not_used_in_UM","CLM_CMPLTN_DT":"12/31/9999","MBR_CNTRCT_EFCTV_DT":"Not_used_in_UM","MBR_CNTRCT_END_DT":"Not_used_in_UM","SRVC_TO_DT":"12/6/2018","BILLD_CHRGD_AMT":"Not_used_in_UM","MEDCRB_APRVD_AMT":"Not_used_in_UM","MEDCRB_COINSRN_AMT":"Not_used_in_UM","MEDCRB_DDCTBL_AMT":"Not_used_in_UM","MEDCRB_PAID_AMT":"Not_used_in_UM","UNITS_OCR_NBR":"Not_used_in_UM","BILLD_SRVC_UNIT_QTY":"1","MBR_CNTRCT_CD":"Not_used_in_UM","PN_CD":"IND1","HCPCS_CD":"*","PROC_CD":"01711","TOS_TYPE_CD":"RAB","UM_CASE_NBR":"*","nationalSegment1":"Not_used_in_UM","TAX_LIC_SPLTYCDE":"Not_used_in_UM","nationalSegment2":"Not_used_in_UM","TYPE_OF_BILL_CD":"Not_used_in_UM","nationalSegment3":"Not_used_in_UM","CLM_PATH_IND":"Not_used_in_UM","CLM_FILE_COB_IND":"Not_used_in_UM","BILLG_TXNMY_CD":"Not_used_in_UM","RNDRG_TXNMY_CD":"Not_used_in_UM","RNDRG_TAX_ID":"Not_used_in_UM"}'

data =[{"KEY_CHK_DCN_NBR": "1234C03894", "PROV_NM": "09","PROV_SCNDRY_NM": "SPACE VALUE"}]
data1 = \
[
    {"KEY_CHK_DCN_NBR": "1234C03894", "PROV_NM": 12343},
    {"KEY_CHK_DCN_NBR": "1234C03894", "PROV_NM": 34857,"DATE_VALUE":"12/6/2018"},
    {"KEY_CHK_DCN_NBR": "1234C03894", "PROV_NM": 34857,"future_column":"value"},
    {"KEY_CHK_DCN_NBR": "1234C03894", "PROV_NM": 34857},
    {"KEY_CHK_DCN_NBR": "1234C03894", "PROV_NM": 34857}
]


def z(payload):
    try:
        print("ENTER TRY")
        schema = _load_json_schema("UM_Schema.json")
        x = validate(payload, schema, format_checker=jsonschema.FormatChecker())
        print(x)
        print("EXIT TRY")
    except jsonschema.exceptions.ValidationError as ve:
        if debug:
            sys.stderr.write("Record #{}: ERROR\n".format(ve))
            sys.stderr.write(str(ve) + "\n")
        return False
    return True


def assert_valid_schema(data, schema_file):
    """ Checks whether the given data matches the schema """

    schema = _load_json_schema(schema_file)
    try:
        if debug :print("ENTER TRY")
        x = validate(data, schema, format_checker=jsonschema.FormatChecker())
        if debug: print("EXIT TRY")

    except jsonschema.exceptions.ValidationError as ve:
        if debug:
            sys.stderr.write("Record #{}: ERROR\n".format(ve))
            sys.stderr.write(str(ve) + "\n")
        return False
    return True

def _load_json_schema(filename):
    """ Loads the given schema file """

    #relative_path = join('schemas', filename)
    absolute_path = join(dirname(__file__), filename)
    print(absolute_path)
    with open(absolute_path) as schema_file:
        return json.loads(schema_file.read())

def um_transform(data):
    part_dict = {'ICD_A_CD' : '*','ICD_B_CD' : '*','ICD_C_CD' : '*','ICD_D_CD' : '*','ICD_E_CD' : '*',
                'ICD_OTHR_6_CD':'*','ICD_OTHR_7_CD':'*','ICD_OTHR_8_CD':'*','ICD_OTHR_9_CD':'*','ICD_OTHR_10_CD':'*',
                 'ICD_OTHR_11_CD':'*','ICD_OTHR_12_CD': '*','ICD_OTHR_13_CD':'*','ICD_OTHR_14_CD':'*','ICD_OTHR_15_CD':'*',
                 'ICD_OTHR_16_CD':'*','ICD_OTHR_17_CD':'*','ICD_OTHR_18_CD': '*','ICD_OTHR_19_CD':'*','ICD_OTHR_20_CD':'*'}
    #TODO: Need to add all optional columns
    optional_col_dict = {'BILLG_NPI':'*','UM_PROV_SQNC_NBR':'*'}

    data_dict = json.loads(data)


    counter = 0
    icd_keys = list(part_dict.keys())
    for item in data_dict['ICD_CD']:
        part_dict[icd_keys[counter]] = item
        counter = counter +1
    #print(part_dict)


    for key,value in optional_col_dict.items():
        if key not in data_dict:
            part_dict[key] = value
            print("Adding key ;"+str(key))


    part_dict['memID'] = str(data_dict['SBSCRBR_CERTFN_1_NBR'])+str(data_dict['SBSCRBR_CERTFN_2_NBR'])+str(data_dict['SBSCRBR_CERTFN_3_NBR'])

    #part_dict['PRVDR_STATUS'] = 'PAR' if(data_dict['ITS_HOME_IND'] == 'Y' and
    #join_dict = merge_two_dicts(data_dict,part_dict)


def merge_two_dicts(x, y):
    #z = {**x, **y} # for 3.5 or greater
    z = x.copy()   # start with x's keys and values
    z.update(y)    # modifies z with y's keys and values & returns None
    return z

if __name__ == '__main__':
    PORT = 50010
    app.run( port=PORT, debug=True)
    #print("Calling with correct data")
    #Pass data should be type of object as per schema ...we can parameterize that
    #bool1 = UM_validate(data1)
    #print(bool1)
    #print("Calling with wrong data")
    #bool2 = UM_validate(data2)
    #print(bool2)
    #print(UM_validate(testdata))
    #print(assert_valid_schema(testdata,'UM_Schema.json'))
    #validate(data1, schema2, format_checker=jsonschema.FormatChecker())
    #um_data = um_transform(testdata_trans)

