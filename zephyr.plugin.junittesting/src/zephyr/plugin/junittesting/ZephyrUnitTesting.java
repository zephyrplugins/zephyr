package zephyr.plugin.junittesting;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import zephyr.plugin.core.api.internal.ZephyrTests;
import zephyr.plugin.junittesting.bars.BarTest;
import zephyr.plugin.junittesting.busevent.TestBusEvent;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ZephyrTests.class, TestBusEvent.class, RunnableFilesTests.class, BarTest.class })
public class ZephyrUnitTesting {
}
