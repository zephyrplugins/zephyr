package zephyr.plugin.core.api.internal.codeparser.parsers;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.codetree.ObjectCollectionNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.internal.parsing.CollectionLabelBuilder;

public abstract class AbstractCollectionParser<T> implements FieldParser {
  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook instanceField, String instanceLabel,
      Object instance) {
    @SuppressWarnings("unchecked")
    T container = (T) instance;
    int nbChildren = nbChildren(container);
    String label = instanceField != null ? instanceField.getName() : "";
    CollectionLabelBuilder labelBuilder = codeParser.newCollectionLabelBuilder(instanceField, nbChildren);
    ObjectCollectionNode collectionNode = new ObjectCollectionNode(label, parentNode, instance, instanceField);
    parentNode.addChild(collectionNode);
    beginChildrenParse(container);
    for (int i = 0; i < nbChildren; i++) {
      Object element = getElement(container, i);
      if (element == null)
        continue;
      String elementLabel = labelBuilder.elementLabel(i);
      parseElement(codeParser, collectionNode, elementLabel, element, instanceField);
    }
    endChildrenParse();
    return collectionNode;
  }

  protected void beginChildrenParse(T fieldValue) {
  }

  protected void endChildrenParse() {
  }

  private static void parseElement(CodeParser codeParser, MutableParentNode parentNode, String elementLabel,
      Object element, CodeHook field) {
    ClassNode childNode;
    if (element.getClass().isArray()) {
      childNode = new ObjectCollectionNode(elementLabel, parentNode, element, field);
      codeParser.recursiveParseInstance(childNode, null, null, element);
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
