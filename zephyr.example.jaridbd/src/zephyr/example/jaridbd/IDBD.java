package zephyr.example.jaridbd;

import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class IDBD extends LMS {
  private final double metaStepSize;
  private final double[] betas;
  private final double[] hs;

  public IDBD(int nbInputs, double metaStepSize, double initialStepSize) {
    super(nbInputs, initialStepSize);
    this.metaStepSize = metaStepSize;
    betas = new double[nbInputs];
    hs = new double[nbInputs];
    for (int i = 0; i < nbInputs; i++)
      betas[i] = Math.log(initialStepSize);
  }

  @Override
  public void update(double[] x, double y) {
    error = y - predict(x);
    for (int i = 0; i < weights.length; i++) {
      betas[i] = Math.max(-10, betas[i] + (metaStepSize * error * x[i] * hs[i]));
      double alpha_i = Math.exp(betas[i]);
      weights[i] += alpha_i * error * x[i];
      hs[i] = hs[i] * Math.max(0, 1 - alpha_i * x[i] * x[i]) + alpha_i * error * x[i];
    }
  }
}
