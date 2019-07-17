import traceback
import sys
import pandas as pd
from collections import OrderedDict
sys.path.append("/home/af08853/phbase.py")
from thrift import Thrift
from thrift.transport import TSocket, TTransport
from thrift.protocol import TBinaryProtocol
from hbased import ttypes,Hbase
from hbased.Hbase import Client
from pandas import Series
import csv
import time
import subprocess
import re
import json
import hashlib

host = "dwbdtest1r1e.wellpoint.com"
port = 9090
start_time = time.time()
table_name = "dv_hb_ccpcogx_nogbd_r000_in:cogx_benefits_v"
spec_file = "/home/af49123/ndo-api-disc/specality_code.csv"
krb_service = "hbase"
krb_host = host

socket = TSocket.TSocket(host, port)
transport = TTransport.TSaslClientTransport(socket, krb_host, krb_service)

protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = Client(protocol)
transport.open()

'''df = pd.read_csv("/home/af08853/UM_auth.txt")
df1 = df.to_dict(orient='records')

a = []
lst = []
dict = {}
for x in df1:
    x1 = x['SRC_SBSCRBR_ID']
    if x1 in dict.keys():
        a = dict[x1]
        a.append(x)
        dict[x1] = a
    else:
        lst.append(x)
        dict[x1] = lst
    a = []
    lst = []

transport.open()
tables = client.getTableNames()
#scan = Hbase.TScan(startRow= start_key, stopRow= end_key)
#scannerId = client.scannerOpenWithScan(table_name, scan,{})
#row = client.scannerGet(scannerId)
#rowList = client.scannerGetList(scannerId,1)

keys = dict.keys()
for key in keys:
    hash_key = hashlib.md5(key.encode('utf-8')).hexdigest()[0:8]
    key_1 = hash_key + key
    #print a, b
    val = {}
    #print dict[key]
    val['cogxUM'] = dict[key] 
    val = str(json.dumps(val))
    print val
    mutations = [Hbase.Mutation(column='um:jsonData', value= val)]
    client.mutateRow('dv_hb_ccpcogx_gbd_r000_in:um_auth_v', key_1, mutations,None)
'''


import json
import pandas as pd
import thrift
import base64
import hashlib
to_datetime = lambda d: pd.datetime.strptime(d, '%m/%d/%Y')
#with open('../sample/dup/history_detail.txt', 'r') as inputFP:
#    his_dtl = inputFP.read()
#with open('../sample/dup/history_header.txt', 'r') as inputFP:
#    his_hdr= inputFP.read()

#his_dtl = his_dtl.split("\n")
#his_hdr = his_hdr.split("\n")

#df_his_dtl = pd.DataFrame.from_dict([sub.split(",") for sub in his_dtl])
#df_his_hdr = pd.DataFrame.from_dict([sub.split(",") for sub in his_hdr])
#print(df_his_dtl.head())
#print(df_his_hdr.head())
#print(df_his_dtl.count())
#print(df_his_hdr.count())

df_his_dtl = pd.read_csv("/home/af08853/history_detail.txt",dtype=str,converters={'CLM_CMPLTN_DT':to_datetime,'SRVC_FROM_DT_DTL':to_datetime,'SRVC_TO_DT_DTL':to_datetime},index_col=None)
df_his_hdr = pd.read_csv("/home/af08853/history_header.txt",dtype=str,converters={'CLM_CMPLTN_DT':to_datetime,'SRVC_FROM_DT':to_datetime,'SRVC_THRU_DT':to_datetime},index_col=None)
#df_his_dtl = pd.read_csv("../sample/dup/history_detail.txt",dtype=str,index_col=None,infer_datetime_format =True)
#df_his_hdr = pd.read_csv("../sample/dup/history_header.txt",dtype=str,index_col=None,infer_datetime_format =True)
#df_his_dtl['SRVC_FROM_DT_DTL'] =df_his_dtl['SRVC_FROM_DT_DTL'].apply(lambda x : x.split('/')[2]+''+x.split('/')[0]+''+x.split('/')[1])
#df_his_dtl['SRVC_TO_DT_DTL'] =df_his_dtl['SRVC_TO_DT_DTL'].apply(lambda x : x.split('/')[2]+''+x.split('/')[0]+''+x.split('/')[1])
#df_his_hdr['SRVC_FROM_DT'] = df_his_hdr['SRVC_FROM_DT'].apply(lambda x : x.split('/')[2]+''+x.split('/')[0]+''+x.split('/')[1])
#df_his_hdr['SRVC_THRU_DT'] = df_his_hdr['SRVC_THRU_DT'].apply(lambda x : x.split('/')[2]+''+x.split('/')[0]+''+x.split('/')[1])

