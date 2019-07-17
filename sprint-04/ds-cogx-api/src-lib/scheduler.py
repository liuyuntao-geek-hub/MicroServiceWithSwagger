import sys
import time
from threading import Thread
import yaml
from rotate_logs import get_logger
import os
import datetime
import glob
import gzip
import shutil
import subprocess
import logging

PATH = os.path.dirname(os.path.abspath(__file__))

'''
# @app.before_first_request
def before_first_request():
    logger = get_logger()

    class RefereshThread(Thread):
        def __init__(self, name="COGX_REFRESH_VAR_THREAD"):
            Thread.__init__(self)
            self.name = name
            self.setup()
            self.refresh_const()

        def setup(self):
            try:
                with open(PATH + '/../config/constant_ref.yml') as f:
                    c = yaml.load(f, Loader=yaml.FullLoader)
                    logger.info('Reload time mentioned in file: {}'.format(str(c.get('START_TIME'))))
                    self.end_time = datetime.datetime.strptime(c.get('START_TIME'), "%I:%M%p")
            except:
                logger.error('Error in setup(): {}'.format((str(sys.exc_info()[1]).replace('\n', ' '))))
            self.start_time = datetime.datetime.now().replace(year=1900, month=1, day=1, microsecond=0)
            self.duration = (self.end_time - self.start_time).total_seconds()

            if self.duration <= 0:
                self.end_time = self.end_time.replace(day=2, microsecond=0)
                self.duration = (self.end_time - self.start_time).total_seconds()
            logger.info('Current refresh time: {}'.format(str(self.start_time.time())))
            logger.info('Next load refresh time: {}'.format(str(self.end_time.time())))
            logger.info('Duration to next load: {} seconds'.format(str(self.duration)))

        def refresh_const(self):
            try:
                with open(PATH + '/../config/constant_ref.yml') as f:
                    c = yaml.load(f, Loader=yaml.FullLoader)
                    # TODO : Add iterative approach to get all the elements from property object and set to const
                    # TODO : use thread lock here to update value
                    constants.UM_THRESHOLD = c.get('UM_THRESHOLD')
                    logger.info('New threshold: {}'.format(str(constants.UM_THRESHOLD)))
                    logger.info('Latest load time: {}'.format(str(datetime.datetime.now().time())))
                    logger.info('Sleep duration: {}'.format(str(self.duration)))
                    self.setup()

            except:
                logger.error('Error in refresh_const(): {}'.format(str(sys.exc_info()[1]).replace('\n', ' ')))

        def run(self):
            while True:
                logger.info('Sleeping for {} seconds ...'.format(str(self.duration)))
                time.sleep(self.duration)
                logger.info('Reloading after sleeping for {}  ...'.format(str(self.duration)))
                self.duration = 24 * 60 * 60
                self.refresh_const()

    thread = RefereshThread('REFRESH_VAR_THREAD')
    thread.daemon = True
    thread.start()
    logger.info("Started Refresh LOAD THREAD ")
'''


def cleanup(backup_count):
    backup_count = int(backup_count)
    path, file = os.path.split(os.environ['COGX_LOGS'])
    log_filename = 'cogx-api-{}.log'
    files = glob.glob(path + os.path.sep + log_filename.format('*'))
    files.sort(key=os.path.getmtime)
    # print('\n'.join(files))
    if len(files) > (backup_count + 1):
        files = files[0: -(backup_count+1)]
        for file in files:
            with open(file, 'rb') as f_in:
                with gzip.open(file+'.gz', 'wb') as f_out:
                    shutil.copyfileobj(f_in, f_out)
            os.remove(file)
    
    log_filename = 'cogx-api-{}.gz'
    files = glob.glob(path + os.path.sep + log_filename.format('*'))
    files.sort(key=os.path.getmtime)
    if len(files) > (backup_count + 1):
        files = files[0: -(backup_count+1)]
        for file in files:
            os.remove(file)


def cleanup_workers():
    curr_date = datetime.datetime.now().strftime("%Y-%m-%d")
    loggers = [logging.getLogger(name) for name in logging.root.manager.loggerDict]
    logger = logging.getLogger('logfile' + curr_date)
    for log in loggers:
        if log.name.startswith('logfile') and logger.name != log.name:
            while len(log.handlers) > 0:
                handler = log.handlers.pop()
                handler.flush()
                handler.close()
            
'''
def refresh():
    try:
        with open(os.environ['COGX_REFRESH']) as f:
            c = yaml.load(f, Loader=yaml.FullLoader)
            constants.UM_THRESHOLD = c.get('UM_THRESHOLD')
    except Exception as e:
        print(e)



def check_and_reinit_kticket():
    k_ticket = has_kerberos_ticket()
    if k_ticket == True:
        pass
    else:
        reinit_kticket_func()


def reinit_kticket_func():
    user = 'srcccpcogxapidv'
    keytab = VarList.keytab
    command = "kinit srcccpcogxapidv -kt "+keytab
    subprocess.Popen(command, shell=True) # USE THIS LINE FOR OWN USER ID's
'''