package zephyr.plugin.plotting.privates.histories;

import java.util.Arrays;

public class History {
  protected float[] history;
  int shift = 0;
  final public int length;
  double sum = 0.0;

  public History(int length) {
    assert length > 0;
    history = allocate(length);
    Arrays.fill(history, 0.0f);
    this.length = length;
  }

  protected float[] allocate(int length) {
    return new float[length];
  }

  public void append(float value) {
    int index = index(shift);
    updateSum(history[index], value);
    history[index] = value;
    shift += 1;
  }

  private void updateSum(double oldValue, double value) {
    sum = sum - oldValue + value;
  }

  protected int index(int index) {
    return (index + 2 * length) % length;
  }

  @Override
  public String toString() {
    return Arrays.toString(history);
  }

  public double sum() {
    return sum;
  }

  synchronized public float[] toArray(float[] result) {
    for (int i = result.length; i > 0; i--) {
      int index = index(shift - i);
      if (index < history.length)
        result[result.length - i] = history[index];
      else
        result[result.length - i] = 0;
    }
    return result;
  }

  public float[] toArray() {
    return toArray(new float[length]);
  }

  public void fill(float value) {
    for (int i = 0; i < length; i++)
      append(value);
  }

  public float get(int x) {
    return history[index(shift + x)];
  }

  public int capacity() {
    return history.length;
  }

  public int shift() {
    return shift;
  }
}
