package zephyr.plugin.junittesting.support.checklisteners;

import java.util.concurrent.Semaphore;

import zephyr.plugin.core.internal.async.events.Event;

public class CheckEvent implements Event {
  public static final String CountChildrenID = "zephyr.event.countcontrolchildren";
  public static final String CountColorID = "zephyr.event.countcolorchildren";
  private int result = -Integer.MAX_VALUE;
  private final Semaphore semaphore = new Semaphore(0);
  private final String id;
  private final String viewID;

  public CheckEvent(String eventID, String viewID) {
    this.id = eventID;
    this.viewID = viewID;
  }

  public void setResult(int result) {
    this.result = result;
    semaphore.release();
  }

  @Override
  public String id() {
    return id;
  }

  public int pullResult() {
    try {
      semaphore.acquire();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  public String viewID() {
    return viewID;
  }
}
