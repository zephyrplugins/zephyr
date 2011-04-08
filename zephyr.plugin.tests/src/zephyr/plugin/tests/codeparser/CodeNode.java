package zephyr.plugin.tests.codeparser;

import java.util.List;

import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.synchronization.Clock;

public interface CodeNode extends Labeled {
  ClockNode root();

  List<CodeNode> children();

  CodeNode parent();

  Clock clock();
}
