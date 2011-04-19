package zephyr.plugin.core.api.codeparser.codetree;

import zephyr.plugin.core.api.synchronization.Clock;

public interface CodeNode {
  ClockNode root();

  ParentNode parent();

  Clock clock();

  String[] path();

  String label();
}
