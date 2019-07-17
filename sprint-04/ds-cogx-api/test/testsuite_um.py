import unittest
import requests
import json
import pandas as pd
import traceback,sys
class TestCases(unittest.TestCase):
    def setUp(self):
        self.a = 10
        self.b = 20
        name = self.shortDescription()
        if name == "Add":
            self.a = 10
            self.b = 20
            print(name, self.a, self.b)
        if name == "sub":
            self.a = 50
            self.b = 60
            print(name, self.a, self.b)

    def tearDown(self):
        print('\nend of test', self.shortDescription())

    def test_endpoint_positive(self):
        ''' test est_endpoint_positive'''

        def out_message(count, content, message, sep=''):
            dict = {}
            dict['LINE_NUMBER'] = str(count)
            dict['respCD'] = str(content.get('respCd'))
            dict['message'] = message
            dict['KEY_CHK_DCN_NBR'] = content['KEY_CHK_DCN_NBR']
            dict['response'] = str(content)

            print('Line Number: ' + str(count) + ' - respCd: ' + str(content.get('respCd')) + " - " + message + ' - KEY_CHK_DCN_NBR : ' + content['KEY_CHK_DCN_NBR'] + ' - response : ' + str(content))
            #return json.dumps(dict)
            return 'Line Number: ' + str(count) + ' - respCd: ' + str(content.get('respCd')) + " - " + message + ' - KEY_CHK_DCN_NBR : ' + content['KEY_CHK_DCN_NBR'] + ' - response : ' + str(content) + sep

        umreport = open("report_um.generated", "w")
        umfile = open('..\\sample\\um\\formatted_uber.generated', 'r')
        df = pd.read_csv('..\\sample\\um\\UM_output_top3.csv', dtype={'DTL_LINE_NBR': 'S', 'RFRNC_NBR':'S', 'Result': 'S'})
        # ltrfile = open("../sample/ltr/formatted_uber.generated", "r")
        # url = 'http://VDAASW711187:9080/processClaim'
        url = 'http://localhost:9080/processClaim/um'
        headers = {"Content-Type": "application/json"}
        report_err = []
        no_refdata=0
        count = 0
        end_count = 9999
        for line in umfile:
            count = count + 1
            if count < 1:  # first to be processed = 0
                pass
            else:
                try:
                    data = line

                    response = requests.post(url, data=data, headers=headers)
                    content = response.text

                    content = json.loads(content)
                    error = 'error'
                    if content.get('respCd') == 700:
                        filtered = df.loc[df['KEY_CHK_DCN_NBR'] == content['KEY_CHK_DCN_NBR']]
                        matched = True
                        lineno = ""

                        for recommendation in content['recommendations']:
                            #print(recommendation)
                            lineno = recommendation['lineNumber']
                            if len(lineno) == 1:
                                lineno = '0'+lineno
                            row = filtered.loc[filtered['DTL_LINE_NBR'] == lineno]
                            if row.size == 0:
                                umreport.write(out_message(count, content, 'NO RECORD IN UM_OUTPUT_TOP3 for DTL_LINE_NBR:'+str(lineno), '\n'))
                                no_refdata =no_refdata+1
                                continue

                            row = row.to_dict(orient='records')
                            row = row[0]
                            #print(round(float(recommendation['modelScore']),5))
                            #print(round(row['Model_Score'],5))
                            str_details = "COUNTER : " + str(count) + " FOR :" + str(content["KEY_CHK_DCN_NBR"]) + " DTL_LINE : " + str(lineno) + " model_score mismatch between api_score  " + str(round(row['Model_Score'], 5)) + " and batch_score : " + str(round(float(recommendation['modelScore']), 5)) + " - " + str(abs(round(row['Model_Score'], 5) - round(float(recommendation['modelScore']), 5)))
                            print(str_details)
                            #print(row['Result'])
                            #print(recommendation['actionValue'])
                            if abs(round(row['Model_Score'],5) - round(float(recommendation['modelScore']),5)) < 0.000011 and int(row['Result']) == int(recommendation['actionValue']):
                                matched = True
                            else:
                                matched = False
                                break
                        if matched:
                            # print('matched - ' + content['KEY_CHK_DCN_NBR'])
                            pass
                        else:
                            #print(str(count) + ' - Did not match - ' + content['KEY_CHK_DCN_NBR'])
                            umreport.write(str_details+"\n")
                            umreport.write(out_message(count,content,' MISMATCH for DTL_LINE_NBR '+str(lineno),'\n'))
                    else:
                        filtered = df.loc[df['KEY_CHK_DCN_NBR'] == content['KEY_CHK_DCN_NBR']]
                        if filtered.size == 0:
                            umreport.write(out_message(count, content, content['respDesc'] + ' - NO RECORD IN TEST DATA ', '\n'))
                        else:
                            umreport.write(out_message(count, content, 'MISMATCH : '+ content['respDesc'] + ' - RECORD AVAILABLE IN TEST DATA ','\n'))

                except Exception as e:
                    print((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
                    report_err.append({'payload': data, 'error in getting attributes from response': response.text})
                finally:
                    umreport.flush()
                    if count > end_count:
                        break


        #print(str(report_err))

        if len(report_err) != 0:
            with umreport as f:
                for item in report_err:
                    f.write("%s\n" %item)
        self.assertTrue(len(report_err) == 0)

    def test_respcd(self):
        '''RESP_CD'''
        pass


def ordered(obj):
    if isinstance(obj, dict):
        return sorted((k, ordered(v)) for k, v in obj.items())
    if isinstance(obj, list):
        return sorted(ordered(x) for x in obj)
    else:
        return obj

def suite():
    suite_list = []
    #TODO : not working to add specifc test functions, any how suite execution all the test
    suite = unittest.TestSuite()
    suite.addTest(TestCases('testadd'))
##   suite.addTest (TestCases("testsub"))
##   suite.addTest(unittest.makeSuite(TestCases("testsub")))
    suite_list.append(suite)


    return unittest.TestSuite(suite_list)

if __name__ == '__main__':
    unittest.main()
    suite = unittest.TestSuite()
    suite.addTest(TestCases("testsub"))
    runner = unittest.TextTestRunner()
    runner.run(suite)
