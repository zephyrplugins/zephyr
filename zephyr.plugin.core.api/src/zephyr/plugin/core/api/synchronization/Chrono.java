/*
 * 
 * Created on 21 sept. 2005
 *
 */
package zephyr.plugin.core.api.synchronization;

public class Chrono {
  private long creationTime;

  public Chrono() {
    start();
  }

  public void start() {
    creationTime = System.currentTimeMillis();
  }

  public long getCurrentMillis() {
    return System.currentTimeMillis() - creationTime;
  }

  public double getTime() {
    return getCurrentMillis() / 1000.0;
  }

  public float getCurrentChrono() {
    return (float) (getCurrentMillis() / 1000.0);
  }

  @Override
  public String toString() {
    return toStringMillis(getCurrentMillis());
  }

  public static String toStringMillis(long millisTime) {
    if (millisTime < 1000)
      return String.valueOf(millisTime) + "ms";
    int seconds = (int) (millisTime / 1000.0);
    int hours = seconds / 3600;
    seconds = seconds - hours * 3600;
    int minutes = seconds / 60;
    seconds = seconds - minutes * 60;
    StringBuilder result = new StringBuilder();
    if (hours != 0)
      result.append(hours + "h");
    if (minutes != 0)
      result.append(minutes + "m");
    result.append(seconds + "s");
    return result.toString();
  }
}
