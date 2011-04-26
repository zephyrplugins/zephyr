package zephyr.plugin.core.api.monitoring.helpers;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;

public class Parser {
  public static void parse(DataMonitor dataMonitor, Object toParse, int levelRequired) {
    MonitoredDataTraverser traverser = new MonitoredDataTraverser(dataMonitor, levelRequired);
    if (toParse instanceof Monitored)
      dataMonitor.add(Labels.label(toParse), (Monitored) toParse);
    if (toParse instanceof MonitorContainer)
      ((MonitorContainer) toParse).addToMonitor(dataMonitor);
    CodeParser codeParser = new CodeTreeParser();
    ClassNode classNode = codeParser.parse(toParse);
    CodeTrees.traverse(traverser, classNode);
  }
}
