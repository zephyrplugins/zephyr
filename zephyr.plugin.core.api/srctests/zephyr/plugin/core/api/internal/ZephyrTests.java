package zephyr.plugin.core.api.internal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import zephyr.plugin.core.api.internal.codeparser.parsers.ParserArray2DTest;
import zephyr.plugin.core.api.internal.codeparser.parsers.ParserArrayLabelsTest;
import zephyr.plugin.core.api.internal.codeparser.parsers.ParserBooleanTest;
import zephyr.plugin.core.api.internal.codeparser.parsers.ParserLevelTest;
import zephyr.plugin.core.api.internal.codeparser.parsers.ParserTest01;
import zephyr.plugin.core.api.internal.codeparser.parsers.ParserTest02;
import zephyr.plugin.core.api.internal.codeparser.parsers.ParserTest03;
import zephyr.plugin.core.api.internal.codeparser.parsers.ParserWrappersTest;
import zephyr.plugin.core.api.internal.logfiles.BZippedLogFileTest;
import zephyr.plugin.core.api.internal.logfiles.GZippedLogFileTest;
import zephyr.plugin.core.api.internal.logfiles.TextLogFileTest;
import zephyr.plugin.core.api.internal.logfiles.ZippedLogFileTest;
import zephyr.plugin.core.api.internal.logging.fileloggers.FileLoggerTest;
import zephyr.plugin.core.api.internal.logging.fileloggers.LoggerRowTest;
import zephyr.plugin.core.api.internal.signals.SignalTest;
import zephyr.plugin.core.api.internal.synchronization.ChronoTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({ SignalTest.class, ChronoTest.class, LoggerRowTest.class, FileLoggerTest.class,
    ParserTest01.class, ParserTest02.class, ParserArrayLabelsTest.class, ParserArray2DTest.class, ParserTest03.class,
    ParserBooleanTest.class, ParserWrappersTest.class, ParserLevelTest.class, TextLogFileTest.class,
    GZippedLogFileTest.class, ZippedLogFileTest.class, BZippedLogFileTest.class })
public class ZephyrTests {
}