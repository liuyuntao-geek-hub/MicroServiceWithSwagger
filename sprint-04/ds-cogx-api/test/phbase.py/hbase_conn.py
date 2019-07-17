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
import time

batch_size = 1000
host = "dwbddisc1r3e2.wellpoint.com"
port = 9090
file_path = "/home/af08853/geo_2.csv"
namespace = "pinnga1"
row_count = 0
start_time = time.time()
table_name = "pinnga1:GEO_ACCESS"

krb_service = "hbase"
krb_host = host

socket = TSocket.TSocket(host, port)
transport = TTransport.TSaslClientTransport(socket, krb_host, krb_service)

protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = Client(protocol)

transport.open()
tables = client.getTableNames()
scan = Hbase.TScan(startRow="VACOMMERCIALPPO000", stopRow="VACOMMERCIALPPO002")
scannerId = client.scannerOpenWithScan(table_name, scan,{})
row = client.scannerGet(scannerId)
rowList = client.scannerGetList(scannerId,1)
final_data = []
data1 = []
while len(rowList):
     #print len(rowList)
     for row in rowList:
             data = row.columns.get("p:data").value
             final_data.append(data)
             #print data
             rowKey = row.row
             #print rowKey
             #print len(data)
     rowList = client.scannerGetList(scannerId,1)

print "----------------------------------------------------------------------------------------------------------"
#print final_data
print data1

client.scannerClose(scannerId)
print final_data
print type(final_data)
print "---------------------------------------------------------------------------------------"
print final_data[2000]
print final_data[20000]
transport.close()
