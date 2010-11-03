package zephyr.example.jaridbd;

import java.util.Random;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class Problem {
  private final Random random;
  @Monitor
  private final double[] weights;
  @Monitor
  private final double[] inputs;
  private final int numRelevantInputs;
  private int nbUpdate = 0;

  public Problem(Random random, int numTotalInputs, int numRelevantInputs) {
    this.random = random;
    this.weights = createWeights(numTotalInputs, numRelevantInputs);
    this.inputs = new double[numTotalInputs];
    this.numRelevantInputs = numRelevantInputs;
  }

  private double[] createWeights(int numTotalInputs, int numRelevantInputs) {
    double[] weights = new double[numTotalInputs];
    for (int i = 0; i < weights.length; i++)
      if (i < numRelevantInputs)
        weights[i] = random.nextBoolean() ? -1 : 1;
      else
        weights[i] = 0;
    return weights;
  }

  public double[] computeInputs() {
    for (int i = 0; i < inputs.length; i++)
      inputs[i] = random.nextGaussian();
    return inputs;
  }

  public double computeTarget(double[] inputs) {
    double result = 0.0;
    for (int i = 0; i < inputs.length; i++)
      result += weights[i] * inputs[i];
    return result;
  }

  public void updateWeights() {
    nbUpdate++;
    if (nbUpdate % 100 != 0)
      return;
    weights[random.nextInt(numRelevantInputs)] *= -1;
  }
}
