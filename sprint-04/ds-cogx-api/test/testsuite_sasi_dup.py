import unittest
import requests
import json
import pandas as pd
import os


class TestCases(unittest.TestCase):

    def test_endpoint_positive(self):
        cwd = os.getcwd()
        ''' test est_endpoint_positive'''
        dupreport = open("report_dup.generated", "w")
        notfoundreport = open("report_not_found.generated", "w")
        mismatchreport = open("report_mismatch.generated", "w")

        dupfile = open('../sample/dup/formatted_uber.generated', 'r')
        df = pd.read_csv('../sample/dup/CnB_final_out_filtered.csv', dtype={'KEY_CHK_DCN_NBR': 'S', 'actioncode': 'S'})
        url = 'http://localhost:9080/processClaim/dup'
        headers = {"Content-Type": "application/json"}
        report_err = []
        count = 0
        for line in dupfile:
            data1 = json.loads(line)
            #print("KEY_CHK_DCN_NBR " + str(data1['KEY_CHK_DCN_NBR']))
            present = df.loc[df['KEY_CHK_DCN_NBR'] == data1['KEY_CHK_DCN_NBR']]
            if present.size != 0:
                data = line
                # print(data)
                response = requests.post(url, data=data, headers=headers)
                content = response.text
                try:
                    content = json.loads(content)
                    error = 'error'
                    if content.get('respCd') == 700:
                        filtered = df.loc[df['KEY_CHK_DCN_NBR'] == content['KEY_CHK_DCN_NBR']]
                        matched = True
                        for recommendation in content['recommendations']:
                            row = filtered.to_dict(orient='records')
                            row = row[0]
                            if float(str(row['Top 1 Score'])[:5]) == float(str(recommendation['Top 1 Score'])[:5]) and 'recommendation' in row and row['recommendation'] != []:
                                matched = True
                            else:
                                matched = False
                                print('===== Not matched - ' + str(content['KEY_CHK_DCN_NBR']) + '==================')
                                break
                    else:
                        error = 'respCd issue'
                        print('respCode: ' + str(content.get('respCd')) + ' - ' + str(content['KEY_CHK_DCN_NBR']))
                        report_err.append({'payload': data, 'error respCd': response.text})

                except Exception as e:
                    print(e)
                    report_err.append({'payload': data, 'error in getting attributes from response': response.text})
                finally:
                    count = count + 1
                if count > 20000:
                    break
            else:
                notfoundreport.write(str(data1['KEY_CHK_DCN_NBR']) + ' is not present in batch_run.txt' + '\n')
                count = count + 1
                if count > 20000:
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
