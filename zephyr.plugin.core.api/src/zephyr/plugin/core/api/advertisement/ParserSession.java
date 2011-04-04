package zephyr.plugin.core.api.advertisement;

import java.util.Stack;

import zephyr.plugin.core.api.parsing.LabelBuilder;
import zephyr.plugin.core.api.synchronization.Clock;

public class ParserSession {
  private final Clock clock;
  private final Object info;
  private final LabelBuilder labelBuilder = new LabelBuilder();
  private final Stack<Object> parents = new Stack<Object>();

  public ParserSession(Clock clock, Object info) {
    this.clock = clock;
    this.info = info;
  }

  public Clock clock() {
    return clock;
  }

  public Object info() {
    return info;
  }

  public LabelBuilder labelBuilder() {
    return labelBuilder;
  }

  public Stack<Object> parents() {
    return parents;
  }

  public void push(String label, Object object) {
    labelBuilder.push(label);
    parents.push(object);
  }

  public void pop(String label, Object object) {
    String removedLabel = labelBuilder.pop();
    assert removedLabel.equals(label);
    Object removedObject = parents.pop();
    assert removedObject == object;
  }
}
