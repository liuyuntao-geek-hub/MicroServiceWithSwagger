import pickle
import csv
import yaml
import json
import logging 
import pandas as pd

import constants as const
from rotate_logs import get_logger
logger = get_logger()


with open(const.PATH + '/config/models.yml', 'r') as f:
    config = yaml.load(f,Loader=yaml.FullLoader)

usecases = config['usecases']
for usecase in usecases:
    logger.debug(usecase['name'])
    models = usecase['models']
    models[:] = filter(lambda x: x['active'], models)
    for model in models:
        logger.debug(model['model'])
        model['binary'] = pickle.load(open(const.PATH + '/models/' + usecase['name'] + '/' + model['path'], 'rb'))        


reject_list = []
with open(const.MODEL_REJECT_CODES, 'r') as csvfile:
    reader = csv.reader(csvfile, delimiter=',')
    for row in reader:
        reject_list.append(row[0])
    
'''
other_list = []
with open(const.MODEL_OTHER_REASONS, 'r') as csvfile:
    reader = csv.reader(csvfile, delimiter=',')
    for row in reader:
        other_list.append(row[0])
'''

dup_col_list = []
with open(const.DUP_COL_CODES, 'r') as csvfile:
    reader = csv.reader(csvfile, delimiter=',')
    for row in reader:
        dup_col_list.append(row[0])

MBU = pd.read_csv(const.MBU_ROLLUP_FILE, dtype={'FULL_MNGMNT_BU_CD':str})
MBU = MBU.set_index('FULL_MNGMNT_BU_CD')
ben_col_list = pd.read_csv(const.BEN_COL_CODES, dtype=object)

data_frames = {}
data_frames['MBU'] = MBU
data_frames['ben_col_list'] = ben_col_list

model_encodings = {}
model_encodings['reject_list'] = reject_list
model_encodings['dup_col_list'] = dup_col_list
model_encodings['ben_col_list'] = ben_col_list

mappings = {}
with open(const.PATH + '/config/mapping-um.yml', 'r') as f:
    mapping = yaml.load(f,Loader=yaml.FullLoader)
mappings['um_mapping'] = mapping

with open(const.PATH + '/config/mapping-ltr.yml', 'r') as f:
    mapping = yaml.load(f,Loader=yaml.FullLoader)
mappings['ltr_mapping'] = mapping

with open(const.PATH + '/config/mapping-dup.yml', 'r') as f:
    mapping = yaml.load(f,Loader=yaml.FullLoader)
mappings['dup_mapping'] = mapping

with open(const.PATH + '/config/mapping-ben.yml', 'r') as f:
    mapping = yaml.load(f,Loader=yaml.FullLoader)
mappings['ben_mapping'] = mapping

validations = {}
with open(const.PATH + '/config/uber_um_payload_schema.json', 'r') as f:
    validation = json.loads(f.read())
validations['um_validation'] = validation

with open(const.PATH + '/config/uber_ltr_payload_schema.json', 'r') as f:
    validation = json.loads(f.read())
validations['ltr_validation'] = validation

with open(const.PATH + '/config/uber_dup_payload_schema.json', 'r') as f:
    validation = json.loads(f.read())
validations['dup_validation'] = validation

with open(const.PATH + '/config/uber_ben_payload_schema.json', 'r') as f:
    validation = json.loads(f.read())
validations['ben_validation'] = validation

business_units = {}
with open(const.PATH + "/config/crnt_que_unit_nbr_um.txt", 'r') as f:
    units = [line.strip() for line in f]
business_units['um_business_units'] = units

with open(const.PATH + "/config/crnt_que_unit_nbr_dup.txt", 'r') as f:
    units = [line.strip() for line in f]
business_units['dup_business_units'] = units

with open(const.PATH + "/config/crnt_que_unit_nbr_ben.txt", 'r') as f:
    units = [line.strip() for line in f]
business_units['ben_business_units'] = units

with open(const.PATH + "/config/crnt_que_unit_nbr_ltr.txt", 'r') as f:
    units = [line.strip() for line in f]
business_units['ltr_business_units'] = units