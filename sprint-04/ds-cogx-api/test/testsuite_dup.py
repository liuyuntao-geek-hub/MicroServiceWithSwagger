import unittest
import requests
import json
import pandas as pd
import os
class TestCases(unittest.TestCase):

    def test_endpoint_positive_ltr(self):
        ''' test est_endpoint_positive'''


        dup_report = open("report_dup.generated", "w")
        dupfile = open('..\\sample\\dup\\formatted_uber.generated', 'r')
        df = pd.read_csv('..\\sample\\dup\\CnB_final_out_all.csv', dtype={'KEY_CHK_DCN_NBR': 'S', 'actioncode': 'S'})

        url = 'http://localhost:9080/processClaim/dup'
        # url = 'http://VDAASW711187:9080/processClaim'
        headers = {"Content-Type": "application/json"}
        report_err = []
        count = 0
        end_count = 9999

        def out_message(count,content,message,sep=''):
            print('Line Number: ' + str(count) + ' - respCd: ' + str(content.get('respCd')) + " - " + message + ' - KEY_CHK_DCN_NBR : ' + content['KEY_CHK_DCN_NBR'] + ' - response : ' + str(content))
            #return json.dumps(dict)
            return 'Line Number: ' + str(count) + ' - respCd: ' + str(content.get('respCd')) + " - " + message + ' - KEY_CHK_DCN_NBR : ' + content['KEY_CHK_DCN_NBR'] + ' - response : ' + str(content) + sep


        for line in dupfile:
            count = count + 1
            if count <439:  # first to be processed = 0
                pass
            else:
                try:
                    data = line
                    # print(data)
                    response = requests.post(url, data=data, headers=headers)
                    content = response.text
                    content = json.loads(content)
                    print("count : "+str(count)+" working on : "+content['KEY_CHK_DCN_NBR'])
                    error = 'error'
                    if content.get('respCd') == 700:
                        filtered = df.loc[df['KEY_CHK_DCN_NBR'] == content['KEY_CHK_DCN_NBR']]
                        if filtered.size == 0 :
                            dup_report.write(out_message(count, content,'NO RECORD IN TEST DATA FILE for KEY_CHK_DCN_NBR:' + str(content['KEY_CHK_DCN_NBR']),'\n'))
                            continue
                        else:
                            pass
                            #print('FOUND record in batch_run for KEY CHK_DCN_NBR : '+content['KEY_CHK_DCN_NBR'])
                        matched = True
                        for recommendation in content['recommendations']:
                            row = {}
                            row = filtered.to_dict(orient='records')
                            row = row[0]
                            print(round(row['Top 1 Score'],5))
                            print(round(recommendation['Top 1 Score'],5))
                            print(abs(round(row['Top 1 Score'],5) - round(recommendation['Top 1 Score'],5)))
                            if abs(round(row['Top 1 Score'],5) - round(recommendation['Top 1 Score'],5)) < 0.000011 and row['CURNT_QUE_UNIT_NBR'] == recommendation['CURNT_QUE_UNIT_NBR'] and row['recommendation'] == recommendation['recommendation']:
                                matched = True
                            else:
                                st = ' MISMATCH '
                                matched = False

                            if matched:
                                print('matched - ' + content['KEY_CHK_DCN_NBR'])
                                pass
                            else:
                                dup_report.write(out_message(count, content,st, '\n'))


                    else:
                        # dup_report.write(out_message(count, content, content['respDesc'], '\n'))
                        filtered = df.loc[df['KEY_CHK_DCN_NBR'] == content['KEY_CHK_DCN_NBR']]
                        if filtered.size == 0:
                            dup_report.write(out_message(count, content, content['respDesc'] + ' - NO RECORD IN TEST DATA ', '\n'))
                        else:
                            dup_report.write(out_message(count, content,' MISMATCH : '+ content['respDesc'] + ' - RECORD AVAILABLE IN TEST DATA ','\n'))

                except Exception as e:
                    print(e)
                    report_err.append({'payload': data, 'error in getting attributes from response': response.text})
                finally:
                    #Change the counter to run the no of req
                    #writer.save()
                    dup_report.flush()
                    if count > end_count:
                        break

        #print(str(report_err))
        if len(report_err) != 0:
            with dup_report as f:
                for item in report_err:
                    f.write("%s\n" % item)
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