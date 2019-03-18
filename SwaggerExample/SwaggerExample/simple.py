from flask import Flask, request, jsonify
from flask_restplus import Resource, Api

import logging.config
logging.config.fileConfig(fname='SwaggerExample/ExternalLogger/file.conf', disable_existing_loggers=False)
logger = logging.getLogger('sLogger')



app = Flask(__name__)                  #  Create a Flask WSGI application
api = Api(app)                         #  Create a Flask-RESTPlus API

tasks = [
    {
        'id': 1,
        'title': u'Shopping book',
        'description': u'shop a book for cooking with Milk, Cheese, Pizza, Fruit, Oil',
        'done': False
    },
    {
        'id': 2,
        'title': u'Reading book after purchase',
        'description': u'Find a book I like and Read',
        'done': False
    }]

@api.route('/simpleGet')                   #  Create a URL route to this resource
class HelloWorld(Resource):            #  Create a RESTful resource
    def get(self):    #  Create GET endpoint
        myName = 'Yuntao'
        logger.info('| Sender = %s | RequestType = %s | Request = %s | Response = %s' % (
        myName, 'GET', 'GetRequest', {'tasks': tasks}))
        print(" program name is %s",__name__)
        return {'hello': 'world', 'name':myName, 'errors': {'per_page': 'results not found'}}

@api.route('/simplePost/<string:senderName>')
class HelloWorldPost(Resource):            #  Create a RESTful resource
    def post(self, senderName):
        message = jsonify({'tasks': tasks})
        thisRequest = request.json
        logger.info('| Sender = %s | RequestType = %s | Request = %s | Response = %s' % ( senderName, 'POST', thisRequest,{'tasks':tasks}) )
        return jsonify({'tasks':tasks})


class TestingFunc:
    def __init__(self,x,y):
        self.x = x
        self.y = y
        self.multi=0
    def getMultiple(self):
        self.multi = self.x * self.y
        return self.multi



if __name__ == '__main__':
    app.run(debug=True)                #  Start a development server