package zephyr.plugin.core.api.internal.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ObjectParser implements FieldParser {
  @Override
  public boolean canParse(Object fieldValue) {
    return true;
  }

  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, Field instanceField, String instanceLabel,
      Object instance) {
    String label = instanceLabel != null ? instanceLabel : extractLabel(instanceField);
    ClassNode node = new ClassNode(label, parentNode, instance, instanceField);
    parentNode.addChild(node);
    codeParser.recursiveParseClass(node, node.instance());
    CodeTrees.popupIFN(codeParser, instanceField, node);
    return node;
  }

  private static String extractLabel(Field instanceField) {
    if (instanceField == null)
      return "";
    Monitor monitor = instanceField.getAnnotation(Monitor.class);
    if (monitor != null && !monitor.label().isEmpty())
      return monitor.label();
    return instanceField.getName();
  }
}
