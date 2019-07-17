import multiprocessing

from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.jobstores.sqlalchemy import SQLAlchemyJobStore

import os
import sys
import glob

PATH_1 = os.path.dirname(os.path.abspath(__file__))
print(PATH_1)
sys.path.append(PATH_1)
sys.path.append(PATH_1 + "/../src-lib/")

# from scheduler import cleanup, refresh
from scheduler import cleanup, cleanup_workers



#workers = multiprocessing.cpu_count() * 2 + 1
workers = 2
#bind = 'dwbdtest1r5e1.wellpoint.com:45380'
bind = '127.0.0.1:9080'
proc_name = 'gunicorn_cogx'
pidfile = '/tmp/gunicorn_cogx.pid'
logfile = '/tmp/gunicorn.log'
worker_class = 'sync'
timeout = 3600
debug = False
max_requests = 0
preload_app = True


def when_ready(server):
    
    print('Server started')
    sqlite = 'sqlite:///{}.sqlite'.format(os.getpid())
    backup_files = 5
    cleanup(backup_files)
    rerun_monitor = BackgroundScheduler(jobstores={'default': SQLAlchemyJobStore(url=sqlite)})
    job = rerun_monitor.add_job(cleanup, args=(str(backup_files)), trigger='cron', hour='00', minute='02', second='00', id='cleanup', replace_existing=True)
    # job = rerun_monitor.add_job(cleanup, args=('0'), trigger='cron', minute='*', second='5', id='cleanup', replace_existing=True)
    rerun_monitor.start()
    pass


def on_exit(server):
    print('exiting server')
    files = glob.glob('*.sqlite')
    for file in files:
        os.remove(file)
    pass


def post_fork(server, worker):
    pass


def nworkers_changed(server, new_value, old_value):
    pass



def post_worker_init(worker): 
    print('worker initialized')
    # print(os.getpid())
    sqlite = 'sqlite:///{}.sqlite'.format(os.getpid())
    rerun_monitor = BackgroundScheduler(jobstores={'default': SQLAlchemyJobStore(url=sqlite)})
    job = rerun_monitor.add_job(cleanup_workers,  trigger='cron', hour='0', minute='0', second='30', id='cleanup_workers', replace_existing=True, jitter=30)
    # job = rerun_monitor.add_job(cleanup_workers,  trigger='cron', minute='*', id='cleanup_workers', replace_existing=True)
    rerun_monitor.start()
    pass