df_his_dtl['SRVC_FROM_DT_DTL']=df_his_dtl['SRVC_FROM_DT_DTL'].dt.strftime('%Y%m%d')
df_his_dtl['SRVC_TO_DT_DTL'] =df_his_dtl['SRVC_TO_DT_DTL'].dt.strftime('%Y%m%d')
df_his_dtl['CLM_CMPLTN_DT']=df_his_dtl['CLM_CMPLTN_DT'].dt.strftime('%Y%m%d')
df_his_hdr['SRVC_FROM_DT'] =df_his_hdr['SRVC_FROM_DT'].dt.strftime('%Y%m%d')
df_his_hdr['SRVC_THRU_DT'] =df_his_hdr['SRVC_THRU_DT'].dt.strftime('%Y%m%d')
df_his_hdr['CLM_CMPLTN_DT']=df_his_hdr['CLM_CMPLTN_DT'].dt.strftime('%Y%m%d')

#print(df_his_dtl.head())
#print(df_his_hdr.head())
#print(df_his_dtl.count())
#print(df_his_hdr.count())

#print(df_his_dtl.columns)
#print(df_his_dtl.dtypes)

#print(df_his_hdr.columns)
#print(df_his_hdr.dtypes)

test_hdr_df = df_his_hdr#[df_his_hdr['KEY_CHK_DCN_NBR'] =='14157CN2711']
test_dtl_df = df_his_dtl#[df_his_dtl['KEY_CHK_DCN_NBR'] =='14157CN2711']

print(test_hdr_df.shape)
print(test_dtl_df.shape)

result = test_hdr_df.join(test_dtl_df.set_index('KEY_CHK_DCN_NBR'), on='KEY_CHK_DCN_NBR',rsuffix='_drop')
#cols = [c for c in result.columns if '_drop' not in str(c).lower()]
result['LOAD_INGSTN_ID'] = '88880808'
result['ICD_9_1_CD'] = '1'
cols = ['KEY_CHK_DCN_NBR','KEY_CHK_DCN_ITEM_CD','CLM_CMPLTN_DT','CLM_PAYMNT_ACTN_1_CD','CLM_PAYMNT_ACTN_2_6_CD','MEMBER_SSN','PAT_MBR_CD','GRP_NBR','SRVC_FROM_DT','SRVC_THRU_DT','PROV_TAX_ID','PROV_NM','PROV_SCNDRY_NM','PROV_SPCLTY_CD','PROV_LCNS_CD','TOTL_CHRG_AMT','TYPE_OF_BILL_CD','MDCL_RCRD_2_NBR','MRN_NBR','ICD_A_CD','ICD_B_CD','ICD_C_CD','ICD_D_CD','ICD_E_CD','PRVDR_STATUS','CLAIM_TYPE','DTL_LINE_NBR','ICD_9_1_CD','PROC_CD','TOS_TYPE_CD','PROC_SRVC_CLS_1_CD','PROC_SRVC_CLS_2_CD','PROC_SRVC_CLS_3_CD','HCPCS_CD','BILLD_CHRGD_AMT','BILLD_SRVC_UNIT_QTY','UNITS_OCR_NBR','PROC_MDFR_CD','HCPCS_MDFR_CD','MDFR_1_CD','MDFR_2_CD','MDFR_3_CD','HCFA_PT_CD','POT_CD','ELGBL_EXPNS_AMT','SRVC_FROM_DT_DTL','SRVC_TO_DT_DTL','LOAD_INGSTN_ID']
result = result[cols]
print(result.shape)
print(result.columns)
print(result.dtypes)


def key_generate(row):
    MEMBER_SSN = str(row['MEMBER_SSN'])
    PAT_MBR_CD = str(row['PAT_MBR_CD'])
    SRVC_FROM_DT = str(row['SRVC_FROM_DT'])
    SRVC_THRU_DT = str(row['SRVC_THRU_DT'])
    return MEMBER_SSN + PAT_MBR_CD + SRVC_FROM_DT + SRVC_THRU_DT


result['KEY'] = result.apply(lambda row: key_generate(row), axis=1)

#with open('../../dup_record.generated','+a') as f:

for key in result.KEY.unique():
    data = {}
    value = []
    row_key = hash_key = hashlib.md5(key.encode('utf-8')).hexdigest()[0:8] + key
    record = result[result['KEY'] == key].drop(['KEY'], axis=1).to_dict(orient='records')
    value.append(record)
    data['cogxClaim'] = record

    data = json.dumps(data)
    print(row_key)
    mutations = [Hbase.Mutation(column='c1:jsonData', value= data)]
    client.mutateRow('dv_hb_ccpcogx_nogbd_r000_in:cogx_claims_v', row_key, mutations,None)
