package zephyr.example.jaridbd;

import java.util.Random;

import zephyr.plugin.core.api.ZephyrRunnable;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class Experiment implements ZephyrRunnable {
  final static private int numTotalInputs = 20;
  final static private int numRelevantInputs = 5;

  final private Clock clock = new Clock();
  final private Problem problem = new Problem(new Random(0), numTotalInputs, numRelevantInputs);
  final private LMS lms = new LMS(numTotalInputs, .1 / numTotalInputs);
  final private IDBD idbd = new IDBD(numTotalInputs, 0.01, .1 / numTotalInputs);

  @LabelProvider(ids = { "weights" })
  protected String labelOf(int i) {
    return i < numRelevantInputs ? "Relevant" : "Irrelevant";
  }

  @Override
  public void run() {
    while (!clock.isTerminated()) {
      clock.tick();
      double[] inputs = problem.computeInputs();
      double target = problem.computeTarget(inputs);
      lms.update(inputs, target);
      idbd.update(inputs, target);
      problem.updateWeights();
    }
  }

  @Override
  public Clock clock() {
    return clock;
  }

  public static void main(String[] args) {
    new Experiment().run();
  }
}
