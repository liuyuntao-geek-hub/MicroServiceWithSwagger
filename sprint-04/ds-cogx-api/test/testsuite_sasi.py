import unittest
import requests
import json
import pandas as pd
import os


class TestCases(unittest.TestCase):

    def test_endpoint_positive(self):
        cwd = os.getcwd()
        ''' test est_endpoint_positive'''
        ltrreport = open("report_ltr.generated", "w")

        umfile = open('..\\sample\\ltr\\formatted_uber.generated', 'r')
        df = pd.read_csv('..\\sample\\ltr\\batch_run.txt', dtype={'KEY_CHK_DCN_NBR': 'S', 'actioncode':'S'})
        # ltrfile = open("../sample/ltr/formatted_uber.generated", "r")
        # url = 'http://VDAASW711187:9080/processClaim'
        url = 'http://localhost:9080/processClaim'
        headers = {"Content-Type": "application/json"}
        report_err = []
        count = 0
        for line in umfile:
            data = line
            # print(data)
            response = requests.post(url, data=data, headers=headers)
            content = response.text
            try:
                content = json.loads(content)
                error = 'error'
                if content.get('respCd') == '200':
                    filtered = df.loc[df['KEY_CHK_DCN_NBR'] == content['KEY_CHK_DCN_NBR']]
                    matched = True
                    for recommendation in content['recommendations']:
                        row = filtered.to_dict(orient='records')
                        row = row[0]

                        if row['actioncode'] == recommendation['actionCode'] and abs(row['actionscore'] - recommendation['actionValue']) < 0.0000001:
                            matched = True
                        else:
                            matched = False
                            print('recom_action_value ' + str(recommendation['actionValue']))
                            print('row_action_score ' + str(row['actionscore']))
                            print('recomm_action_code ' + str(recommendation['actionCode']))
                            print('row_action_code ' + str(row['actioncode']))
                            break
                    if matched:
                        # print('matched - ' + content['KEY_CHK_DCN_NBR'])       
                        pass
                    else:
                        print('Line number: ' + str(count) + ' - Did not match - ' + content['KEY_CHK_DCN_NBR'])
                else:
                    error = 'respCd issue'
                    print('respCode: ' + content.get('respCd') + ' - ' + content['KEY_CHK_DCN_NBR'])
                    report_err.append({'payload': data, 'error respCd': response.text})
                    
            except Exception as e:
                print(e)
                report_err.append({'payload': data, 'error in getting attributes from response': response.text})
            finally:
                count = count + 1                
                if count > 100:
                    break
                
        print(str(report_err))
        
        if len(report_err) != 0:
            with ltrreport as f:
                for item in report_err:
                    f.write("%s\n" %item)
        self.assertTrue(len(report_err) == 0)


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
