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

  public Chrono(long creationTime) {
    this.creationTime = creationTime;
  }

  public void start() {
    creationTime = System.nanoTime();
  }

  public long getCurrentNano() {
    return System.nanoTime() - creationTime;
  }

  public long getCurrentMillis() {
    return getCurrentNano() / 1000000;
  }

  public double getCurrentChrono() {
    return getCurrentNano() / 1000000000.0;
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

  public static String toPeriodString(long periodNano) {
    if (periodNano < 1000000)
      return "0." + String.valueOf(periodNano / 1000) + "ms";
    if (periodNano < 1000000000)
      return String.valueOf(periodNano / 1000000) + "ms";
    return String.valueOf(periodNano / 1000000000) + "s";
  }

  static public long longTimeAgo() {
    return System.nanoTime() - ((long) 365 * (long) 24 * 3600 * 1000000000);
  }
}
