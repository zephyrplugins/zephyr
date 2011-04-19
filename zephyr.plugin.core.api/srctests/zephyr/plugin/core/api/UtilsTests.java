package zephyr.plugin.core.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import zephyr.plugin.core.api.codeparser.parsers.ParserArray2DTest;
import zephyr.plugin.core.api.codeparser.parsers.ParserArrayLabelsTest;
import zephyr.plugin.core.api.codeparser.parsers.ParserBooleanTest;
import zephyr.plugin.core.api.codeparser.parsers.ParserLevelTest;
import zephyr.plugin.core.api.codeparser.parsers.ParserTest01;
import zephyr.plugin.core.api.codeparser.parsers.ParserTest02;
import zephyr.plugin.core.api.codeparser.parsers.ParserTest03;
import zephyr.plugin.core.api.codeparser.parsers.ParserWrappersTest;
import zephyr.plugin.core.api.logfiles.BZippedLogFileTest;
import zephyr.plugin.core.api.logfiles.GZippedLogFileTest;
import zephyr.plugin.core.api.logfiles.TextLogFileTest;
import zephyr.plugin.core.api.logfiles.ZippedLogFileTest;
import zephyr.plugin.core.api.logging.fileloggers.FileLoggerTest;
import zephyr.plugin.core.api.logging.fileloggers.LoggerRowTest;
import zephyr.plugin.core.api.signals.SignalTest;
import zephyr.plugin.core.api.synchronization.ChronoTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({ SignalTest.class, ChronoTest.class, LoggerRowTest.class, FileLoggerTest.class,
    ParserTest01.class, ParserTest02.class,
    ParserArrayLabelsTest.class, ParserArray2DTest.class, ParserTest03.class,
    ParserBooleanTest.class, ParserWrappersTest.class, ParserLevelTest.class, TextLogFileTest.class,
    GZippedLogFileTest.class, ZippedLogFileTest.class, BZippedLogFileTest.class })
public class UtilsTests {
}