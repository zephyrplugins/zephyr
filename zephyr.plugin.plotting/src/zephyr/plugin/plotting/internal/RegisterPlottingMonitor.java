package zephyr.plugin.plotting.internal;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.startup.StartupJob;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;

public class RegisterPlottingMonitor implements StartupJob {
  @Override
  public int level() {
    return 0;
  }

  @Override
  public void run() {
    ZephyrCore.syncCode().onParse.connect(new Listener<ClassNode>() {
      @Override
      public void listen(ClassNode classNode) {
        ClockTracesManager manager = ZephyrPluginPlotting.tracesManager();
        Clock clock = CodeTrees.clockOf(classNode);
        DataMonitor dataMonitor = manager.dataMonitor(clock);
        MonitoredDataTraverser traverser = new MonitoredDataTraverser(dataMonitor);
        CodeTrees.traverse(traverser, classNode.parent());
      }
    });
  }
}
