package zephyr.plugin.core.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import zephyr.plugin.core.api.advertisement.AdvertisementInvocationTest;
import zephyr.plugin.core.api.advertisement.AdvertisementParentTest;
import zephyr.plugin.core.api.advertisement.AdvertisementTest;
import zephyr.plugin.core.api.logfiles.BZippedLogFileTest;
import zephyr.plugin.core.api.logfiles.GZippedLogFileTest;
import zephyr.plugin.core.api.logfiles.TextLogFileTest;
import zephyr.plugin.core.api.logfiles.ZippedLogFileTest;
import zephyr.plugin.core.api.logging.fileloggers.FileLoggerTest;
import zephyr.plugin.core.api.logging.fileloggers.LoggerRowTest;
import zephyr.plugin.core.api.logging.fileloggers.ParserArray2DTest;
import zephyr.plugin.core.api.logging.fileloggers.ParserArrayLabelsTest;
import zephyr.plugin.core.api.logging.fileloggers.ParserBooleanTest;
import zephyr.plugin.core.api.logging.fileloggers.ParserLevelTest;
import zephyr.plugin.core.api.logging.fileloggers.ParserTest01;
import zephyr.plugin.core.api.logging.fileloggers.ParserTest03;
import zephyr.plugin.core.api.logging.fileloggers.ParserWrappersTest;
import zephyr.plugin.core.api.signals.SignalTest;
import zephyr.plugin.core.api.synchronization.ChronoTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({ SignalTest.class, ChronoTest.class, LoggerRowTest.class, FileLoggerTest.class,
    ParserTest01.class, ParserArrayLabelsTest.class, ParserArray2DTest.class, ParserTest03.class,
    ParserBooleanTest.class, ParserWrappersTest.class, ParserLevelTest.class, AdvertisementTest.class,
    AdvertisementParentTest.class, AdvertisementInvocationTest.class, TextLogFileTest.class, GZippedLogFileTest.class,
    ZippedLogFileTest.class, BZippedLogFileTest.class })
public class UtilsTests {
}