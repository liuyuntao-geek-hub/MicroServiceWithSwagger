import requests
from flask import jsonify, request, make_response, Flask
import socket
from requests_kerberos import HTTPKerberosAuth
import datetime
import json
import base64
import os 
import hashlib
import urllib.parse
import constants
os.environ['KRB5_CLIENT_KTNAME'] = constants.HBASE_KEYTAB
os.environ['KRB5CCNAME'] = constants.HBASE_KFILE


def hbaseLookup(key, table_column, logger, extra=None):  
  if os.environ.get('COGX_ENV', None) == None:
    rest_url = constants.HBASE_ENDPOINT + '?key=%s&table=%s' % (urllib.parse.quote(key), urllib.parse.quote(table_column))
    r = requests.get(rest_url, verify=False, timeout=(constants.CONNECTION_TIMEOUT, constants.READ_TIMEOUT))
    logger.debug(r.text, extra=extra)
    return r.text
  else:
    logger.debug(os.environ.get('KRB5_CLIENT_KTNAME', None))
    logger.debug(os.environ.get('KRB5CCNAME', None))

  hash_key = hashlib.md5(key.encode('utf-8')).hexdigest()[0:8]
  key_1 = hash_key + key
  logger.debug(key_1, extra=extra)
  splits = table_column.split('~', 2)
  rest_url = constants.HBASE_ENDPOINT + splits[0] + urllib.parse.quote(key_1) + splits[1]
  # rest_url = "https://dwbdtest1r2e.wellpoint.com:20550/dv_hb_ccpcogx_nogbd_r000_in:um_auth/" + key + "/um:jsonData"
  logger.debug(rest_url, extra=extra)
  #rest_url = "https://dwbdtest1r2e.wellpoint.com:20550/dv_hb_ccpcogx_nogbd_r000_in:um_auth/00005b3a571656021/um:jsonData"
  headers = {'Accept': 'application/json'}
  start_time = datetime.datetime.now()
  auth=HTTPKerberosAuth(force_preemptive=True)
  r = requests.get(rest_url, verify=constants.HBASE_PEM, headers=headers, auth=HTTPKerberosAuth(principal=constants.HBASE_PRINCIPAL), timeout=(constants.CONNECTION_TIMEOUT, constants.READ_TIMEOUT))
  # r = requests.get(rest_url, verify='/home/srcccpcogxapidv/ds_keychain/root.pem', headers=headers, auth=HTTPKerberosAuth())
  end_time = datetime.datetime.now()
  pTime = (end_time - start_time)
  logger.debug("Time taken to retrieve data from HBase - " + str(pTime), extra=extra)
  if r.text.strip() != 'Not found':
    j = json.loads(r.text)
    res = base64.b64decode(j['Row'][0]['Cell'][0]['$']).decode('UTF-8')
  else:
    res = r.text
  
  return res