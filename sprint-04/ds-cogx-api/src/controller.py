from computeUM import computeUM
from computeLTR import computeLTR
from computeDUP import computeDUP
from computeBEN import computeBEN
import copy
from queue import Queue
import threading
import requests
import json
import constants
import response as resp_msg
import traceback,sys
# import mongoUtil as mutil


class FlowController():
    def __init__(self, app, logger, extra):
        self.app = app
        self.logger = logger
        self.extra = extra
        self.errors = []
        self.response = {}
    

    def modelExecution(self, url, model, payload, msg_queue):
        try:
            transid = self.extra['tags']['reqid'] + '-' + model
            headers = {'Content-type': 'application/json', 'Accept': 'text/plain', 'meta-transid': transid}
            resp = requests.post(url,verify=False, data=json.dumps(payload), headers=headers, timeout=(constants.CONNECTION_TIMEOUT, constants.READ_TIMEOUT))
            
        except Exception as e:
            self.logger.error(e, extra=self.extra)
            resp = '{"KEY_CHK_DCN_NBR":"' + payload['KEY_CHK_DCN_NBR'] + '", "respCd": 904, "respDesc": "' + constants.RESP_CD_MAP[904] + '", "messages":[{"msgCd": 100, "msgDesc":"Connection Timed out", "modelName":"' + model + '"}]}'
            msg_queue.put(lambda: self.on_complete(model, resp))
        else:
            msg_queue.put(lambda: self.on_complete(model, resp))


    def criteria_rules(self,payload,models=['um','dup','ben','ltr']):
        models = [x.lower() for x in models]
        model_criteria = {x.lower():False for x in models}
        err_cds = [] if payload.get('ERR_CDS') is None else payload.get('ERR_CDS')
        
        try:

            if 'um' in models:
                um_crnt_lst = self.app.config['business_units']['um_business_units']
                curnt_que = "" if payload.get('CURNT_QUE_UNIT_NBR') is None else payload.get('CURNT_QUE_UNIT_NBR')
                curnt_que_criteria_um = False

                if curnt_que in um_crnt_lst:
                    curnt_que_criteria_um = True
                criteria = ['UM0', 'UM1', 'UM2', 'PAM']
                
                criteria_met = any(x in err_cds for x in criteria)
                model_criteria['um'] = criteria_met and curnt_que_criteria_um
                # if len(models) ==1:
                #     return model_criteria['um']


            if 'dup' in models:
                dup_crnt_lst = self.app.config['business_units']['dup_business_units']
                curnt_que = "" if payload.get('CURNT_QUE_UNIT_NBR') is None else payload.get('CURNT_QUE_UNIT_NBR')
                curnt_que_criteria_dup = False

                if curnt_que in dup_crnt_lst:
                    curnt_que_criteria_dup = True

                criteria = ['PAJ', 'CBC', 'SCB', 'DAT', 'QCB', 'QCM', 'QC0', 'QC5', 'QC8', 'QTB', 'QTD', 'QTM', 'QTR',
                            'QTS', 'QT0', 'QT2', 'QT5', 'QT6', 'QT7', 'QT8', 'QV1', 'QV2', 'QV9', 'QX', 'QY', 'QZ',
                            'Q0', 'Q47', 'Q5', 'Q7', 'Q8', 'Q9']
                criteria_met = any(x in err_cds for x in criteria)
                model_criteria['dup'] = criteria_met and curnt_que_criteria_dup
                # if len(models) ==1:
                #     return model_criteria['dup']

            if 'ben' in models:
                ben_crnt_lst = self.app.config['business_units']['ben_business_units']
                crnt_que = "" if payload.get('CURNT_QUE_UNIT_NBR') is None else payload.get('CURNT_QUE_UNIT_NBR')
                curnt_que_criteria_ben = False

                if crnt_que in ben_crnt_lst:
                    curnt_que_criteria_ben = True

                model_criteria['ben'] = curnt_que_criteria_ben
                # if len(models) ==1:
                #     return criteria_met['ben']

            if 'ltr' in models:
                ltr_crnt_lst = self.app.config['business_units']['ltr_business_units']
                crnt_que = "" if payload.get('CURNT_QUE_UNIT_NBR') is None else payload.get('CURNT_QUE_UNIT_NBR')
                curnt_que_criteria_ltr = False

                if crnt_que in ltr_crnt_lst:
                    curnt_que_criteria_ltr = True

                model_criteria['ltr'] = curnt_que_criteria_ltr

        except:
            self.logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=self.extra)

        return model_criteria

    def resp_assembler(self,response = None):
        resp = None
        if response == None:
            response = self.response
        try:
            for key in response.keys():
                if resp == None:
                    resp = response[key]
                else:
                    resp_key = response[key]
                    if resp_key['respCd'] == 700 and resp['respCd'] != 700:
                        resp['respCd'] = resp_key['respCd']
                        resp['respDesc'] = resp_key['respDesc']
                    if resp_key.get('messages') is not None:
                        resp.get('messages').extend(resp_key.get('messages', []))
                    if resp_key.get('recommendations') is not None:
                        if resp.get('recommendations') is not None:
                            resp.get('recommendations').extend(resp_key.get('recommendations', []))
                        else:
                            resp['recommendations'] = resp_key.get('recommendations', None)
                    else:
                        if resp.get('recommendations') is None:
                            resp['recommendations'] = resp_key.get('recommendations', None)
                    if resp_key.get('modelInsights') is not None:
                        if resp.get('modelInsights') is not None:
                            resp.get('modelInsights').extend(resp_key.get('modelInsights', []))
                        else:
                            resp['modelInsights'] = resp_key.get('modelInsights', None)
                    else:
                        if resp.get('modelInsights') is None:
                            resp['modelInsights'] = resp_key.get('modelInsights', None)
        except:
            self.logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=self.extra)

        return resp

    def on_complete(self, model, resp):
        if isinstance(resp, str):
            self.response[model] = json.loads(resp)
        else:
            self.response[model] = json.loads(resp.text)
        if model == 'ltr' and self.response[model]['respCd'] == 700:
            return True
        return False

    def execute(self, payload, model=None):

        if model == 'ltr':
            criteria = self.criteria_rules(payload, ['ltr'])
            if criteria['ltr']:
                ltr = computeLTR(self.app, self.logger, self.extra)
                return ltr.process_request(copy.deepcopy(payload))
            else:
                # TODO : TEST below generate_desc
                return resp_msg.generate_desc(712, 'ltr', self.response, payload)
        elif model == 'um':            
            criteria = self.criteria_rules(payload, ['um'])
            if criteria['um']:
                um = computeUM(self.app, self.logger, self.extra)
                return um.process_request(copy.deepcopy(payload))
            else:
                # TODO : TEST below generate_desc
                return resp_msg.generate_desc(712, 'um', self.response, payload)
        elif model == 'dup':            
            criteria = self.criteria_rules(payload, ['dup'])
            if criteria['dup']:
                dup = computeDUP(self.app, self.logger, self.extra)
                return dup.process_request(copy.deepcopy(payload))
            else:
                # TODO : TEST below generate_desc
                return resp_msg.generate_desc(712, 'dup', self.response, payload)
            
        elif model == 'ben':            
            criteria = self.criteria_rules(payload, ['ben'])
            if criteria['ben']:
                ben = computeBEN(self.app, self.logger, self.extra)
                return ben.process_request(copy.deepcopy(payload))
            else:
                # TODO : TEST below generate_desc
                return resp_msg.generate_desc(712, 'ben', self.response, payload)

        elif model == 'ltr':
            ltr = computeLTR(self.app, self.logger, self.extra)
            criteria = self.criteria_rules(payload, ['ltr'])
            if criteria['ltr']:
                return ltr.process_request(copy.deepcopy(payload))
            else:
                # TODO : TEST below generate_desc
                return resp_msg.generate_desc(712, 'ltr', self.response, payload)
        else:
            userId = payload.get('userId', None)
            #print("userId: ", userId)
            models = []
            resp = {}
            criteria = self.criteria_rules(payload, ['um','dup','ben','ltr'])
            for model,value in criteria.items():
                if value == True:
                    models.append(model)

            if len(models) > 0:
                msg_queue = Queue()
                thread_count = threading.active_count()
                url = constants.NGINX_URL
                for model in models:
                    url1 = url + '/' + model
                    t = threading.Thread(target=self.modelExecution, args=(url1, model, payload, msg_queue))
                    t.start()

                while threading.active_count() > thread_count:
                    event = msg_queue.get()
                    if event():
                        break

                resp = self.resp_assembler()
                return resp
            else:
                resp['respCd'] = 712
                resp['respDesc'] = constants.RESP_CD_MAP[712]
                resp_msg.generate_result_row(resp, payload)
                
                return resp
        
