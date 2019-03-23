import pickle
import constants as const
import csv
import yaml
import configparser
from itertools import filterfalse


with open(const.PATH + '/config/models.yml', 'r') as f:
    config = yaml.load(f)

usecases = config['usecases']
for usecase in usecases:
    print(usecase['name'])
    models = usecase['models']
    models[:] = filter(lambda x: x['active'], models)
    print(len(models))
    for model in models:
        print(model['model'])
        model['binary'] = pickle.load(open(const.PATH + '/models/' + usecase['name'] + '/' + model['path'], 'rb'))        


reject_list = []
with open(const.MODEL_REJECT_CODES, 'r') as csvfile:
    reader = csv.reader(csvfile, delimiter=',')
    for row in reader:
        reject_list.append(row[0])
    
other_list = []
with open(const.MODEL_OTHER_REASONS, 'r') as csvfile:
    reader = csv.reader(csvfile, delimiter=',')
    for row in reader:
        other_list.append(row[0])

model_encodings = {}
model_encodings['reject_list'] = reject_list
model_encodings['other_list'] = other_list

mappings = {}
with open(const.PATH + '/config/mapping-um.yml', 'r') as f:
    mapping = yaml.load(f)
mappings['um_mapping'] = mapping
