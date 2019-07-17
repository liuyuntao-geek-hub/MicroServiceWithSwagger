import os


def flat_dict_values(dict):
    dict_keys = [*dict.values()]
    dict_keys = [item for sublist in dict_keys for item in sublist]
    return dict_keys


sample = False

PATH = os.path.dirname(os.path.realpath(__file__))
PATH = os.path.split(PATH)[0]
LOW_FREQ_THRESH = 0
MODEL_REJECT_CODES = PATH + '/models/ltr/col_dict.csv'
MODEL_OTHER_REASONS = PATH + '/models/ltr/LTR_Other_reasons_07_2018_12_2018_col_dict.csv'
DUP_COL_CODES = PATH + '/models/dup/col_dict_2018_06_2018_12.csv'
MBU_ROLLUP_FILE = PATH + '/models/ben/MBU_rollup_file.csv'
BEN_COL_CODES = PATH + '/models/ben/col_dict_v0529.csv'

# MODEL_NAME_BINARY = PATH + '/models/LTR_Other_reasons_xg_boost_depth_10_data_X_train.sav'

DB_PATH = PATH + '/data/cogx.db'
DB_PATH_DUP = PATH + '/data/cogx_dup.db'
DB_PATH_BEN = PATH + '/data/cogx_ben.db'

HEADER_DICT = {'ICD': ['ICD_A_CD', 'ICD_B_CD', 'ICD_C_CD', 'ICD_D_CD', 'ICD_E_CD','ICD_OTHR_1_CD','ICD_OTHR_2_CD','ICD_OTHR_3_CD','ICD_OTHR_4_CD','ICD_OTHR_5_CD','ICD_OTHR_6_CD','ICD_OTHR_7_CD','ICD_OTHR_8_CD','ICD_OTHR_9_CD','ICD_OTHR_10_CD','ICD_OTHR_11_CD','ICD_OTHR_12_CD','ICD_OTHR_13_CD','ICD_OTHR_14_CD','ICD_OTHR_15_CD','ICD_OTHR_16_CD','ICD_OTHR_17_CD','ICD_OTHR_18_CD','ICD_OTHR_19_CD','ICD_OTHR_20_CD'],
               'LMT_CLS_CD': ['LMT_CLS_A1_CD','LMT_CLS_A2_CD','LMT_CLS_A3_CD','LMT_CLS_B1_CD','LMT_CLS_B2_CD','LMT_CLS_B3_CD','LMT_CLS_C1_CD','LMT_CLS_C2_CD','LMT_CLS_C3_CD','LMT_CLS_D1_CD','LMT_CLS_D2_CD','LMT_CLS_D3_CD','LMT_CLS_E1_CD','LMT_CLS_E2_CD','LMT_CLS_E3_CD'],
               'JAA_claims': ['JAA_claims'],
               'ITS_HOST_claims': ['ITS_HOST'],
               'Medi_Cal_claims': ['Medi_Cal','SSB_West_Virginia','SSB_South_Carolina_Medicare'],
               'MBR_PROD_CLS_CD': ['MBR_PROD_CLS_CD'],
               'special_edit_claims' : ['special_edit_claims'],
               'ATCHMNT_IND': ['ATCHMNT_IND'],
               'GRP_NBR': ['GRP_NBR'],
               'PROV_IND': ['PROV_IND'],
               'PROV_ZIP_5_CD': ['PROV_ZIP_5_CD'],
               'PROV_ZIP_4_CD': ['PROV_ZIP_4_CD'],
               'PROD_NTWK' :['PROD_NTWK'],
               'SBSCRBR_ZIP_5_CD': ['SBSCRBR_ZIP_5_CD'],
               'SBSCRBR_ZIP_4_CD': ['SBSCRBR_ZIP_4_CD'],
               'CLM_TYPE_CD': ['CLM_TYPE_CD'],
               'CLAIM_TYPE':['CLAIM_TYPE'],
               'EDI_CLM_FLNG_IND': ['EDI_CLM_FLNG_IND'],  
               'MEMBER_SSN': ['MEMBER_SSN'],
               'PROV_TAX_ID': ['PROV_TAX_ID'],
               'PRVDR_STATUS':['PRVDR_STATUS'],
               'MK_FUND_TYPE_CD' : ['MK_FUND_TYPE_CD'],
               'ENCNTR_IND': ['ENCNTR_IND'],
               'ASO_FI': ['ASO_FI'],
               'MBU_CD': ['MBU_CD'],
               'FULL_MNGMNT_BU_CD' : ['FULL_MNGMNT_BU_CD'],
               'SRVC_TYPE_CURNT_CD': ['SRVC_TYPE_CURNT_CD'],
               'SRVC_TYPE_ORGNL_CD': ['SRVC_TYPE_ORGNL_CD'],
               'TAX_LIC_SPLTYCDE': ['TAX_LIC_SPLTYCDE'],
               'TYPE_OF_BILL_CD': ['TYPE_OF_BILL_CD'],
               'CLM_FILE_COB_IND': ['CLM_FILE_COB_IND'],
               'BILLG_TXNMY_CD': ['BILLG_TXNMY_CD'],
               'RNDRG_TXNMY_CD': ['RNDRG_TXNMY_CD'],
               'RNDRG_TAX_ID': ['RNDRG_TAX_ID'],
               'MEDCR_STTS_IND':['MEDCR_STTS_IND'],
               'CLM_PAYMNT_PRCS_CD':['CLM_PAYMNT_PRCS_CD']}

