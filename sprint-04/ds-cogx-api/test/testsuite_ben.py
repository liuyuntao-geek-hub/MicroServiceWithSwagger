import unittest
import requests
import json
import pandas as pd
import os
import numpy as np

class TestCases(unittest.TestCase):

    def test_endpoint_positive(self):
        cwd = os.getcwd()
        ''' test est_endpoint_positive'''
        dupreport = open("report_ben.generated", "w")
        notfoundreport = open("report_not_found.generated", "w")
        mismatchreport = open("report_mismatch.generated", "w")

        dupfile = open('../sample/ben/formatted_uber.generated', 'r')
        df = pd.read_csv('../sample/ben/Benefits_Pilot_Filtered.csv', dtype={'DCN': 'S',  'Line#':'S', 'Benefits': 'S', 'max_score': 'S'})
        df.rename(columns={'Line#' : 'Line'}, inplace=True)
        df['Line'] = df['Line'].astype(float)
        df['Line'] = df['Line'].astype(int)
        url = 'http://localhost:9080/processClaim/ben'
        headers = {"Content-Type": "application/json"}
        report_err = []
        count = 0
        i = 0
        for line in dupfile:
            data1 = json.loads(line)
            #print("KEY_CHK_DCN_NBR " + str(data1['KEY_CHK_DCN_NBR']))
            present = df.loc[df['DCN'] == data1['KEY_CHK_DCN_NBR']]   # && df['Line#'] == data1['lineNumber']]
            if present.size != 0:
                data = line
                # print(data)
                response = requests.post(url, data=data, headers=headers)
                content = response.text
                try:
                    content = json.loads(content)
                    error = 'error'
                    if content.get('respCd') == 700 :
                        filtered = df.loc[df['DCN'] == content['KEY_CHK_DCN_NBR']]
                        matched = True
                        rec = content['recommendations']
                        rec = pd.DataFrame(rec)
                        #print(rec)
                        #print(content['recommendations'])
                        #print("==================================================================================================")
                        for recommendation in content['recommendations']:
                            rec = int(recommendation['lineNumber'])
                            df_tmp = filtered.loc[filtered['Line'] == float(recommendation['lineNumber'])]
                            row = df_tmp.to_dict(orient='records')
                            row = row[0]
                            if float(str(row['max_score'])[:5]) == float(str(recommendation['modelScore'])[:5]) and str(int(row['Line'])) == str(int(recommendation['lineNumber'])) and recommendation['actionValue'] == row['Benefits'][:15] :
                                matched = True
                            else:
                                matched = False
                                print('+++++++ Not matched - DCN ' + str(content['KEY_CHK_DCN_NBR']) + ' +++++ LINE NO ' + str(recommendation['lineNumber']) + ' ++++ '+str(row['Line'])+' ++++ BATCH SCORE ++++ '+ str(row['max_score']) + ' ++++ MODEL SCORE ++++ '+str(recommendation['modelScore']) + '+++++' + recommendation['actionValue'] + ' m ++ b' + row['Benefits'])
                                
                    else:
                        error = 'respCd issue'
                        if content.get('respCd') == 711 :
                            filtered = df.loc[df['DCN'] == content['KEY_CHK_DCN_NBR']]
                            for i, row in filtered.iterrows():
                                if str(row['max_score']) == str(0.0) :
                                    matched = True
                                else:
                                    print('respCode: ' + str(content.get('respCd')) + ' -  not matched - ' + str(content['KEY_CHK_DCN_NBR']))
                                    report_err.append({'payload': data, 'error respCd': response.text})

                            matched = True
                        else:
                            print('respCode: ' + str(content.get('respCd')) + ' - ' + str(content['KEY_CHK_DCN_NBR']))
                            report_err.append({'payload': data, 'error respCd': response.text})

                except Exception as e:
                    print(e)
                    report_err.append({'payload': data, 'error in getting attributes from response': response.text})
                finally:
                    count = count + 1
                    #print(count)
                if count > 5000:
                    break
            else:
                notfoundreport.write(str(data1['KEY_CHK_DCN_NBR']) + ' is not present in batch_run.txt' + '\n')
                count = count + 1
                #print(count)
                if count > 5000:
                    break
        print(str(report_err))
        
        if len(report_err) != 0:
            with dupreport as f:
                for item in report_err:
                    f.write("%s\n" %item)
        #self.assertTrue(len(report_err) == 0)


def suite():
    suite_list = []
#    TODO : not working to add specifc test functions, any how suite execution all the test
    suite = unittest.TestSuite()
    suite.addTest(TestCases('test_endpoint_positive'))
#   suite.addTest (TestCases("testsub"))
#   suite.addTest(unittest.makeSuite(TestCases("testsub")))
    suite_list.append(suite)

    return unittest.TestSuite(suite_list)


if __name__ == '__main__':
    unittest.main()
    suite = unittest.TestSuite()
    runner = unittest.TextTestRunner()
    runner.run(suite)
