package zephyr.plugin.junittesting;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import zephyr.plugin.core.api.ZephyrTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ZephyrTests.class, RunnableFilesTests.class })
public class UnitTesting {
}
