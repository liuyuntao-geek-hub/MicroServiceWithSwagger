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
import csv
import happybase
import time

batch_size = 1000
host = "dwbddisc1r3e2.wellpoint.com"
port = 9090
file_path = "/home/af08853/geo_2.csv"
namespace = "pinnga1"
row_count = 0
start_time = time.time()
table_name = "dv_pdppndo:NDO_GEO_ACCESS_PROV_TC4"

krb_service = "hbase"
krb_host = host

socket = TSocket.TSocket(host, port)
transport = TTransport.TSaslClientTransport(socket, krb_host, krb_service)

protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = Client(protocol)

transport.open()
tables = client.getTableNames()
scan = Hbase.TScan(startRow="GACPPO000", stopRow="GACPPO002")
scannerId = client.scannerOpenWithScan(table_name, scan,{})
row = client.scannerGet(scannerId)
rowList = client.scannerGetList(scannerId,10000)
final_data = []
while len(rowList):
     for row in rowList:
             data = row.columns.get("f1:data").value
             final_data.append(data)
             rowKey = row.row
             
     rowList = client.scannerGetList(scannerId,10000)

print len(final_data)
client.scannerClose(scannerId)

