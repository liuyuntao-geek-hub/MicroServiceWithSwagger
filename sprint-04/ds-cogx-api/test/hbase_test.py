import traceback
import sys
import pandas as pd
from collections import OrderedDict
sys.path.append("/home/af81392/ds-cogx-api/test/phbase.py")
sys.path.append("/home/af81392/ds-cogx-api/test/phbase.py/hbased")
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

krb_service = "hbase"
krb_host = host

socket = TSocket.TSocket(host, port)
transport = TTransport.TSaslClientTransport(socket, krb_host, krb_service)

protocol = TBinaryProtocol.TBinaryProtocol(transport)
client = Client(protocol)

transport.open()
tables = client.getTableNames()

for table in tables:
    print(table)

transport.close()
