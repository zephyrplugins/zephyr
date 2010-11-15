package zephyr.example.jaridbd;

import java.util.Random;

import zephyr.plugin.core.api.ZephyrRunnable;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

/**
 * The class is annotated with @Monitor meaning that Zephyr will explore all the
 * fields including problem, LMS and IDBD
 * 
 */
@Monitor
public class Experiment implements ZephyrRunnable {
  final static private int numTotalInputs = 20;
  final static private int numRelevantInputs = 5;

  /**
   * Instance used to synchronize the data with Zephyr
   */
  final private Clock clock = new Clock();
  final private Problem problem = new Problem(new Random(0), numTotalInputs, numRelevantInputs);
  final private LMS lms = new LMS(numTotalInputs, .1 / numTotalInputs);
  final private IDBD idbd = new IDBD(numTotalInputs, 0.01, .1 / numTotalInputs);

  /**
   * This method is annotated with @LabelProvider indicating to Zephyr that it
   * can use it to build labels for elements of array with a field name
   * "weights", including arrays in the instance referenced by this. Zephyr will
   * pass as an argument the index in the array or the collection, the method
   * should return a string.
   * 
   * @param i
   *          the index of the element to build the label for
   * @return a label corresponding to the element
   */
  @LabelProvider(ids = { "weights" })
  protected String labelOf(int i) {
    return i < numRelevantInputs ? "Relevant" : "Irrelevant";
  }

  @Override
  public void run() {
    // While clock is not terminated, we can continue to generate data
    while (!clock.isTerminated()) {
      // We tell Zephyr that now is the right time to collect data
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

  /**
   * Zephyr does not use the static main method. But it can be used to run the
   * class by Java directly, without Zephyr.
   */
  public static void main(String[] args) {
    new Experiment().run();
  }
}
