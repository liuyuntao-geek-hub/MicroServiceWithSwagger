import csv
import json
import datetime


files = ['sample/dup/current_header.txt', 'sample/dup/current_edits.txt', 'sample/dup/current_detail.txt', 'sample/dup/history_header.txt', 'sample/dup/history_detail.txt']

claims = {}
obj_current = {}

with open(files[0], encoding='ISO-8859-1') as csvfile:
    reader = csv.DictReader(csvfile)
    title = reader.fieldnames
    field_mapping = {}
    #field_mapping1 = {}
    for col in title:
        if title.index(col) is not None:
            field_mapping[col] = title.index(col)
            #field_mapping1[col] = title.index(col)
    for row in reader:
        claims[row[title[0]] + row[title[1]]] = {'header': {key:row[title[field_mapping[key]]] for key in field_mapping.keys()}}
        #obj_current[row[title[37]] + row[title[5]] + row[title[45]] + row[title[46]]] = {key:row[title[field_mapping1[key]]] for key in field_mapping1.keys()}

print('Done processing - ' + files[0])

edits = {}
with open(files[1], encoding='ISO-8859-1') as csvfile:
    reader = csv.DictReader(csvfile)
    title = reader.fieldnames
    field_mapping = {}
    for col in title:
        if title.index(col) is not None:
            field_mapping[col] = title.index(col)
    for row in reader:
        if edits.get(row[title[0]] + row[title[1]]) is not None:
            edits.get(row[title[0]] + row[title[1]]).extend([{key:row[title[field_mapping[key]]] for key in field_mapping.keys()}])
        else:
            edits[row[title[0]] + row[title[1]]] = [{key:row[title[field_mapping[key]]] for key in field_mapping.keys()}]
print('Done processing - ' + files[1])


details = {}

with open(files[2], encoding='ISO-8859-1') as csvfile:
    reader = csv.DictReader(csvfile)
    title = reader.fieldnames
    field_mapping = {}
    for col in title:
        if title.index(col) is not None:
            field_mapping[col] = title.index(col)
    for row in reader:
        if details.get(row[title[0]] + row[title[1]]) is not None:
            details.get(row[title[0]] + row[title[1]]).extend([{key:row[title[field_mapping[key]]] for key in field_mapping.keys()}])
        else:
            details[row[title[0]] + row[title[1]]] = [{key:row[title[field_mapping[key]]] for key in field_mapping.keys()}]
print('Done processing - ' + files[2])

hist_header = {}

hh = open('sample/dup/history_header.generated', 'w')
s = ','
with hh as f:
    with open(files[3], encoding='ISO-8859-1') as csvfile:
        reader = csv.DictReader(csvfile)
        title = reader.fieldnames
        field_mapping = {}
        f.write(s.join(title) + '\n')
        #field_mapping1 = {}
        for col in title:
            if title.index(col) is not None:
                field_mapping[col] = title.index(col)
                #field_mapping1[col] = title.index(col)
        for row in reader:
            # print(row.values())
            dt = row[title[2]]
            dt = datetime.datetime.strptime(dt, '%m/%d/%Y').strftime('%Y-%m-%d')
            row[title[2]] = dt
            dt = row[title[8]]
            dt = datetime.datetime.strptime(dt, '%m/%d/%Y').strftime('%Y-%m-%d')
            row[title[8]] = dt
            dt = row[title[9]]
            dt = datetime.datetime.strptime(dt, '%m/%d/%Y').strftime('%Y-%m-%d')
            row[title[9]] = dt
            if row[title[12]].find(',') != -1:
                row[title[12]] = '\"' + row[title[12]] + '\"'
            if row[title[11]].find(',') != -1:
                row[title[11]] = '\"' + row[title[11]] + '\"'
            if row[title[19]].find(',') != -1:
                row[title[19]] = '\"' + row[title[19]] + '\"'
            hist_header[row[title[0]] + row[title[1]] + row[title[2]]] = {'hist_header': {key: row[title[field_mapping[key]]] for key in field_mapping.keys()}}
            s = ','
            f.write(s.join(row.values()) + '\n')

print('Done processing - ' + files[3])


