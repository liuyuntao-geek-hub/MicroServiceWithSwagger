import logging
import logging.config

logging.config.fileConfig(fname='file.conf', disable_existing_loggers=False)

# Get the logger specified in the file
# root logger can be used on everything
logger = logging.getLogger(__name__)
logger = logging.getLogger('sLogger')
logger.debug('This is a debug message')
logger.warning('This is a debug message')




# log something
logger.debug('debug message')
logger.info('info message')
logger.error('error message')
logger.critical('critical message')

# give error tracking of logger
a = 5
b = 0

try:
  c = a / b
except Exception as e:
    logger.error("Exception occurred" , exc_info=True)

a = 5
b = 0
try:
  c = a / b
except Exception as e:
    logger.exception("Exception occurred")