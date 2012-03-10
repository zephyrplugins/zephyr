package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
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
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, Field instanceField, String instanceLabel, Object instance) {
    String label = CodeTrees.labelOf(instanceField);
    Object container = ((ClassNode) parentNode).instance();
    int level = CodeTrees.levelOf(instanceField);
    PrimitiveNode node = new PrimitiveNode(label, parentNode, instanceField, container, level);
    parentNode.addChild(node);
    List<MonitorWrapper> localWrappers = Wrappers.getWrappers(instanceField, null);
    if (localWrappers != null)
      addLocalWrapper(parentNode, label, level, node, localWrappers);
    CodeTrees.popupIFN(codeParser, instanceField, node);
    return node;
  }

  private void addLocalWrapper(MutableParentNode parentNode, String label, int level, PrimitiveNode node,
      List<MonitorWrapper> localWrappers) {
    for (MonitorWrapper wrapper : localWrappers) {
      String wrapperLabel = label + wrapper.getClass().getSimpleName();
      Monitored wrapperMonitored = wrapper.createMonitored(node.monitored());
      PrimitiveNode wrapperNode = new PrimitiveNode(wrapperLabel, parentNode, wrapperMonitored, level);
      parentNode.addChild(wrapperNode);
    }
  }
}
