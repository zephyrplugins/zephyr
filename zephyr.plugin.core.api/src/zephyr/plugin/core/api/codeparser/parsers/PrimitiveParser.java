package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveFieldNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;

public class PrimitiveParser implements FieldParser {
  @Override
  public boolean canParse(Object fieldValue) {
    return CodeTrees.isPrimitive(fieldValue.getClass());
  }

  @Override
  public void parse(CodeParser codeParser, MutableParentNode parentNode, Field field, Object fieldValue) {
    String label = CodeTrees.labelOf(field);
    Object container = ((ClassNode) parentNode).instance();
    PrimitiveFieldNode node = new PrimitiveFieldNode(label, parentNode, field, container);
    parentNode.addChild(node);
  }
}