HEADER_NUM_COLS = ['TOTL_CHRG_AMT', 'COB_BSIC_PAYMNT_AMT', 'COB_MM_PAYMNT_AMT', 'COB_SGMNT_CNT', 'MEDCR_CNT',
                   'PAT_AGE_NBR']

DETAIL_DICT = {'MODIFIER_CD': ['HCPCS_MDFR_CD','PROC_MDFR_CD', 'MDFR_1_CD', 'MDFR_2_CD', 'MDFR_3_CD'],
               'PROC_CD': ['PROC_CD','HCPCS_CD'],
               'PROC_SRVC_CLS_CD':['PROC_SRVC_CLS_1_CD','PROC_SRVC_CLS_2_CD','PROC_SRVC_CLS_3_CD'],
               'MBR_CNTRCT_TYPE_CD': ['MBR_CNTRCT_TYPE_CD'],
               'POT_CD': ['POT_CD'],
               'TRTMNT_TYPE_CD': ['TRTMNT_TYPE_CD'],
               'MBR_CNTRCT_CVRG_CD': ['MBR_CNTRCT_CVRG_CD'],
               'MBR_CNTRCT_CD': ['MBR_CNTRCT_CD'],
               'PN_CD': ['PN_CD'],
               'TOS_TYPE_CD': ['TOS_TYPE_CD']}

DETAIL_NUM_COLS = ['DTL_LINE_NBR', 'BILLD_CHRGD_AMT', 'MEDCRB_APRVD_AMT', 'MEDCRB_PAID_AMT', 'MEDCRB_COINSRN_AMT',
                   'MEDCRB_DDCTBL_AMT']

DETAIL_DATE_COLS = ['BNFT_YEAR_CNTRCT_EFCTV_DT', 'BNFT_YEAR_CNTRCT_REV_DT', 'MBR_CNTRCT_EFCTV_DT', 'MBR_CNTRCT_END_DT',
                    'SRVC_FROM_DT', 'SRVC_TO_DT']

