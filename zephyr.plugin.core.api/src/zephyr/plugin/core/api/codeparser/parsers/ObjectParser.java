package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;


public class ObjectParser implements Parser {
  @Override
  public boolean canParse(Object fieldValue) {
    return true;
  }

  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
    Object instance = CodeParser.getValueFromField(field, parentNode.instance());
    String label = field.getName();
    ClassNode node = new ClassNode(label, parentNode, instance, field);
    parentNode.addChild(node);
    codeParser.parseChildren(node);
  }
}
