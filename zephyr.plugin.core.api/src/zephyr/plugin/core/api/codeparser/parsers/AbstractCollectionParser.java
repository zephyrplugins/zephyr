package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.ObjectCollectionNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;

public abstract class AbstractCollectionParser<T> implements FieldParser {
  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, Field field, Object fieldValue) {
    @SuppressWarnings("unchecked")
    T container = (T) fieldValue;
    int nbChildren = nbChildren(container);
    String label = field != null ? field.getName() : "";
    CollectionLabelBuilder labelBuilder = codeParser.newCollectionLabelBuilder(field, nbChildren);
    ObjectCollectionNode collectionNode = new ObjectCollectionNode(label, parentNode, fieldValue, field);
    parentNode.addChild(collectionNode);
    beginChildrenParse(container);
    for (int i = 0; i < nbChildren; i++) {
      Object element = getElement(container, i);
      if (element == null)
        continue;
      String elementLabel = labelBuilder.elementLabel(i);
      parseElement(codeParser, collectionNode, elementLabel, element, field);
    }
    endChildrenParse();
    return collectionNode;
  }

  protected void beginChildrenParse(T fieldValue) {
  }

  protected void endChildrenParse() {
  }

  private void parseElement(CodeParser codeParser, MutableParentNode parentNode, String elementLabel, Object element,
      Field field) {
    ClassNode childNode;
    if (element.getClass().isArray()) {
      childNode = new ObjectCollectionNode(elementLabel, parentNode, element, field);
      codeParser.recursiveParseInstance(childNode, null, element);
    } else {
      childNode = new ClassNode(elementLabel, parentNode, element, field);
      codeParser.recursiveParseClass(childNode, element);
    }
    parentNode.addChild(childNode);
    CodeTrees.popupIFN(codeParser, field, childNode);
  }

  abstract protected int nbChildren(T container);

  abstract protected Object getElement(T container, int index);
}
