package zephyr.plugin.junittesting.busevent;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import zephyr.ZephyrCore;
import zephyr.plugin.core.async.BusEvent;

public class TestBusEvent {
  @Test
  public void testEventHandling() {
    TestingEvent event = new TestingEvent();
    ZephyrCore.busEvent().syncDispatch(event);
    Assert.assertTrue(event.processed());
  }

  @Test
  public void testMultipleEventHandling() {
    List<TestingEvent> events = new ArrayList<TestingEvent>();
    for (int i = 0; i < 10000; i++)
      events.add(new TestingEvent());
    TestingEvent syncEvent = new TestingEvent();
    BusEvent busEvent = ZephyrCore.busEvent();
    for (TestingEvent event : events)
      busEvent.syncDispatch(event);
    busEvent.syncDispatch(syncEvent);
    for (TestingEvent event : events)
      Assert.assertTrue(event.processed());
    Assert.assertTrue(syncEvent.processed());
  }

  @Test
  public void testEventBothRegistration() {
    TestingEvent event = new TestingEvent();
    ZephyrCore.busEvent().register(event, TestingEventListener.listener);
    ZephyrCore.busEvent().syncDispatch(event);
    Assert.assertTrue(event.processed());
  }

  @Test
  public void testEventOnlyRegistration() {
    TestingEvent event = new TestingEvent("HelloWorld");
    ZephyrCore.busEvent().syncDispatch(event);
    Assert.assertFalse(event.processed());
    ZephyrCore.busEvent().register(event, TestingEventListener.listener);
    ZephyrCore.busEvent().syncDispatch(event);
    Assert.assertTrue(event.processed());
  }
}
