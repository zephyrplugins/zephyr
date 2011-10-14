from zephyr.plugin.core.api import Zephyr
from zephyr.plugin.core.api.synchronization import Clock
from zephyr import ZephyrPlotting

def advertise(clock, obj):
    Zephyr.advertise(clock, obj)
    
def monfunc(clock, func, level = 0, name = None):
    logger = ZephyrPlotting.createMonitor(clock)
    logger.add(func.__name__ if name is None else name, level, 
               (lambda : float(func())))
    
def monattr(clock, obj, name, level = 0, label = None):
    logger = ZephyrPlotting.createMonitor(clock)
    attr = getattr(obj, name)
    monitored = ((lambda : float(attr())) if callable(attr) 
                 else (lambda : float(getattr(obj, name))))
    logger.add(name if label is None else label, level, monitored)

def clock(clockName = "NoName"):
    return Clock(clockName)