package zephyr.plugin.junittesting.busevent;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.core.internal.async.BusEvent;

public class TestBusEvent {
  @Test
  public void testEventHandling() {
    TestingEvent event = new TestingEvent();
    ZephyrSync.busEvent().syncDispatch(event);
    Assert.assertTrue(event.processed());
  }

  @Test
  public void testMultipleEventHandling() {
    List<TestingEvent> events = new ArrayList<TestingEvent>();
    for (int i = 0; i < 10000; i++)
      events.add(new TestingEvent());
    TestingEvent syncEvent = new TestingEvent();
    BusEvent busEvent = ZephyrSync.busEvent();
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
    ZephyrSync.busEvent().register(event, TestingEventListener.listener);
    ZephyrSync.busEvent().syncDispatch(event);
    Assert.assertTrue(event.processed());
  }

  @Test
  public void testEventOnlyRegistration() {
    TestingEvent event = new TestingEvent("HelloWorld");
    ZephyrSync.busEvent().syncDispatch(event);
    Assert.assertFalse(event.processed());
    ZephyrSync.busEvent().register(event, TestingEventListener.listener);
    ZephyrSync.busEvent().syncDispatch(event);
    Assert.assertTrue(event.processed());
  }
}
