package zephyr.plugin.core.api.internal.codeparser.interfaces;

public interface CodeNode {
  ParentNode parent();

  String[] path();

  String label();

  int level();
}
