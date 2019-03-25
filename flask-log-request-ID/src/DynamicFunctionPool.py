import logging
import logging.config
from random import randint
from flask import Flask
from flask_log_request_id import RequestID, RequestIDLogFilter, current_request_id
import socket

def dynamic_generic_add(a, b):
    """Simple function to add two numbers that is not aware of the request id"""
    logging.warning('Called generic_add({}, {})'.format(a, b))
    logging.warning(current_request_id())
    return a + b

