import yaml
import os
import sys
import json
import configparser
import copy
PATH = os.path.dirname(os.path.abspath(__file__))
APP_PATH = os.path.split(PATH)[0]
sys.path.append(APP_PATH+"/src-lib/")
import json_transform


with open('sample/um/mapping-uber.yml', 'r') as f:
    mapping = yaml.load(f)

fp = open('sample/um/formatted_uber.generated', 'w')
files = ['sample/um/formatted_input.generated']
with open(files[0], encoding='ISO-8859-1') as f:
    for cnt, line in enumerate(f):
        obj = json_transform.transform(copy.deepcopy(mapping), json.loads(line)) 
        fp.write(json.dumps(obj) + '\n')


