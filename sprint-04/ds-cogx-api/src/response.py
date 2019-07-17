import copy

from constants import RESP_CD_MAP

def generate_result_row(result_row, payload):
    if result_row.get('respCd', None) == None:
        result_row['respCd'] = 0
        result_row['respDesc'] = ''
        result_row['KEY_CHK_DCN_NBR'] = payload.get('KEY_CHK_DCN_NBR', None)
        result_row['KEY_CHK_DCN_ITEM_CD'] = payload.get('KEY_CHK_DCN_ITEM_CD', None)
        result_row['KEY_CHK_DCN_CENTRY_CD'] = payload.get('KEY_CHK_DCN_CENTRY_CD', None)
        result_row['KEY_ADJ_SEQ_NBR'] = payload.get('KEY_ADJ_SEQ_NBR', None)
        result_row['messages'] = []
        result_row['recommendations'] = []
        result_row['modelInsights'] = []
    
    if result_row.get('messages',None) == None:
        message = {}
        message['msgCd'] = result_row.get('respCd',0)
        message['msgDesc'] = result_row.get('respDesc','')
        result_row['messages'] = [message]
    

def generate_messages(model_name, errors, result_row):
    messages = []
    for error in errors:
        msg = {}
        msg['msgCd'] = 100
        msg['msgDesc'] = error
        msg['modelName'] = model_name
        messages.append(msg)
    result_row['messages'] = messages


def generate_desc(respCd, model_name, result_row, payload):
    result_row['respCd'] =respCd
    result_row['respDesc'] = RESP_CD_MAP[respCd] if respCd in RESP_CD_MAP.keys() else 'NOT FOUND'
    result_row['KEY_CHK_DCN_NBR'] = payload.get('KEY_CHK_DCN_NBR', None)
    result_row['KEY_CHK_DCN_ITEM_CD'] = payload.get('KEY_CHK_DCN_ITEM_CD', None)
    result_row['KEY_CHK_DCN_CENTRY_CD'] = payload.get('KEY_CHK_DCN_CENTRY_CD', None)
    result_row['KEY_ADJ_SEQ_NBR'] = payload.get('KEY_ADJ_SEQ_NBR', None)
    # adding msg to result row to summarize the models that are executed
    msg = {}
    msg['msgCd'] = respCd
    msg['msgDesc'] = result_row['respDesc']
    msg['modelName'] = model_name
    msgs = result_row.get('messages', [])
    msgs.append(msg)
    result_row['messages'] = msgs
    return result_row


def extra_tags(extra, tags, flag=1):
    extra_updated = copy.deepcopy(extra)
    if flag == 1:
        extra_updated.update({'payload': tags})
    else:
        try:
            extra_updated.update(dict(tags))
        except:
            extra_updated.update({'payload': tags})
    return extra_updated
