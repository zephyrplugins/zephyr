package zephyr.plugin.core.internal.observations;

/*
 * From Mike Sokolsky code 
 */
public class ObsStat {
  public final String label;
  final private double rangeMin;
  final private double rangeMax;
  private final int observationIndex;
  private int numSamples;
  double mean;
  double decayMax;
  double decayMin;
  double max = -Double.MAX_VALUE;
  double min = Double.MAX_VALUE;
  private double stdCalc;
  private double stdDev;
  protected double current;
  double length;
  private double barMax;
  double barMin;

  public ObsStat(String label, int observationIndex, double rangeMin, double rangeMax) {
    this.observationIndex = observationIndex;
    this.rangeMin = rangeMin;
    this.rangeMax = rangeMax;
    this.label = label;
  }

  public void updateValue(double[] currentObservation) {
    double newVal = currentObservation[observationIndex];

    stdCalc += numSamples * Math.pow(newVal - mean, 2) / (numSamples + 1);
    numSamples++;
    mean += (newVal - mean) / numSamples;
    stdDev = Math.sqrt(stdCalc / (numSamples + 1));

    decayMax -= (max - min) / 10000;
    decayMin += (max - min) / 10000;

    decayMin = Math.min(decayMin, newVal);
    decayMax = Math.max(decayMax, newVal);

    min = Math.min(min, newVal);
    max = Math.max(max, newVal);
    current = newVal;
    barMax = Math.max(max, rangeMax);
    barMin = Math.min(min, rangeMin);
    length = barMax - barMin;
  }

  public double scaledStdDev() {
    return stdDev / length;
  }
}
