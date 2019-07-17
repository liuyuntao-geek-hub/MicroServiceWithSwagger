import traceback
import sys
import pandas as pd
import happybase
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
file_path = "/home/af08853/prov_60.csv"
namespace = "pinnga1"
row_count = 0
start_time = time.time()
table_name = "dv_pdppndo:NDO_GEO_ACCESS_PROV_TEST"

krb_service = "hbase"
krb_host = host

socket = TSocket.TSocket(host, port)
transport = TTransport.TSaslClientTransport(socket, krb_host, krb_service)

protocol = TBinaryProtocol.TBinaryProtocol(transport)
conn = Client(protocol)

transport.open()
tables = conn.getTableNames()

s = open("/home/af08853/prov_60.csv", "rb")
	
linenumber = 0;
	
	
mutationsbatch = []
	
for line in s:
        line = line.strip().split(",",1)
        #print line
	rowkey = line[0]
	mutations = [Hbase.Mutation(column="p:data", value=line[1])]
	#print tables		
	mutationsbatch.append(Hbase.BatchMutation(row=rowkey,mutations=mutations))
	linenumber = linenumber + 1

# Run the mutations for the work of Shakespeare
conn.mutateRows(table_name, mutationsbatch,{})
transport.close()


