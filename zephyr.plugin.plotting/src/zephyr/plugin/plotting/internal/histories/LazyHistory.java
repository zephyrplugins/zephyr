package zephyr.plugin.plotting.internal.histories;

import java.util.Arrays;


public class LazyHistory extends History {
  static final int blockSize = 1;

  public LazyHistory(int length) {
    super(length);
  }

  @Override
  protected float[] allocate(int length) {
    return new float[Math.min(blockSize, length)];
  }

  private void allocateIFN() {
    int arrayIndex = super.index(shift);
    if (arrayIndex < history.length)
      return;
    int newLength = Math.min(length, history.length + blockSize);
    history = Arrays.copyOf(history, newLength);
  }

  @Override
  public void append(double value) {
    allocateIFN();
    super.append(value);
  }
}
