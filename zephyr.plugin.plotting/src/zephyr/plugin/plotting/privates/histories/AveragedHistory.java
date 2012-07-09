package zephyr.plugin.plotting.privates.histories;


public class AveragedHistory extends LazyHistory {
  public final int period;
  private int cumPeriod = 0;
  @SuppressWarnings("hiding")
  private float sum = 0.0f;

  public AveragedHistory(int period, int length) {
    super(length);
    this.period = period;
  }

  @Override
  public void append(float value) {
    cumPeriod += 1;
    sum += value;
    if (cumPeriod % period == 0) {
      super.append(sum / cumPeriod);
      cumPeriod = 0;
      sum = 0;
    }
  }

  public int nbBufferedData() {
    return cumPeriod;
  }
}
