package zephyr.plugin.junittesting;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import zephyr.plugin.core.api.ZephyrTests;
import zephyr.plugin.junittesting.bars.BarTest;
import zephyr.plugin.junittesting.busevent.TestBusEvent;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ZephyrTests.class, TestBusEvent.class, RunnableFilesTests.class, BarTest.class })
public class UnitTesting {
}
