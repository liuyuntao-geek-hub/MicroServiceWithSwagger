import unittest
import requests
import json
import pandas as pd
import os


class TestCases(unittest.TestCase):

    def test_endpoint_positive(self):
        ''' test est_endpoint_positive'''

        ltrreport = open("report_ltr.generated", "w")
        umfile = open('..\\sample\\ltr\\formatted_uber.generated', 'r')
        df = pd.read_csv('..\\sample\\ltr\\batch_run.txt', dtype={'KEY_CHK_DCN_NBR': 'S', 'actioncode':'S'})

        url = 'http://localhost:9080/processClaim/ltr'
        #url = 'http://VDAASW711187:9080/processClaim'
        headers = {"Content-Type": "application/json"}
        report_err = []
        count = 0
        start_count = 0
        end_count = 500

        def out_message(count,content,message,sep=''):
            print('Line Number: ' + str(count) + ' - respCd: ' + str(content.get('respCd')) + " - " + message + ' - KEY_CHK_DCN_NBR : ' + content['KEY_CHK_DCN_NBR'] + ' - response : ' + str(content))
            #return json.dumps(dict)
            return 'Line Number: ' + str(count) + ' - respCd: ' + str(content.get('respCd')) + " - " + message + ' - KEY_CHK_DCN_NBR : ' + content['KEY_CHK_DCN_NBR'] + ' - response : ' + str(content) + sep


        for line in umfile:
            data = line
            if count < start_count:
                count+=1
            else:
                data = line
                try:
                    response = requests.post(url, data=data, headers=headers)
                    content = response.text
                    # print(data)
                    content = json.loads(content)

                    error = 'error'
                    #TODO : Need to update the code as per the new respCd and desc
                    if content.get('respCd') == 700:
                        filtered = df.loc[df['KEY_CHK_DCN_NBR'] == content['KEY_CHK_DCN_NBR']]
                        matched = True

                        for recommendation in content['recommendations']:
                            if filtered.size == 0:
                                ltrreport.write(out_message(count, content, 'NO RECORD IN TEST DATA', '\n'))
                                no_refdata = no_refdata + 1
                                continue
                            row = filtered.to_dict(orient='records')
                            row: dict = row[0]
                            row['actioncode'] = 'R'+ str(row['actioncode'])

                            if row['actioncode'] == str(recommendation['actionValue']) and abs(row['actionscore'] - recommendation['modelScore']) < 0.00000011:
                                matched = True
                                '''
                                print('Recommendation: action_value ' + str(recommendation['modelScore']))
                                print('Batch process:  action_score ' + str(row['actionscore']))
                                print('Recommendation: action_code ' + str(recommendation['actionValue']))
                                print('Batch Process:  action_code ' + str(row['actioncode']))
                                '''
                            else:
                                matched = False
                                print('===== Not matched - ' + content['KEY_CHK_DCN_NBR'] + '==================')
                                print('Recommendation: action_value ' + str(recommendation['modelScore']))
                                print('Batch process:  action_score ' + str(row['actionscore']))
                                print('Recommendation: action_code ' + str(recommendation['actionValue']))
                                print('Batch Process:  action_code ' + str(row['actioncode']))
                                #
                                ltrreport.write(out_message(count,content,' MISMATCH ','\n'))

                        if matched:
                            print('matched - ' + content['KEY_CHK_DCN_NBR'])
                            pass

                    else:

                        filtered = df.loc[df['KEY_CHK_DCN_NBR'] == content['KEY_CHK_DCN_NBR']]
                        if filtered.size == 0:
                            ltrreport.write(out_message(count, content, content['respDesc'] + ' - NO RECORD IN TEST DATA ', '\n'))
                        else:
                            ltrreport.write(out_message(count, content, 'MISMATCH : ' + content['respDesc'] + ' - RECORD AVAILABLE IN TEST DATA ', '\n'))

                except Exception as e:
                    import traceback,sys
                    print((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
                    report_err.append({'payload': data, 'error in getting attributes from response': response.text})
                finally:
                    ltrreport.flush()
                    count = count + 1
                    if count == end_count:  # last to process = 10
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
