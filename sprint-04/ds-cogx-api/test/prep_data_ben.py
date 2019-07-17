import csv
import json


files = ['sample/ben/claim_line.txt']
claim_lines = {}
with open(files[0], encoding='ISO-8859-1') as csvfile:
    reader = csv.DictReader(csvfile)
    title = reader.fieldnames
    for row in reader:
        print(row[title[0]],"---", row[title[21]])
        if claim_lines.get(row[title[0]]+row[title[21]], None) == None:
            pass
        else:
            print(row[title[0]])
        claim_lines[row[title[0]]+row[title[21]]] = {title[i]:row[title[i]] for i in range(len(title))}

print(len(claim_lines.keys()))

f = open('sample/ben/formatted_input.generated', 'w')

for key in claim_lines.keys():
    obj = {}
    obj['ben_claim'] = claim_lines.get(key,  {})
    f.write(json.dumps(obj) + '\n')
f.flush()
f.close()
