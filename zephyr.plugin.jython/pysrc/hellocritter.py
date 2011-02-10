from critterbot.actions import VoltageSpaceAction
from critterbot.actions import XYThetaAction
from critterbot.environment import CritterbotDrops
from critterbot.environment import CritterbotSimulator
from zephyr.plugin.core.api import Zephyr

if __name__ == '__main__':
    environment = CritterbotSimulator()
    Zephyr.advertise(environment)
    legend = environment.legend()
    while not environment.isClosed():
      obs = environment.waitNewObs()
      if obs[legend.indexOf(CritterbotDrops.IRDistance + "0")] > 128:
        action = XYThetaAction(10, -10, 10)
      else:
        action = VoltageSpaceAction(10, -10, 10)
      environment.sendAction(action);

