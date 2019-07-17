import datetime
import json
import os
import sys
import traceback
import uuid
from flasgger import Swagger
from flask import request, make_response, Flask
import hbase
# from pymongo import MongoClient


PATH_1 = os.path.dirname(os.path.abspath(__file__))
APP_PATH = os.path.split(PATH_1)[0]
sys.path.append(APP_PATH + "/src-lib/")
from rotate_logs import get_logger
from cache import usecases, model_encodings, mappings, validations, data_frames, business_units
from controller import FlowController
import constants
import response as util
# from scheduler import before_first_request

app = Flask(__name__)
app.config.update(MAX_CONTENT_LENGTH=1 * 1024 * 1024)
app.config['usecases'] = usecases
app.config['model_encodings'] = model_encodings
app.config['mappings'] = mappings
app.config['validations'] = validations
app.config['data_frames'] = data_frames
app.config['business_units'] = business_units
# make maxPoolSize = None for unbounded
# app.config['mongo_client'] = MongoClient(constants.MONGO_URL, connect=False, ssl=True, ssl_ca_certs=constants.MONGO_PEM, appname = 'COGX_API', maxPoolSize = 200)

app.config['SWAGGER'] = {
    'swagger_version': '2.0',
    'specs': [
        {
            'version': '0.1alpha',
            'title': 'Cognitive Claims API (v1)',
            'description': 'Builds a API for Cognitive Claims models',
            'endpoint': 'v1_spec',
            'route': '/'
        }
    ]
}

Swagger(app)

