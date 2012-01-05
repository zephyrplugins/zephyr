import math
import zepy

class HelloZephyr(object):
    def __init__(self):
        self.clock = zepy.Clock("HelloZephyr")
        self.value = 0 
        zepy.monattr(self.clock, self, "value")
        zepy.monattr(self.clock, self, "someValue")
        
    def someValue(self):
        return math.cos(self.value)
        
    def run(self):
        while not self.clock.isTerminated():
            self.value += 1
            self.clock.tick()

if __name__ == '__main__':
    HelloZephyr().run()
    
