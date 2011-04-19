package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveCollectionNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;


public class PrimitiveCollectionParser implements FieldParser {
  @Override
  public boolean canParse(Object fieldValue) {
    if (!(fieldValue instanceof List))
      return false;
    List<?> collection = (List<?>) fieldValue;
    if (collection.size() == 0)
      return false;
    Object firstElement = collection.get(0);
    if (firstElement == null)
      return false;
    return CodeTrees.isPrimitive(firstElement.getClass());
  }

  @Override
  public void parse(CodeParser codeParser, MutableParentNode parentNode, Field field, Object fieldValue) {
    int level = CodeTrees.levelOf(field);
    String label = CodeTrees.labelOf(field);
    List<?> list = (List<?>) fieldValue;
    CollectionLabelBuilder labelBuilder = codeParser.newCollectionLabelBuilder(field, list.size());
    PrimitiveCollectionNode arrayPrimitiveNode = new PrimitiveCollectionNode(label, parentNode, list, labelBuilder,
                                                                             level);
    parentNode.addChild(arrayPrimitiveNode);
  }
}
