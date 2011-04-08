package zephyr.plugin.tests.codeparser;


public class ClassNode extends AbstractCodeNode {
  private final Object instance;

  public ClassNode(CodeNode parent, Object root) {
    super(parent);
    this.instance = root;
  }

  @Override
  public String label() {
    return String.valueOf(instance);
  }
}
