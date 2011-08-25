package zephyr.plugin.core.api.monitoring.helpers;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;

public class Parser {
  public static void parse(DataMonitor dataMonitor, Object toParse, int levelRequired) {
    MonitoredDataTraverser traverser = new MonitoredDataTraverser(dataMonitor, levelRequired);
    CodeParser codeParser = new CodeTreeParser(levelRequired);
    ClassNode classNode = new ClassNode();
    codeParser.parse(classNode, toParse);
    CodeTrees.traverse(traverser, classNode);
  }
}
