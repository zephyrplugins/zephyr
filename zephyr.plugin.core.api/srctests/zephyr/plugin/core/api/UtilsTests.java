package zephyr.plugin.core.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import zephyr.plugin.core.api.logging.fileloggers.FileLoggerTest;
import zephyr.plugin.core.api.logging.fileloggers.LoggerRowTest;
import zephyr.plugin.core.api.logging.fileloggers.ParserTest01;
import zephyr.plugin.core.api.logging.fileloggers.ParserTest02;
import zephyr.plugin.core.api.logging.fileloggers.ParserTest03;
import zephyr.plugin.core.api.logging.fileloggers.ParserTest04;
import zephyr.plugin.core.api.signals.SignalTest;
import zephyr.plugin.core.api.synchronization.ChronoTest;


@RunWith(Suite.class)
@Suite.SuiteClasses( { SignalTest.class, ChronoTest.class, LoggerRowTest.class, FileLoggerTest.class,
    ParserTest01.class, ParserTest02.class, ParserTest03.class, ParserTest04.class })
public class UtilsTests {
}