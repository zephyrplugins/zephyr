package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.wrappers.Wrappers;

public class PrimitiveParser implements FieldParser {
  @Override
  public boolean canParse(Object fieldValue) {
    return CodeTrees.isPrimitive(fieldValue.getClass());
  }

  @Override
  public void parse(CodeParser codeParser, MutableParentNode parentNode, Field field, Object fieldValue) {
    String label = CodeTrees.labelOf(field);
    Object container = ((ClassNode) parentNode).instance();
    int level = CodeTrees.levelOf(field);
    PrimitiveNode node = new PrimitiveNode(label, parentNode, field, container, level);
    parentNode.addChild(node);
    List<MonitorWrapper> localWrappers = Wrappers.getWrappers(field, null);
    if (localWrappers == null)
      return;
    for (MonitorWrapper wrapper : localWrappers) {
      String wrapperLabel = label + wrapper.getClass().getSimpleName();
      Monitored wrapperMonitored = wrapper.createMonitored(node.monitored());
      PrimitiveNode wrapperNode = new PrimitiveNode(wrapperLabel, parentNode, wrapperMonitored, level);
      parentNode.addChild(wrapperNode);
    }
  }
}
