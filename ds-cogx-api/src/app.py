import constants as const
from flask import jsonify, request, make_response, Flask
import socket
from flasgger import Swagger
import logging
import logging.config
import datetime
import traceback
import sys
import os
import requests
import uuid
import copy
import json
PATH = os.path.dirname(os.path.abspath(__file__))
APP_PATH = os.path.split(PATH)[0]
sys.path.append(APP_PATH+"/src-lib/")
from measure import measure_time_with_path, measure_time_old


logging.config.fileConfig(const.LOGGING_CONFIG)
logger = logging.getLogger('logfile')

from cache import usecases, model_encodings, mappings, validations
app = Flask(__name__)
app.config['usecases'] = usecases
app.config['model_encodings'] = model_encodings
app.config['mappings'] = mappings
app.config['validations'] = validations
import computeLTR as ltr
from computeUM import computeUM


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
@measure_time_with_path('/processClaim')
def processClaim():
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
                            limitClass:
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
                            PROC_SRVC_CLS_1_CD:
                                type: string
                            PROC_SRVC_CLS_2_CD:
                                type: string
                            PROC_SRVC_CLS_3_CD:
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
                            TOS_TYPE_CD:
                                type: string
                            UM_CASE_NBR:
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
                schema:
                    type: object
                    required:
                        - resCd
                        - resDesc
                        - KEY_CHK_DCN_NBR
                        - KEY_CHK_DCN_ITEM_CD
                        - recommendationsPresent
                    properties:
                        resCd:
                            type: string
                        resDesc:
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
                        KEY_CHK_DCN_NBR:
                            type: string
                        KEY_CHK_DCN_ITEM_CD:
                            type: string
                        sequenceNumber:
                            type: number
                            format: int32
                        recommendationsPresent:
                            type: boolean
                        recommendations:
                            type: array
                            items:
                                type: object
                                properties:
                                    modelName:
                                        type: string
                                    actionCode:
                                        type: string
                                    actionValue:
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
    resp = {}
    reqid = request.headers.get('meta-transid', str(uuid.uuid1()))
    # extra={"tags": ["hello=world"]})
    extra={"tags": {"reqid": reqid}}
    # extra={"reqid": reqid}
    try:         
        payload = request.get_json(silent=True)
        # extra['payload'] = payload
        extra1 = copy.deepcopy(extra)
        extra1.update({'payload': payload})
        logger.debug('input', extra=extra1)
        new_um = computeUM(app, logger, extra)
        resp = new_um.process_request(payload)
            
            
    except:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '), extra=extra)
        resp['error'] = str(sys.exc_info()[1]).replace('\n', ' ')
    finally:
        response =  make_response(json.dumps(resp), 200)
        response.mimetype = "application/json"
        return response



# TODO this will be deleted
def process_ltr():
    """
        Post request for Cognitive Claims processing
        ---
        tags:
            - Cognitive Claims

        summary: 'processClaim'
        consumes:
            - application/json
        produces:
            - text/html
        parameters:
            - name: Claim_input
              in: body
              required: true
              description: JSON input for request
              schema:
                type: object
                properties:
                    header:
                        type: object
                    details:
                        type: "array"
                        items:
                            type: object
                    edits:
                        type: "array"
                        items:
                            type: object
        responses:
            200:
                description: Predictions
            500:
                description: Errors
    """
    resp = {}
    try:         
        payload = request.get_json(silent=True)
        logger.debug(payload)
        #TODO : Add error handling while payload is None like when data is not in valid json format
        resp = ltr.process_request(payload)
    except:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
        resp['error'] = str(sys.exc_info()[1]).replace('\n', ' ')
    finally:
        return make_response(str(resp), 200)


# TODO this will be deleted
def process_um():
    """
        Post request for Cognitive Claims processing
        ---
        tags:
            - Cognitive Claims

        summary: 'processClaim'
        consumes:
            - application/json
        produces:
            - text/html
        parameters:
            - name: Claim_input
              in: body
              required: true
              description: JSON input for request
              schema:
                type: object
                properties:
                    um_claim:
                        type: object
        responses:
            200:
                description: Predictions
            500:
                description: Errors
    """
    resp = {}
    reqid = request.headers.get('meta-transid', str(uuid.uuid1()))
    # extra={"tags": ["hello=world"]})
    extra={"tags": {"reqid": reqid}}
    # extra={"reqid": reqid}
    try:         
        payload = request.get_json(silent=True)
        # extra['payload'] = payload
        extra1 = copy.deepcopy(extra)
        extra1.update({'payload': payload})
        logger.debug('input', extra=extra1)
        new_um = computeUM(app, logger, extra)
        resp = new_um.process_request(payload)
                       
    except:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '), extra=extra)
        resp['error'] = str(sys.exc_info()[1]).replace('\n', ' ')
    finally:
        return make_response(str(resp), 200)


if __name__ == '__main__':
    HOSTNAME = socket.gethostname()
    if HOSTNAME == 'LC02W10KYHTDD.':
        HOSTNAME = 'localhost'
    print(HOSTNAME)
    PORT = 9080
    app.run(host=HOSTNAME, port=PORT, debug=True)