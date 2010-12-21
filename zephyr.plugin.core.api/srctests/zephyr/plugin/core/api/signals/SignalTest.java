package zephyr.plugin.core.api.signals;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SignalTest {

  static class EventInfo {
    int eventValue;

    EventInfo(int eventValue) {
      this.eventValue = eventValue;
    }
  }

  private Signal<EventInfo> signalEvent;
  private Signal<SignalTest> signalThis;

  final public int value01 = 4;
  final public int value02 = 8;
  int info01;

  @Before
  public void setUp() {
    signalEvent = new Signal<EventInfo>();
    signalThis = new Signal<SignalTest>();
  }

  @After
  public void setDown() {
    signalEvent = null;
    signalThis = null;
  }

  @Test
  public void testSignalByEvent() {
    signalEvent.connect(new Listener<EventInfo>() {
      @Override
      public void listen(EventInfo eventInfo) {
        info01 = eventInfo.eventValue;
      }
    });
    signalEvent.fire(new EventInfo(value01));
    Assert.assertTrue(info01 == value01);
    signalEvent.fireIFN(new Signal.EventInfoGenerator<EventInfo>() {
      @Override
      public EventInfo generate() {
        return new EventInfo(value02);
      }
    });
    Assert.assertTrue(info01 == value02);
  }

  @Test
  public void testSignalBySelf() {
    info01 = value01;
    signalThis.connect(new Listener<SignalTest>() {
      @Override
      public void listen(SignalTest eventInfo) {
        Assert.assertTrue(info01 == value01);
        info01 = value02;
      }
    });
    signalThis.fire(this);
    Assert.assertTrue(info01 == value02);
  }

  @Test
  public void testDisconnect() {
    Listener<EventInfo> listener = new Listener<EventInfo>() {
      @Override
      public void listen(EventInfo eventInfo) {
        info01 = eventInfo.eventValue;
      }
    };

    signalEvent.connect(listener);
    info01 = value02;
    signalEvent.fire(new EventInfo(value01));
    Assert.assertTrue(info01 == value01);

    signalEvent.disconnect(listener);
    info01 = value02;
    signalEvent.fire(new EventInfo(value01));
    Assert.assertFalse(info01 == value01);
  }

}
