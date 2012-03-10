package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveArrayNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;


public class PrimitiveArrayParser implements FieldParser {
  @Override
  public boolean canParse(Object fieldValue) {
    Class<? extends Object> fieldClass = fieldValue.getClass();
    if (!fieldClass.isArray())
      return false;
    return CodeTrees.isPrimitive(fieldClass.getComponentType());
  }

  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, Field instanceField, String instanceLabel, Object instance) {
    String label = CodeTrees.labelOf(instanceField);
    int level = CodeTrees.levelOf(instanceField);
    int length = Array.getLength(instance);
    CollectionLabelBuilder labelBuilder = codeParser.newCollectionLabelBuilder(instanceField, length);
    PrimitiveArrayNode<?> arrayPrimitiveNode = PrimitiveArrayNode.createPrimitiveArrayNode(label, parentNode,
                                                                                           instance, labelBuilder,
                                                                                           level);
    parentNode.addChild(arrayPrimitiveNode);
    CodeTrees.popupIFN(codeParser, instanceField, arrayPrimitiveNode);
    return arrayPrimitiveNode;
  }
}
