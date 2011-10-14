import zepy

class SimpleClient(object):
    def __init__(self):
        self.monitoredAttribute = 0
    
    def run(self):
        clock = zepy.clock()
        zepy.monattr(clock, self, 'monitoredAttribute')
        zepy.monfunc(clock, (lambda: -self.monitoredAttribute), name="Function")
        while clock.tick():
            self.monitoredAttribute = (self.monitoredAttribute + 1) % 10

if __name__ == "__main__":
    SimpleClient().run()