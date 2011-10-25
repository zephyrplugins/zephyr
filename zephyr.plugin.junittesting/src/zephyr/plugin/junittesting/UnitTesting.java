package zephyr.plugin.junittesting;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import zephyr.plugin.core.api.ZephyrTests;
import zephyr.plugin.junittesting.histograms.HistogramTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ZephyrTests.class, RunnableFilesTests.class, HistogramTest.class })
public class UnitTesting {
}
