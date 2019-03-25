import logging
import logging.config
from random import randint
from flask import Flask, request, jsonify
from flask_log_request_id import RequestID, RequestIDLogFilter, current_request_id
#from flask_log_request_id.extras.celery import enable_request_id_propagation
import socket
from flasgger import Swagger
import requests
from pythonjsonlogger import jsonlogger
#celery = Celery()
#enable_request_id_propagation(celery)  # << This step here is critical to propagate request-id to workers


app = Flask(__name__)
RequestID(app)

app.config['SWAGGER'] = {
    'swagger_version': '2.0',
    'specs': [
        {
            'version': '0.01',
            'title': ' Get Add Random Numbers - Client',
            'description': 'Builds a API To add two Random Number',
            'endpoint': 'Get Add Random Number and report',
            'route': '/swagger.json'
            # http://USDENYUNTLIU1:9081/apidocs => give swagger definition
            #
        }
    ]
}

Swagger(app)
# Setup logging
handler = logging.StreamHandler()
handler.setFormatter(
#logging.Formatter("%(asctime)s - %(name)s - level=%(levelname)s - request_id=%(request_id)s - %(message)s")
jsonlogger.JsonFormatter("%(asctime)s - %(name)s - level=%(levelname)s - request_id=%(request_id)s - %(message)s")
)
handler.addFilter(RequestIDLogFilter())  # << Add request id contextual filter
logging.getLogger().addHandler(handler)


def getAddRandom():
    url = "http://USDENYUNTLIU1:9080/addrandom"
    resp = requests.get(url)
    data = resp.json()
    source_request_ID = resp.headers.get('X-REQUEST-ID')
    logging.warning("source service request ID: {}".format(source_request_ID))
    print(data)
    return data

# The following annotation is needed = This will build the swagger page
@app.route('/getaddrandom', methods = ['GET'])
def index():
    """
    This is the Add Random Number API
    Call this api without passing any parameters
    ---
    tags:
      - Add Random Number
    responses:
      500:
        description: Error The language is not awesome!
      200:
        description: A language with its awesomeness
    """

    data =getAddRandom()
    data
    logging.warning('Adding two random numbers from service: {} '.format(data['Random Addtion']))

    return jsonify(data)
   # return str(generic_add(a, b))

@app.after_request
def append_request_id(response):
    response.headers.add('X-REQUEST-ID', current_request_id())
    return response

if __name__ == '__main__':
    HOSTNAME = socket.gethostname()
    if HOSTNAME == 'LC02W10KYHTDD.':
        HOSTNAME = 'localhost'
    print(HOSTNAME)
    PORT = 9081
    app.run(host=HOSTNAME, port=PORT, debug=True)

