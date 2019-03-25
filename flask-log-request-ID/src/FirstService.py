import logging
import logging.config
from random import randint
from flask import Flask, request, jsonify
from flask_log_request_id import RequestID, RequestIDLogFilter, current_request_id
import socket
from FunctionPool import counterCount, generic_add
from flasgger import Swagger
from pythonjsonlogger import jsonlogger

app = Flask(__name__)
RequestID(app)

app.config['SWAGGER'] = {
    'swagger_version': '2.0',
    'specs': [
        {
            'version': '0.01',
            'title': 'Add Random Numbers',
            'description': 'Builds a API To add two Random Number',
            'endpoint': 'Add Random Number',
            'route': '/swagger.json'
            # http://USDENYUNTLIU1:9080/apidocs => give swagger definition
            #
        }
    ]
}

Swagger(app)


# Setup logging
handler = logging.StreamHandler()
handler.setFormatter(
#logging.Formatter("%(asctime)s - %(name)s - level=%(levelname)s - request_id=%(request_id)s - %(message)s")
jsonlogger.JsonFormatter("%(asctime)s - %(name)s - filename=%(filename)s level=%(levelname)s - request_id=%(request_id)s - %(message)s")
)
handler.addFilter(RequestIDLogFilter())  # << Add request id contextual filter
logging.getLogger().addHandler(handler)


# The following annotation is needed = This will build the swagger page
@app.route('/addrandom', methods = ['GET'])
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

    a, b = randint(1, 15), randint(1, 15)
    logging.warning('Adding two random numbers {} {}'.format(a, b))

    return jsonify({'Random Addtion': generic_add(a, b), 'Request Count':counterCount() })
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
    PORT = 9080
    app.run(host=HOSTNAME, port=PORT, debug=True)