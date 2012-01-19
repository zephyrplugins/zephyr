package zephyr.example.jaridbd;

import java.util.Random;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.parsing.LabelProvider;
import zephyr.plugin.core.api.synchronization.Clock;

/**
 * The class is annotated with @Monitor meaning that Zephyr will explore all the
 * fields including problem, LMS and IDBD
 * 
 */
@Monitor
public class Experiment {
  final static private int numTotalInputs = 20;
  final static private int numRelevantInputs = 5;

  final private Problem problem = new Problem(new Random(0), numTotalInputs, numRelevantInputs);
  final private LMS lms = new LMS(numTotalInputs, .1 / numTotalInputs);
  final private IDBD idbd = new IDBD(numTotalInputs, 0.01, .1 / numTotalInputs);
  final private Clock clock = new Clock("Experiment");

  public Experiment() {
    Zephyr.advertise(clock, this);
  }

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

  private void step() {
    double[] inputs = problem.computeInputs();
    double target = problem.computeTarget(inputs);
    lms.update(inputs, target);
    idbd.update(inputs, target);
    problem.updateWeights();
  }

  public void run() {
    // While clock is not terminated when we tick it, we can continue to
    // generate data
    // Note that data is also collected when we call this method
    while (clock.tick())
      step();
  }

  public static void main(String[] args) {
    new Experiment().run();
  }
}
