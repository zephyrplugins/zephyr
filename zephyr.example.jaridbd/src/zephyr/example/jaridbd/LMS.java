package zephyr.example.jaridbd;

import zephyr.plugin.core.api.internal.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class LMS {
  private final double stepSize;
  /**
   * Indicate to Zephyr to monitor the field error as well as its square
   */
  @Monitor(wrappers = Squared.ID)
  protected double error;
  @Monitor
  protected final double[] weights;

  public LMS(int nbInputs, double stepSize) {
    weights = new double[nbInputs];
    this.stepSize = stepSize;
  }

  public void update(double[] x, double y) {
    error = y - predict(x);
    for (int i = 0; i < weights.length; i++)
      weights[i] += x[i] * stepSize * error;
  }

  protected double predict(double[] x) {
    double result = 0.0;
    for (int i = 0; i < x.length; i++)
      result += weights[i] * x[i];
    return result;
  }
}
