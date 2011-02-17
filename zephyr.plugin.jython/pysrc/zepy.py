from zephyr.plugin.core.api import Zephyr
from zephyr import ZephyrPlotting
from zephyr.plugin.core.api.synchronization import Clock

def _findclock(clock, obj):
    if clock is not None:
        return clock
    return obj.clock() if callable(obj.clock) else obj.clock

def advertise(obj, clock = None):
    Zephyr.advertise(_findclock(clock, obj), obj)
    
def monfunc(clock, func, level = 0, name = None):
    logger = ZephyrPlotting.createLogger(clock)
    logger.add(func.__name__ if name is None else name, 
               (lambda steptime: float(func())), level)
    
def monattr(obj, name, level = 0, clock = None, label = None):
    logger = ZephyrPlotting.createLogger(_findclock(clock, obj))
    attr = getattr(obj, name)
    monitored = ((lambda steptime: float(attr())) if callable(attr) 
                 else (lambda steptime: float(getattr(obj, name))))
    logger.add(name if label is None else label, monitored, level)
