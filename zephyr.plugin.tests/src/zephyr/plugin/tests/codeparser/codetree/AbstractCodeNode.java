package zephyr.plugin.tests.codeparser.codetree;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.synchronization.Clock;

public abstract class AbstractCodeNode implements CodeNode {
  protected final Field parentField;
  private final ParentNode parent;
  private final String label;
  private String identifier;

  protected AbstractCodeNode(String label, ParentNode parent, Field parentField) {
    this.parent = parent;
    this.parentField = parentField;
    this.label = label;
  }

  @Override
  public ClockNode root() {
    return parent.root();
  }

  @Override
  public ParentNode parent() {
    return parent;
  }

  @Override
  public Clock clock() {
    return parent.clock();
  }

  @Override
  public String id() {
    return label;
  }

  @Override
  public String path() {
    if (identifier == null) {
      String parentIdentifier = parent != null ? parent.path() : "";
      identifier = parentIdentifier + "/" + label;
    }
    return identifier;
  }

  @Override
  public String uiLabel() {
    return label;
  }

  @Override
  public String longLabel() {
    return path();
  }
}