@app.route('/processClaim', methods=['POST'])
def processClaim(model=None):
    """
        Post request for Cognitive Claims processing
        ---
        tags:
            - Cognitive Claims

        summary: 'processClaim'
        consumes:
            - application/json
        produces:
            - application/json
        parameters:
            - name: meta-senderapp
              in: header
              required: true
              type: string
              description: 'Identify the client making the call'
            - name: meta-transid
              in: header
              required: true
              type: string
              description: 'Identify single transaction in from a session'
            - name: meta-sessionid
              in: header
              type: string
              required: true
              description: 'session id used by the client to trace the route'
            - name: processClaim
              in: body
              required: true
              description: JSON input for request
              schema:
                type: object
                properties:
                    userId:
                        type: string
                    KEY_CHK_DCN_NBR:
                        type: string
                    KEY_CHK_DCN_CENTRY_CD:
                        type: string
                    KEY_CHK_DCN_ITEM_CD:
                        type: string
                    KEY_ADJ_SEQ_NBR:
                        type: string
                    BHVRL_HLTH_ACS_PROV_IND:
                        type: string
                    ENCNTR_IND:
                        type: string
                    ITS_HOME_IND:
                        type: string
                    ITS_PARG_PROV_IND:
                        type: string
                    MBU_CD:
                        type: string
                    MK_FUND_TYPE_CD:
                        type: string
                    PRMRY_NTWK_CD:
                        type: string
                    PROV_IND:
                        type: string
                    ATCHMNT_IND:
                        type: string
                    BYPS_CDS:
                        type: array
                        items:
                        type: string
                    CLM_TYPE_CD:
                        type: string
                    DTL_LINE_NBR:
                        type: string
                    EDI_CLM_FLNG_IND:
                        type: string
                    PROV_ST_CD:
                        type: string
                    SBSCRBR_ST_CD:
                        type: string
                    SRC_CD:
                        type: string
                    SRVC_TYPE_CURNT_CD:
                        type: string
                    SRVC_TYPE_ORGNL_CD:
                        type: string
                    PROV_LCNS_ALPH_CD:
                        type: string
                    PROV_SPCLTY_CD:
                        type: string
                    ORGNL_ENTRY_DT:
                        type: string
                    SRVC_FROM_DT:
                        type: string
                    SRVC_THRU_DT:
                        type: string
                    BSIC_PAYMNT_AMT:
                        type: number
                        format: double
                    COB_BSIC_PAYMNT_AMT:
                        type: number
                        format: double
                    COB_MM_PAYMNT_AMT:
                        type: number
                        format: double
                    DDCTBL_AMT:
                        type: number
                        format: double
                    MM_PAY_AMT:
                        type: number
                        format: double
                    ELGBL_EXPNS_AMT:
                        type: number
                        format: double
                    RJCT_AMT:
                        type: number
                        format: double
                    TOTL_CHRG_AMT:
                        type: number
                        format: double
                    TOTL_NON_ELGBL_AMT:
                        type: number
                        format: double
                    WRTOF_AMT:
                        type: number
                        format: double
                    PAT_MBR_CD:
                        type: number
                        format: int32
                    PAT_AGE_NBR:
                        type: number
                        format: int32
                        minimum: 0
                        maximum: 110
                    COB_SGMNT_CNT:
                        type: number
                        format: int32
                    MEDCR_CNT:
                        type: number
                        format: int32
                    LPP_INT_AMT:
                        type: number
                        format: double
                    PROV_CITY_NM:
                        type: string
                    SBSCRBR_CITY_NM:
                        type: string
                    ITS_ORGNL_SCCF_NEW_NBR:
                        type: string
                    PROV_NM:
                        type: string
                    PROV_SCNDRY_NM:
                        type: string
                    ERR_CDS:
                        type: array
                        items:
                        type: string
                    PROV_ZIP_4_CD:
                        type: string
                    SBSCRBR_ZIP_4_CD:
                        type: string
                    PROV_ZIP_5_CD:
                        type: string
                    SBSCRBR_ZIP_5_CD:
                        type: string
                    icdPrimaryCds:
                        type: array
                        items:
                        type: object
                        properties:
                            ICD_CD:
                                type: string
                    LMT_CLS:
                        type: array
                        items:
                            type: string
                    FULL_MNGMNT_BU_CD:
                        type: string
                    PROV_LCNS_NMRC_CD:
                        type: string
                    PROV_TAX_ID:
                        type: string
                    ICD_CDS:
                        type: array
                        items:
                            type: string
                    SBSCRBR_CERTFN_1_NBR:
                        type: string
                    SBSCRBR_CERTFN_2_NBR:
                        type: string
                    SBSCRBR_CERTFN_3_NBR:
                        type: string
                    CURNT_QUE_UNIT_NBR:
                        type: string
                    MDCL_RCRD_2_NBR:
                        type: string
                    MRN_NBR:
                        type: string
                    GRP_NBR:
                        type: string
                    MEDCR_STTS_IND:
                        type: string
                    MBR_PROD_CLS_CD:
                        type: string
                    CLM_PAYMNT_PRCS_CD:
                        type: string
                    claimDetails:
                        type: array
                        items:
                        type: object
                        required:
                            - DTL_LINE_NBR
                            - PROC_CD
                            - BILLD_CHRGD_AMT
                            - POT_CD
                            - BNFT_YEAR_CNTRCT_EFCTV_DT
                            - BNFT_YEAR_CNTRCT_REV_DT
                            - MBR_CNTRCT_EFCTV_DT
                            - MBR_CNTRCT_END_DT
                            - SRVC_FROM_DT
                            - SRVC_TO_DT
                            - MEDCRB_APRVD_AMT
                            - MEDCRB_COINSRN_AMT
                            - MEDCRB_DDCTBL_AMT
                            - MEDCRB_PAID_AMT
                            - MBR_CNTRCT_CD
                            - CURNT_QUE_UNIT_NBR
                        properties:
                            MBR_CNTRCT_TYPE_CD:
                                type: string
                            POT_CD:
                                type: string
                            UM_RQRD_IND:
                                type: string
                            DTL_LINE_NBR:
                                type: string
                            HCFA_PT_CD:
                                type: string
                            MDFR_1_CD:
                                type: string
                            MDFR_2_CD:
                                type: string
                            MDFR_3_CD:
                                type: string
                            TRTMNT_TYPE_CD:
                                type: string
                            UM_PROV_SQNC_NBR:
                                type: string
                            FNL_PROC_SRVC_CLS_1_CD:
                                type: string
                            MBR_CNTRCT_CVRG_CD:
                                type: string
                            PROC_SRVC_CLS_CD:
                                type: array
                                items:
                                    type: string
                            BNFT_YEAR_CNTRCT_EFCTV_DT:
                                type: string
                            BNFT_YEAR_CNTRCT_REV_DT:
                                type: string
                            CLM_CMPLTN_DT:
                                type: string
                            MBR_CNTRCT_EFCTV_DT:
                                type: string
                            MBR_CNTRCT_END_DT:
                                type: string
                            SRVC_FROM_DT:
                                type: string
                            SRVC_TO_DT:
                                type: string
                            BILLD_CHRGD_AMT:
                                type: number
                                format: double
                            BSIC_PAYMNT_AMT:
                                type: number
                                format: double
                            MEDCRB_APRVD_AMT:
                                type: number
                                format: double
                            MEDCRB_COINSRN_AMT:
                                type: number
                                format: double
                            MEDCRB_DDCTBL_AMT:
                                type: number
                                format: double
                            MEDCRB_PAID_AMT:
                                type: number
                                format: double
                            UNITS_OCR_NBR:
                                type: number
                                format: double
                            BILLD_SRVC_UNIT_QTY:
                                type: number
                                format: int32
                            KEY_CHK_DCN_NBR:
                                type: string
                            MBR_CNTRCT_CD:
                                type: string
                            PN_CD:
                                type: string
                            HCPCS_CD:
                                type: string
                            PROC_CD:
                                type: string
                            MEDCR_PROC_CD:
                                type: string
                            TOS_TYPE_CD:
                                type: string
                            UM_CASE_NBR:
                                type: string
                            PROC_MDFR_CD:
                                type: string
                            HCPCS_MDFR_CD:
                                type: string
                            ICD_9_1_CD:
                                type: string
                    nationalSegment1:
                        type: object
                        properties:
                            TAX_LIC_SPLTYCDE:
                                type: string
                    nationalSegment2:
                        type: object
                        properties:
                            TYPE_OF_BILL_CD:
                                type: string
                            BILLG_NPI:
                                type: string
                    nationalSegment3:
                        type: object
                        properties:
                            CLM_PATH_IND:
                                type: string
                            CLM_FILE_COB_IND:
                                type: string
                            BILLG_TXNMY_CD:
                                type: string
                            RNDRG_TXNMY_CD:
                                type: string
                            RNDRG_TAX_ID:
                                type: string  
        responses:
            200:
                description: SUCCESS
                headers:
                    meta-sessionid:
                        type: string
                    meta-transid:
                        type: string
                    KEY_CHK_DCN_NBR:
                        type: string
                    KEY_CHK_DCN_ITEM_CD:
                        type: string
                    KEY_CHK_DCN_CENTRY_CD:
                        type: string
                    KEY_ADJ_SEQ_NBR:
                        type: string
                schema:
                    type: object
                    required:
                        - respCd
                        - respDesc
                        - KEY_CHK_DCN_NBR
                        - KEY_CHK_DCN_ITEM_CD
                    properties:
                        respCd:
                            type: integer
                            format: int32
                        respDesc:
                            type: string
                        messages:
                            type: array
                            items:
                                type: object
                                properties:
                                    msgCd:
                                        type: integer
                                        format: int32
                                    msgDesc:
                                        type: string
                                    modelName:
                                        type: string
                        KEY_CHK_DCN_NBR:
                            type: string
                        KEY_CHK_DCN_ITEM_CD:
                            type: string
                        KEY_CHK_DCN_CENTRY_CD:
                            type: string
                        KEY_ADJ_SEQ_NBR:
                            type: string
                        recommendations:
                            type: array
                            items:
                                type: object
                                properties:
                                    modelName:
                                        type: string
                                    modelScore:
                                        type: number
                                        format: double
                                    actionCode:
                                        type: string
                                    actionValue:
                                        type: string
                                    lineNumber:
                                        type: string
                                    description:
                                        type: string
                        modelInsights:
                            type: array
                            items:
                                type: object
                                properties:
                                    modelName:
                                        type: string
                                    modelInsight:
                                        type: string


            500:
                description: Errors
    """
    api_start_time = datetime.datetime.now().timestamp()
    resp = {}
    reqid = request.headers.get('meta-transid', str(uuid.uuid1()))
    extra = {"tags": {"reqid": reqid}}
    logger = get_logger()
    try:
        # We have at 4MB limit app level : app.config.update{MAX_CONTENT_LENGTH = 4*1024*1024},TODO externalize this as constant
        # Below code is at service level to validate the length
        # if request.content_length is not None and request.content_length > 4 * 1024 * 1024:
        # Don't log payload as its very big
        # raise Exception('ERROR : GETTING CONTENT MORE THEN 16 MB')

        # print(str(app.default_config))
        # force : true is skip to force the content-type to application/json
        # cache : false is to skip caching the payload as we are not calling request.get_json again and again

        payload_raw = request.data
        payload = request.get_json(silent=True, cache=False, force=True)
        if payload is None:
            # print("Wrong json format : "+ str(payload_raw))
            logger.error("Failed to decode JSON object : {}".format(payload_raw.decode("utf-8")), extra=extra)
            raise Exception(" WRONG JSON FORMAT : Failed to decode JSON object ")

        logger.debug('Input', extra=util.extra_tags(extra, payload, flag=1))
        ctrl = FlowController(app, logger, extra)
        resp = ctrl.execute(payload, model=model)
        logger.info('Output', extra=util.extra_tags(extra, resp, flag=1))

    except:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),
                     extra=extra)
        resp['error'] = str(sys.exc_info()[1]).replace('\n', ' ')
    finally:
        response = make_response(json.dumps(resp), 200)
        response.headers['meta-transid'] = reqid
        response.headers['respCd'] = resp.get('respCd', 500)
        response.headers['respDesc'] = resp.get('respDesc', '')
        response.headers['KEY_CHK_DCN_NBR'] = resp.get('KEY_CHK_DCN_NBR', '')
        response.headers['KEY_CHK_DCN_CENTRY_CD'] = resp.get('KEY_CHK_DCN_CENTRY_CD', '')
        response.headers['KEY_CHK_DCN_ITEM_CD'] = resp.get('KEY_CHK_DCN_ITEM_CD', '')
        response.headers['KEY_ADJ_SEQ_NBR'] = resp.get('KEY_ADJ_SEQ_NBR', '')
        response.mimetype = "application/json"
        logger.debug('/processClaim {}'.format(str((datetime.datetime.now().timestamp() - api_start_time) * 1000)),
                     extra=extra)
        return response


