package zephyr.plugin.core.api.logging.fileloggers;

import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.abstracts.LoggedContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Logger;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.LabelProvider;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;


public class ParserTest01 {
  static protected final String logged01AddedFromInterface = "logged01AddedFromInterface";
  static protected final String log01LabelInt = "logged03LabelInt";
  static protected final String log01LabelFloat = "logged04LabelFloat";
  static protected final String[] expectedLabels = new String[] { logged01AddedFromInterface, "logged02FieldName",
      log01LabelInt, log01LabelFloat, "logged05Child01logged", "logged06ArrayDouble[0]", "logged06ArrayDouble[1]",
      "logged07ArrayInt[0]", "logged07ArrayInt[1]", "logged08ArrayFloat[0]", "logged08ArrayFloat[1]",
      "logged09ArrayObject[0]logged", "logged09ArrayObject[1]logged", "logged10ArrayLabeled[0:element0]",
      "logged10ArrayLabeled[1:element1]", "logged11ArrayLabeled[0:element0]logged",
      "logged11ArrayLabeled[1:element1]logged", "logged11CollectionLabeled[0:element0]logged",
      "logged11CollectionLabeled[1:element1]logged", "logged12ClassLoggedlogged" };

  @SuppressWarnings("unused")
  static public class TestAddLog01 implements LoggedContainer {
    private double notLogged;
    @Monitor
    private final double logged02FieldName = 9.0;
    @Monitor(label = log01LabelInt)
    private final int logged03 = 6;
    @Monitor(label = log01LabelFloat)
    private final float logged04 = 3.0f;
    @Monitor
    private final TestAddLog02 logged05Child01 = new TestAddLog02();
    @Monitor
    private final double[] logged06ArrayDouble = { 1.0, 2.0 };
    @Monitor
    private final int[] logged07ArrayInt = { 3, 4 };
    @Monitor
    private final float[] logged08ArrayFloat = { 1.0f, 2.0f };
    @Monitor
    private final Object[] logged09ArrayObject = { new TestAddLog02(), new TestAddLog02() };
    @Monitor
    private final float[] logged10ArrayLabeled = { 1.0f, 2.0f };
    @Monitor(id = "elementLabeled")
    private final Object[] logged11ArrayLabeled = { new TestAddLog02(), new TestAddLog02() };
    @Monitor(id = "elementLabeled")
    private final Collection<Object> logged11CollectionLabeled = new HashSet<Object>();
    @Monitor
    private final TestAddLog03 logged12ClassLogged = new TestAddLog03();

    public TestAddLog01() {
      logged11CollectionLabeled.add(new TestAddLog02());
      logged11CollectionLabeled.add(new TestAddLog02());
    }

    @Override
    public void setLogger(Logger logger) {
      logger.add(logged01AddedFromInterface, new Monitored() {
        @Override
        public double loggedValue(long stepTime) {
          return 0;
        }
      });
    }

    @LabelProvider(ids = { "logged10ArrayLabeled", "elementLabeled" })
    private String labelOf(int index) {
      return ":element" + index;
    }
  }

  static public class TestAddLog02 {
    @Monitor
    double logged = 10.0;
    double notLogged = 10.0;
  }

  @Monitor
  static public class TestAddLog03 {
    double logged = 10.0;
  }

  @Test
  public void testAddLog() {
    FileLogger logger = new FileLogger(new StringWriter());
    TestAddLog01 logged01 = new TestAddLog01();
    logger.add(logged01);
    Assert.assertArrayEquals(expectedLabels, logger.getLabels());
  }
}