EDIT_DICT = {'ERR_CD': ['ERR_1_CD', 'ERR_2_CD', 'ERR_3_CD', 'ERR_4_CD', 'ERR_5_CD', 'ERR_6_CD', 'ERR_7_CD', 'ERR_8_CD',
                        'ERR_9_CD', 'ERR_10_CD', 'ERR_11_CD', 'ERR_12_CD', 'ERR_13_CD','ERR_14_CD', 'ERR_15_CD', 'ERR_16_CD', 'ERR_17_CD', 'ERR_18_CD', 'ERR_19_CD', 'ERR_20_CD', 'ERR_21_CD', 'ERR_22_CD', 'ERR_23_CD', 'ERR_24_CD', 'ERR_25_CD', 'ERR_26_CD', 'ERR_27_CD', 'ERR_28_CD', 'ERR_29_CD', 'ERR_30_CD', 'ERR_31_CD', 'ERR_32_CD']}


EDIT_KEEP_COLS = ['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR'] + flat_dict_values(EDIT_DICT)


UM_THRESHOLD = 0.0
LTR_THRESHOLD = 0.0
DUP_THRESHOLD = 0.0
BEN_THRESHOLD = 0.0

RESP_CD_MAP = {700:'Successful recommendation',710 :'Threshold not met ',711: 'Reference data not found', 709: 'Business exclusion',
               712: 'Model execution criteria not met',
               900:'Validation error',901:'Transformation error',902:'Deriving field error',903:'Model execution error', 904:'Connection Timed out' }


'''
MONGO_USER_NAME = urllib.parse.quote_plus(os.environ.get('MONGO_USER_NAME', 'srccogtrw'))
MONGO_PASSWORD = urllib.parse.quote_plus(os.environ.get('MONGO_PASSWORD', 'AhdS592JpQ'))
MONGO_URL = os.environ.get('MONGO_URL', 'mongodb://%s:%s@va33dlvmdb322.wellpoint.com:37043/cognativedb') % (MONGO_USER_NAME, MONGO_PASSWORD)
MONGO_PEM = os.environ.get('MONGO_PEM', PATH + '/env/root_chain.pem')
'''
HBASE_ENDPOINT = os.environ.get('HBASE_ENDPOINT', 'https://dwbdtest1r5e1.wellpoint.com:45380/search')
HBASE_UM_COLUMN = os.environ.get('HBASE_UM_COLUMN', '/dv_hb_ccpcogx_gbd_r000_in:um_auth_v/~/um:jsonData')
HBASE_DUP_COLUMN = os.environ.get('HBASE_DUP_COLUMN', '/dv_hb_ccpcogx_nogbd_r000_in:cogx_claims_v/~/c1:jsonData')
HBASE_BEN_COLUMN = os.environ.get('HBASE_BEN_COLUMN', '/dv_hb_ccpcogx_nogbd_r000_in:cogx_benefits_v/~/c1:jsonData')
HBASE_PEM = os.environ.get('HBASE_PEM', PATH + '/env/root.pem')
HBASE_PRINCIPAL = os.environ.get('HBASE_PRINCIPAL', 'srcccpcogxapidv@DEVAD.WELLPOINT.COM')
HBASE_KEYTAB = os.environ.get('KRB5_CLIENT_KTNAME', '/home/srcccpcogxapidv/ds_keychain/cogx-svc.keytab')
HBASE_KFILE = os.environ.get('KRB5CCNAME', '/tmp/afxxxxx_cogx')
NGINX_URL = os.environ.get('NGINX_URL', 'http://localhost:9080/processClaim')
CONNECTION_TIMEOUT = 1
READ_TIMEOUT = 180
ACTION_VALUE_LENGTH = 150


COGX_ENV = os.environ.get('COGX_ENV', None)

if COGX_ENV == 'dv':
    HBASE_ENDPOINT = 'https://dwbdtest1r2e.wellpoint.com:20550'
    HBASE_UM_COLUMN = '/dv_hb_ccpcogx_nogbd_r000_in:um_auth/~/um:jsonData'
    HBASE_DUP_COLUMN = '/dv_hb_ccpcogx_nogbd_r000_in:cogx_claims/~/c1:jsonData'
    HBASE_BEN_COLUMN = '/dv_hb_ccpcogx_nogbd_r000_in:cogx_benefits/~/c1:jsonData'
    HBASE_PEM = '/opt/cloudera/security/x509/cert.pem'
    HBASE_PRINCIPAL = 'srcccpcogxapidv@DEVAD.WELLPOINT.COM'
    HBASE_KEYTAB = '/etc/keytabs/srcccpcogxapidv.keytab'
    NGINX_URL = 'https://localhost:45380/processClaim'
    HBASE_KFILE = '/tmp/krb5cc_srcccpcogxapidv'
    READ_TIMEOUT = 30
