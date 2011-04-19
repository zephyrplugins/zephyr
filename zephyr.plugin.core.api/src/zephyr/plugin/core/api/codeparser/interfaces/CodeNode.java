package zephyr.plugin.core.api.codeparser.interfaces;

public interface CodeNode {
  ParentNode parent();

  String path();

  String label();

  int level();
}
