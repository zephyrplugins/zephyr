package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;


public class ObjectParser implements FieldParser {
  @Override
  public boolean canParse(Object fieldValue) {
    return true;
  }

  @Override
  public void parse(CodeParser codeParser, MutableParentNode parentNode, Field field, Object fieldValue) {
    Object instance = CodeTrees.getValueFromField(field, ((ClassNode) parentNode).instance());
    String label = field.getName();
    ClassNode node = new ClassNode(label, parentNode, instance, field);
    parentNode.addChild(node);
    codeParser.recursiveParseClass(node, node.instance());
  }
}