@app.route('/processClaim/<model>', methods=['POST'])
def processClaimModel(model):
    return processClaim(model)

##CODE duplicacy :
@app.route('/processClaim/assemble',methods=['POST'])
def processClaim_Assemble():
    """
            Post request for Cognitive Claims response assembling
            ---
            tags:
                - Cognitive Claims

            summary: 'processClaim/assemble'
            consumes:
                - application/json
            produces:
                - application/json
            parameters:
                - name: meta-senderapp
                  in: header
                  required: true
                  type: string
                  description: 'Identify the client making the call'
                - name: meta-transid
                  in: header
                  required: true
                  type: string
                  description: 'Identify single transaction in from a session'
                - name: meta-sessionid
                  in: header
                  type: string
                  required: true
                  description: 'session id used by the client to trace the route'
                - name: processClaim/assemble
                  in: body
                  required: true
                  description: Array of JSON object input for request
                  schema:
                    type: array
                    items:
                        properties:
                            respCd:
                                type: integer
                                format: int32
                            respDesc:
                                type: string
                            messages:
                                type: array
                                items:
                                    type: object
                                    properties:
                                        msgCd:
                                            type: number
                                            format: int32
                                        msgDesc:
                                            type: string
                                        modelName:
                                            type: string
                            KEY_CHK_DCN_NBR:
                                type: string
                            KEY_CHK_DCN_ITEM_CD:
                                type: string
                            KEY_CHK_DCN_CENTRY_CD:
                                type: string
                            KEY_ADJ_SEQ_NBR:
                                type: string
                            recommendations:
                                type: array
                                items:
                                    type: object
                                    properties:
                                        modelName:
                                            type: string
                                        modelScore:
                                            type: number
                                            format: double
                                        actionCode:
                                            type: string
                                        actionValue:
                                            type: string
                                        lineNumber:
                                            type: string
                                        description:
                                            type: string
                            modelInsights:
                                type: array
                                items:
                                    type: object
                                    properties:
                                        modelName:
                                            type: string
                                        modelInsight:
                                            type: string

            responses:
                200:
                    description: SUCCESS
                    headers:
                        meta-sessionid:
                            type: string
                        meta-transid:
                            type: string
                        KEY_CHK_DCN_NBR:
                            type: string
                        KEY_CHK_DCN_ITEM_CD:
                            type: string
                        KEY_CHK_DCN_CENTRY_CD:
                            type: string
                        KEY_ADJ_SEQ_NBR:
                            type: string
                    schema:
                        type: object
                        required:
                            - respCd
                            - respDesc
                            - KEY_CHK_DCN_NBR
                            - KEY_CHK_DCN_ITEM_CD
                        properties:
                            respCd:
                                type: integer
                                format: int32
                            respDesc:
                                type: string
                            messages:
                                type: array
                                items:
                                    type: object
                                    properties:
                                        msgCd:
                                            type: number
                                            format: int32
                                        msgDesc:
                                            type: string
                                        modelName:
                                            type: string
                            KEY_CHK_DCN_NBR:
                                type: string
                            KEY_CHK_DCN_ITEM_CD:
                                type: string
                            KEY_CHK_DCN_CENTRY_CD:
                                type: string
                            KEY_ADJ_SEQ_NBR:
                                type: string
                            recommendations:
                                type: array
                                items:
                                    type: object
                                    properties:
                                        modelName:
                                            type: string
                                        modelScore:
                                            type: number
                                            format: double
                                        actionCode:
                                            type: string
                                        actionValue:
                                            type: string
                                        lineNumber:
                                            type: string
                                        description:
                                            type: string
                            modelInsights:
                                type: array
                                items:
                                    type: object
                                    properties:
                                        modelName:
                                            type: string
                                        modelInsight:
                                            type: string


                500:
                    description: Errors

        """


    api_start_time = datetime.datetime.now().timestamp()
    resp = {}
    reqid = request.headers.get('meta-transid', str(uuid.uuid1()))
    extra = {"tags": {"reqid": reqid}}
    logger = get_logger()
    try:
        payload_raw = request.data
        payload = request.get_json(silent=True, cache=False, force=True)
        if payload is None:
            logger.error("Failed to decode JSON object : {}".format(payload_raw.decode("utf-8")), extra=extra)
            raise Exception(" WRONG JSON FORMAT : Failed to decode JSON object ")
        logger.debug('Input', extra=util.extra_tags(extra, payload, flag=1))
        input = {}

        key_chk_dcn_nbr = key_chk_dcn_centry_cd = key_chk_dcn_item_cd = key_adj_seq_nbr = None
        for item in payload:
            model_name = None

            try:
                if key_chk_dcn_nbr == None:
                    key_chk_dcn_nbr = item.get('KEY_CHK_DCN_NBR',None)
                    key_chk_dcn_centry_cd = item.get('KEY_CHK_DCN_CENTRY_CD', None)
                    key_chk_dcn_item_cd = item.get('KEY_CHK_DCN_ITEM_CD', None)
                    key_adj_seq_nbr = item.get('KEY_ADJ_SEQ_NBR', None)
                else:
                    if key_chk_dcn_nbr != item.get('KEY_CHK_DCN_NBR',None) or key_chk_dcn_centry_cd != item.get('KEY_CHK_DCN_CENTRY_CD',None) or key_chk_dcn_item_cd != item.get('KEY_CHK_DCN_ITEM_CD',None) or key_adj_seq_nbr != item.get('KEY_ADJ_SEQ_NBR',None):
                        resp['error'] = "KEY's are not same in all the json objects"
                        logger.error('KEYS are not matching in json objects : '+str(payload), extra=util.extra_tags(extra, item, flag=1))
                        return
                messages = item.get('messages',None)
                model_name = item.get('messages',None)[0].get('modelName',None)
                for mes in messages:
                    if mes.get('msgCd') == 700:
                        model_name = mes.get('modelName')

                if model_name is not None and model_name != '':
                    input[model_name] = item
            except:
                logger.info('skip item as dosnt have correct messges block in payload ', extra=util.extra_tags(extra, item, flag=1))

        ctrl = FlowController(app, logger, extra)
        resp = ctrl.resp_assembler(input)
        if resp == None:
            resp = {}
        logger.info('Output', extra=util.extra_tags(extra, resp, flag=1))

    except:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=extra)
        resp['error'] = str(sys.exc_info()[1]).replace('\n', ' ')

    finally:
        response = make_response(json.dumps(resp), 200)
        response.headers['meta-transid'] = reqid
        response.headers['respCd'] = resp.get('respCd', 500)
        response.headers['respDesc'] = resp.get('respDesc', '')
        response.headers['KEY_CHK_DCN_NBR'] = resp.get('KEY_CHK_DCN_NBR', '')
        response.headers['KEY_CHK_DCN_CENTRY_CD'] = resp.get('KEY_CHK_DCN_CENTRY_CD', '')
        response.headers['KEY_CHK_DCN_ITEM_CD'] = resp.get('KEY_CHK_DCN_ITEM_CD', '')
        response.headers['KEY_ADJ_SEQ_NBR'] = resp.get('KEY_ADJ_SEQ_NBR', '')
        response.mimetype = "application/json"
        logger.debug('/processClaim_assemble {}'.format(str((datetime.datetime.now().timestamp() - api_start_time) * 1000)),
                     extra=extra)
        return response

@app.route('/search', methods=['GET'])
def getData():
    logger = get_logger()
    key = request.args.get('key')
    table = request.args.get('table')
    logger.debug(key)
    logger.debug(table)
    res = 'Something wrong with HBase lookup'
    try:
        res = hbase.hbaseLookup(key, table, logger)
    except Exception as e:
        logger.error(e)
    return res


if __name__ == '__main__':
    try:
        # before_first_request()
        HOSTNAME = 'localhost'
        PORT = 9080
        app.run(host=HOSTNAME, port=PORT, debug=False)
    except:
        logger = get_logger()
        logger.error('Unable to initiate the app {}'.format(str(sys.exc_info()[1]).replace('\n', ' ')))
