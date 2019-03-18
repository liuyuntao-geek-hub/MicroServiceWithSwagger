import logging
logging.basicConfig(filename='app.log',level=logging.DEBUG, filemode='w',
                    #format='%(name)s - %(levelname)s - %(message)s')
                    format='%(asctime)s - %(message)s')
                    #format='%(process)d-%(levelname)s-%(message)s')
logging.basicConfig(level=logging.WARNING)
# basicCOnfig has to be right after import logging
# all other logging function will call the logging handler => will invoke the default log handler
# then, the basicCOnfig will not work since the root handler already created

logging.warning('This will get logged to a file')
# This will also invoke the Root logger handler => it will make the new basicConfig stop working
# https://docs.python.org/3/library/logging.html#logging.basicConfig
# The above has all the default variables


logging.debug('This is a debug message')
logging.info('This is an info message')
logging.warning('This is a warning message')
logging.error('This is an error message')
logging.critical('This is a critical message')

logging.critical('default logger level is Warning')

### change the default logger level
logging.basicConfig(level=logging.DEBUG)
logging.debug('This will get logged')

message = 'something'
logging.debug('%s is the message to be displayed', message)

# format the logging message
logging.debug(f'{message} is displayed as f-string')
print("something")

# give error tracking of logger
a = 5
b = 0

try:
  c = a / b
except Exception as e:
  logging.error("Exception occurred" , exc_info=True)

a = 5
b = 0
try:
  c = a / b
except Exception as e:
  logging.exception("Exception occurred")
