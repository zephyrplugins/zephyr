package zephyr.plugin.tests.codeparser.codetree;

import zephyr.plugin.core.api.synchronization.Clock;

public interface CodeNode {
  ClockNode root();

  ParentNode parent();

  Clock clock();

  String path();

  String id();

  String uiLabel();

  String longLabel();
}
