import datetime
import os
import logging
from json_log import JSONFormatter


def get_logger():
    log_filename = 'cogx-api-{}.log'
    curr_date = datetime.datetime.now().strftime("%Y-%m-%d")
    logger = logging.getLogger('logfile' + curr_date)
    if logger.handlers is None or len(logger.handlers) == 0:
        logger.setLevel(logging.DEBUG)
        path, file = os.path.split(os.environ['COGX_LOGS'])
        handler = logging.FileHandler(path + os.path.sep + log_filename.format(curr_date))
        handler.setFormatter(JSONFormatter())
        logger.addHandler(handler)
    return logger

        
'''
def get_logger():
    log_filename = 'log-{}.out'
    curr_date = datetime.datetime.now().strftime("%Y-%m-%d-%H-%M")
    print(datetime.datetime.now().strftime("%Y-%m-%d-%H-%M-%S") + ' : ' + str(os.getpid()))
    logger = logging.getLogger('logfile')
    # stat = os.stat(os.environ['COGX_LOGS'])
    # print(stat)
    # accessed_date = datetime.datetime.fromtimestamp(stat.st_atime).strftime("%Y-%m-%d-%H-%M")
    # print(accessed_date)
    created_date = None
    if len(logger.handlers) > 0:
        created_date = logger.handlers[0].rename_to
        print(created_date)
    if (created_date == curr_date ):
        print('File exists')
        return logger
    else:
        path, file = os.path.split(os.environ['COGX_LOGS'])
        # renaming to current time stamp
        path = path + os.path.sep + log_filename.format(curr_date)
        if os.path.exists(path) == False:
            # other process have not created the file
            if os.path.exists(os.environ['COGX_LOGS']):
                os.rename(os.environ['COGX_LOGS'], path)
                print('File will be created')
        else:
            print('File will be reused')
        logger.handlers = []
        
        # path, file = os.path.split(os.environ['COGX_LOGS'])
        # path = path + os.path.sep + log_filename.format(curr_date)
        logger.setLevel(logging.DEBUG)
        try:
            handler = FileHandlerUpdated(curr_date, os.environ['COGX_LOGS'])
            handler.setFormatter(JSONFormatter())
            logger.addHandler(handler)
        except Exception as e:
            print(e)
            return e
        return logger
'''