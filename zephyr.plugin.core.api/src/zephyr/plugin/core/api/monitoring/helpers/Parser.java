package zephyr.plugin.core.api.monitoring.helpers;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitorAdapter;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;

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
    codeParser.parse(classNode, toParse);
    CodeTrees.traverse(traverser, classNode);
  }
}
