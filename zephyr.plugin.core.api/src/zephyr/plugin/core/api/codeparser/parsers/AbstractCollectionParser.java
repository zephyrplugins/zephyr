package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.CollectionObjectNode;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;
import zephyr.plugin.core.api.parsing.LabelBuilder;

public abstract class AbstractCollectionParser<T> implements Parser {
  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
    boolean includeIndex = true;
    LabelBuilder labelBuilderParent = new LabelBuilder();
    @SuppressWarnings("unchecked")
    T container = (T) fieldValue;
    int nbChildren = nbChildren(container);
    String label = field.getName();
    String id = label;
    CollectionLabelBuilder labelBuilder = new CollectionLabelBuilder(labelBuilderParent, nbChildren, "", id,
                                                                     includeIndex);
    ClassNode collectionNode = new CollectionObjectNode(label, parentNode, fieldValue, field);
    parentNode.addChild(collectionNode);
    for (int i = 0; i < nbChildren; i++) {
      Object element = getElement(container, i);
      if (element == null)
        continue;
      String elementLabel = labelBuilder.elementLabel(i);
      parseElement(codeParser, collectionNode, elementLabel, element, field);
    }
  }

  private void parseElement(CodeParser codeParser, ClassNode parentNode, String elementLabel, Object element,
      Field field) {
    ClassNode node = new ClassNode(elementLabel, parentNode, element, field);
    parentNode.addChild(node);
    codeParser.parseChildren(node);
  }

  abstract protected int nbChildren(T container);

  abstract protected Object getElement(T container, int index);
}
