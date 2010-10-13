package zephyr.plugin.core.api.logging.fileloggers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import zephyr.plugin.core.api.monitoring.LabelBuilder;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.helpers.Parser;


public class ParserLevelTest implements DataMonitor {
  @Monitor
  class A {
    int a;
  }

  class B {
    @Monitor
    int b;
  }

  static protected final String[] labelLevel0 = new String[] { "a0a", "b0b", "i0" };
  static protected final String[] labelLevel4 = new String[] { "a0a", "a4a", "b0b", "b4b", "i0", "i4" };
  static final private Map<String, Integer> labelToLevel = new LinkedHashMap<String, Integer>();
  static {
    labelToLevel.put("a0a", 0);
    labelToLevel.put("b0b", 0);
    labelToLevel.put("i0", 0);
    labelToLevel.put("b4b", 4);
    labelToLevel.put("a4a", 4);
    labelToLevel.put("i4", 4);
  }

  @Monitor
  protected int i0 = -2;
  @Monitor
  protected A a0 = new A();
  @Monitor
  protected B b0 = new B();

  @Monitor(level = 4)
  protected int i4 = -2;
  @Monitor(level = 4)
  protected A a4 = new A();
  @Monitor(level = 4)
  protected B b4 = new B();
  private final LabelBuilder labelBuilder = new LabelBuilder("", "");
  final private List<String> labels = new ArrayList<String>();

  @Test
  public void testAddLogLevel0() {
    add(this, 0);
    Assert.assertArrayEquals(labelLevel0, getLabels());
  }

  @Test
  public void testAddLogLevel4() {
    add(this, 4);
    Assert.assertArrayEquals(labelLevel4, getLabels());
  }

  @Test
  public void testAddLogDefaultLevel() {
    add(this);
    Assert.assertArrayEquals(labelLevel4, getLabels());
  }


  private String[] getLabels() {
    String[] result = new String[labels.size()];
    labels.toArray(result);
    return result;
  }

  @Override
  public void add(Object toAdd) {
    add(toAdd, Parser.MonitorEverythingLevel);
  }

  private void add(Object toAdd, int levelRequired) {
    Parser.findAnnotations(this, toAdd, levelRequired);
  }

  @Override
  public void add(String label, Monitored logged, int level) {
    String fullLabel = labelBuilder.buildLabel(label);
    Assert.assertEquals((int) labelToLevel.get(fullLabel), level);
    labels.add(fullLabel);
  }

  @Override
  public LabelBuilder labelBuilder() {
    return labelBuilder;
  }
}