from zephyr.plugin.core.api import Zephyr
from zephyr.plugin.core.api.synchronization import Clock

#pylint: disable-msg=W0621

def advertise(clock, obj):
    Zephyr.advertise(clock, obj)
    
def monfunc(clock, func, level = 0, name = None):
    monitor = Zephyr.getSynchronizedMonitor(clock)
    monitor.add(func.__name__ if name is None else name, level, 
               (lambda : float(func())))
    
def monattr(clock, obj, name, level = 0, label = None):
    monitor = Zephyr.getSynchronizedMonitor(clock)
    attr = getattr(obj, name)
    monitored = ((lambda : float(attr())) if callable(attr) 
                 else (lambda : float(getattr(obj, name))))
    monitor.add(name if label is None else label, level, monitored)

def clock(clockName = "NoName"):
    return Clock(clockName)