package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ObjectParser implements FieldParser {
  @Override
  public boolean canParse(Object fieldValue) {
    return true;
  }

  @Override
  public void parse(CodeParser codeParser, MutableParentNode parentNode, Field field, Object fieldValue) {
    String label = field != null ? field.getName() : "";
    Monitor monitor = field != null ? field.getAnnotation(Monitor.class) : null;
    if (monitor != null && !monitor.label().isEmpty())
      label = monitor.label();
    ClassNode node = new ClassNode(label, parentNode, fieldValue, field);
    parentNode.addChild(node);
    codeParser.recursiveParseClass(node, node.instance());
  }
}
