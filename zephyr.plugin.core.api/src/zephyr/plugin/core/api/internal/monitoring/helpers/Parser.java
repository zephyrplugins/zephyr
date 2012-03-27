package zephyr.plugin.core.api.internal.monitoring.helpers;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.internal.monitoring.abstracts.DataMonitorAdapter;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class Parser {
  public static void parse(final DataMonitor dataMonitor, Object toParse, int levelRequired) {
    DataMonitorAdapter monitorAdapter = new DataMonitorAdapter() {
      @Override
      public void add(MonitorContainerNode codeNode) {
        String codePath = CodeTrees.mergePath(codeNode);
        String[] labels = codeNode.createLabels();
        Monitored[] monitored = codeNode.createMonitored();
        for (int i = 0; i < monitored.length; i++)
          dataMonitor.add(codePath + labels[i], monitored[i]);
      }
    };
    MonitoredDataTraverser traverser = new MonitoredDataTraverser(monitorAdapter, levelRequired);
    CodeParser codeParser = new CodeTreeParser(levelRequired);
    ClassNode classNode = new ClassNode("", null, null, null);
    codeParser.parse(classNode, toParse, null);
    CodeTrees.traverse(traverser, classNode);
  }
}
