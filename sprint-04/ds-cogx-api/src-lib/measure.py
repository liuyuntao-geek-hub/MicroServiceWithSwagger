import functools
import logging
import copy
import time 

from rotate_logs import get_logger


def measure_time_method(func):
    logger = get_logger()
    @functools.wraps(func)
    def measure_time_elapsed(*args, **kwargs):
        has_tag = False
        if hasattr(args[0], 'extra'):
            has_tag = True
        api_start_time = time.time()
        result = func(*args, **kwargs)
        api_end_time = time.time()
        if has_tag:
            extra = copy.deepcopy(args[0].extra)
            extra.update({func.__qualname__.replace('.', '_'): (api_end_time -api_start_time)})
            extra.update({'respCd': result['respCd']})
            logger.debug('Elapsed time', extra=extra)
        else: 
            logger.debug('Elapsed time', extra={func.__qualname__: (api_end_time -api_start_time)})
        return result
    return measure_time_elapsed


def measure_time_with_path(*args, **kwargs):
    logger = get_logger()
    # print('printing arguments - ' + args[0])
    path1 = args[0]
    extra = args[1]
    print(extra)
    def measure_time(func):
        @functools.wraps(func)
        def measure_time_elapsed(*args, **kwargs):
            api_start_time = time.time()
            # print(api_start_time)
            result = func(*args, **kwargs)
            api_end_time = time.time()
            logger.debug(path1 + ' ' + str((api_end_time -api_start_time) ))
            return result
        return measure_time_elapsed
    return measure_time