elif COGX_ENV == 'ts':
    HBASE_ENDPOINT = 'https://dwbdtest1r2e.wellpoint.com:20550'
    HBASE_UM_COLUMN = '/ts_hb_ccpcogx_nogbd_r000_in:um_auth/~/um:jsonData'
    HBASE_DUP_COLUMN = '/ts_hb_ccpcogx_nogbd_r000_in:cogx_claims/~/c1:jsonData'
    HBASE_BEN_COLUMN = '/ts_hb_ccpcogx_nogbd_r000_in:cogx_benefits/~/c1:jsonData'
    HBASE_PEM = '/opt/cloudera/security/x509/cert.pem'
    HBASE_PRINCIPAL = 'srcccpcogxapits@DEVAD.WELLPOINT.COM'
    HBASE_KEYTAB = '/etc/keytabs/srcccpcogxapits.keytab'
    NGINX_URL = 'https://localhost:45381/processClaim'
    HBASE_KFILE = '/tmp/krb5cc_srcccpcogxapits'
    READ_TIMEOUT = 30
    LTR_THRESHOLD = 0.1
elif COGX_ENV == 'ua':
    HBASE_ENDPOINT = 'https://dwbdtest1r2e.wellpoint.com:20550'
    HBASE_UM_COLUMN = '/dv_hb_ccpcogx_nogbd_r000_in:um_auth/~/um:jsonData'
    HBASE_DUP_COLUMN = '/dv_hb_ccpcogx_nogbd_r000_in:cogx_claims/~/c1:jsonData'
    HBASE_BEN_COLUMN = '/dv_hb_ccpcogx_nogbd_r000_in:cogx_benefits/~/c1:jsonData'
    HBASE_PEM = '/opt/cloudera/security/x509/cert.pem'
    HBASE_PRINCIPAL = 'srcccpcogxapiua@DEVAD.WELLPOINT.COM'
    HBASE_KEYTAB = '/etc/keytabs/srcccpcogxapiua.keytab'
    NGINX_URL = 'https://localhost:45380/processClaim'
    HBASE_KFILE = '/tmp/krb5cc_srcccpcogxapiua'
    READ_TIMEOUT = 30
    LTR_THRESHOLD = 0.1
elif COGX_ENV == 'pr':
    HBASE_ENDPOINT = 'https://bdpr3r6e1pr.wellpoint.com:20550'
    HBASE_UM_COLUMN = '/pr_hb_ccpcogx_gbd_r000_wh:um_auth/~/um:jsonData'
    HBASE_DUP_COLUMN = '/pr_hb_ccpcogx_gbd_r000_wh:cogx_claims/~/c1:jsonData'
    HBASE_BEN_COLUMN = '/pr_hb_ccpcogx_gbd_r000_wh:cogx_benefits/~/c1:jsonData'
    HBASE_PEM = '/opt/cloudera/security/x509/cert.pem'
    HBASE_PRINCIPAL = 'srcccpcogxapipr@US.AD.WELLPOINT.COM'
    HBASE_KEYTAB = '/etc/keytabs/srcccpcogxapipr.keytab'
    NGINX_URL = 'https://localhost:45380/processClaim'
    HBASE_KFILE = '/tmp/krb5cc_srcccpcogxapipr'
    READ_TIMEOUT = 30
    UM_THRESHOLD = 0.4
    LTR_THRESHOLD = 0.95
    DUP_THRESHOLD = 0.76
    BEN_THRESHOLD = 0.99
