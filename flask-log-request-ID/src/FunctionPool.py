import logging
import logging.config
from random import randint
from flask import Flask
from flask_log_request_id import RequestID, RequestIDLogFilter, current_request_id
import socket
from DynamicFunctionPool import dynamic_generic_add

counter=0
def counterCount():
    global counter
    counter=counter+1
    logging.warning('Global counter is: {}'.format(counter))
    return counter

def generic_add(a, b):

    return dynamic_generic_add(a, b)