hist_details = {}
hd = open('sample/dup/history_detail.generated', 'w')
s = ','
with hd as f:
    with open(files[4], encoding='ISO-8859-1') as csvfile:
        reader = csv.DictReader(csvfile)
        title = reader.fieldnames
        field_mapping = {}
        f.write(s.join(title) + '\n')
        for col in title:
            if title.index(col) is not None:
                field_mapping[col] = title.index(col)
        for row in reader:
            dt = row[title[2]]
            dt = datetime.datetime.strptime(dt, '%m/%d/%Y').strftime('%Y-%m-%d')
            row[title[2]] = dt
            dt = row[title[21]]
            dt = datetime.datetime.strptime(dt, '%m/%d/%Y').strftime('%Y-%m-%d')
            row[title[21]] = dt
            dt = row[title[22]]
            dt = datetime.datetime.strptime(dt, '%m/%d/%Y').strftime('%Y-%m-%d')
            row[title[22]] = dt
            f.write(s.join(row.values()) + '\n')
            if hist_details.get(row[title[0]] + row[title[1]] + row[title[2]]) is not None:
                hist_details.get(row[title[0]] + row[title[1]] + row[title[2]]).extend([{key:row[title[field_mapping[key]]] for key in field_mapping.keys()}])
            else:
                hist_details[row[title[0]] + row[title[1]] + row[title[2]]] = [{key:row[title[field_mapping[key]]] for key in field_mapping.keys()}]
print('Done processing - ' + files[4])

keys_1 = []
keys_2 = []
keys_3 = []
merge_claims = {}
for idx, key in enumerate(claims.keys()):
    obj = claims.get(key,  {})
    obj['edits'] = edits.get(key, [])
    if len(obj['edits']) > 0:
        if key not in keys_1:
            keys_1.append(key)
        else:
            print("key already exists in key_1:", key)
    obj['details'] = details.get(key, [])
    if len(obj['details']) > 0:
        if key not in keys_2:
            keys_2.append(key)
        else:
            print("key already exists in key_2:", key)
    obj_current = obj
    #print("obj:", obj)
    key = obj['header']['MEMBER_SSN'] + obj['header']['PAT_MBR_CD'] + datetime.datetime.strptime(obj['header']['SRVC_FROM_DT'], '%m/%d/%Y').strftime('%Y-%m-%d') + datetime.datetime.strptime(obj['header']['SRVC_THRU_DT'], '%m/%d/%Y').strftime('%Y-%m-%d')
    merge_claims[key] = obj_current



merge_hist = {}
for idx1, key1 in enumerate(hist_header.keys()):
    obj_hist = hist_header.get(key1, {})
    obj_hist['hist_details'] = hist_details.get(key1, [])
    if len(obj_hist['hist_details']) > 0:
        keys_3.append(key1)
    key = obj_hist['hist_header']['MEMBER_SSN'] + obj_hist['hist_header']['PAT_MBR_CD'] + obj_hist['hist_header']['SRVC_FROM_DT'] + obj_hist['hist_header']['SRVC_THRU_DT']
    hist = merge_hist.get(key, [])
    hist.append(obj_hist)
    merge_hist[key] = hist
    #print("merge_hist:", merge_hist)
#print("merge_hist keys:", merge_hist.keys())

obj_final = {}

f = open('sample/dup/formatted_input.generated', 'w')

for idx, key in enumerate(merge_claims.keys()):
    print(idx)
    obj_c = merge_claims.get(key, {})
    obj_h = merge_hist.get(key, {})
    obj_c.update({"hist": obj_h})
    #obj_final[key] = obj_c
    #print("after update obj_c:", obj_c)
    f.write(json.dumps(obj_c) + '\n')



print('########################################')
print("len_claims-header:",len(claims.keys()))
# hf = open('sample/dup/header_keys.txt','w')
# hf.write(str(claims.keys()))
print("len keys_1-edits",len(keys_1))
# k1f = open('sample/dup/keys_1.txt','w')
# k1f.write(str(keys_1))
print("len keys_2-details",len(keys_2))
# k2f = open('sample/dup/keys_2.txt','w')
# k2f.write(str(keys_2))
print("len keys_3",len(keys_3))

#print("keys_5[0]:",keys_5[0])

