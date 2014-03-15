package zephyr.plugin.core.api.internal.codeparser.parsers;

import java.util.List;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.codetree.PrimitiveCollectionNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.internal.parsing.CollectionLabelBuilder;


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
    return Number.class.isAssignableFrom(firstElement.getClass());
  }

  @SuppressWarnings("unchecked")
  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook instanceField, String instanceLabel, Object instance) {
    int level = CodeTrees.levelOf(instanceField);
    String label = CodeTrees.labelOf(instanceField);
    List<? extends Number> list = ((List<? extends Number>) instance);
    CollectionLabelBuilder labelBuilder = codeParser.newCollectionLabelBuilder(instanceField, list.size());
    PrimitiveCollectionNode arrayPrimitiveNode = new PrimitiveCollectionNode(label, parentNode, list, labelBuilder,
                                                                             level);
    parentNode.addChild(arrayPrimitiveNode);
    CodeTrees.popupIFN(codeParser, instanceField, arrayPrimitiveNode);
    return arrayPrimitiveNode;
  }
}
