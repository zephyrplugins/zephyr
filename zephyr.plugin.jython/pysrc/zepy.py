from zephyr.plugin.core.api import Zephyr
from zephyr import ZephyrPlotting
from zephyr.plugin.core.api.synchronization import Clock

def _findclock(clock, object):
    if clock is not None:
        return clock
    return object.clock() if callable(object.clock) else object.clock

def advertize(object, clock = None):
    Zephyr.advertise(_findclock(clock, object), object)
    
def monfunc(clock, func, level = 0, name = None):
    logger = ZephyrPlotting.createLogger(_findclock(clock, object))
    logger.add(func.__name__ if name is None else name, 
               (lambda steptime: float(func())), level)
    
def monattr(object, name, level = 0, clock = None):
    logger = ZephyrPlotting.createLogger(_findclock(clock, object))
    attr = getattr(object, name)
    monitored = ((lambda steptime: float(attr())) if callable(attr) 
                 else (lambda steptime: float(getattr(object, name))))
    logger.add(name, monitored, level)
