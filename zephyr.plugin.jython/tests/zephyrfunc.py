import math
import zepy

if __name__ == '__main__':
    clock = zepy.Clock("zephyrfunc")
    value = 0
    zepy.monfunc(clock, (lambda : math.cos(value)), name = "cos")
    while not clock.isTerminated():
        value += 1
        clock.tick()
    
