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

df = pd.read_csv("/home/af08853/UM_auth.txt")
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



"""print type(final_data)
print len(final_data)
hbase_raw = pd.DataFrame(final_data,columns=["DATA"])
no_delim = re.split(' |\*|@|~|\^|', )
last_2 = no_delim[2:]
tax_id = last_2[::2]
specs = last_2[1::2]

new_DATA = [('zip', [no_delim[1]]*(len(last_2)/2)),
('tax', tax_id),
('type', specs),
('dist', [no_delim[0]] * (len(last_2)/2))
]

unparsed = pd.DataFrame.from_items(new_DATA)
print unparsed"""
