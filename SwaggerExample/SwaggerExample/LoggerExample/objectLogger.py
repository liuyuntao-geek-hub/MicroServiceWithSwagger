import logging

logger = logging.getLogger(__name__)
logger.warning('this is a warning')

# logging to console
c_handler = logging.StreamHandler()
c_handler.setLevel(logging.WARNING)
c_format = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
c_handler.setFormatter(c_format)
logger.addHandler(c_handler)

# logging to file
f_handler = logging.FileHandler('file.log','a')
f_handler.setLevel(logging.WARNING)
f_format = logging.Formatter('%(asctime)s | %(name)s | %(levelname)s | %(message)s')
f_handler.setFormatter(f_format)
logger.addHandler(f_handler)

logger.warning('This is a warning')
logger.error('This is a error')


