package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.labels.LabeledElement;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.logging.abstracts.Logger;

public class CollectionLabelBuilder {
  private final String collectionPattern;
  private final LabeledElement labeledElement;
  private final String elementLabelSeparator;

  protected CollectionLabelBuilder(Logger logger, Field field, int size) {
    this(logger, size, Parser.labelOf(field), Parser.idOf(field));
  }

  protected CollectionLabelBuilder(Logger logger, int size, String label, String id) {
    collectionPattern = Labels.collectionPattern(label, size);
    labeledElement = logger.labelBuilder().getLabeledElement(id);
    elementLabelSeparator = logger.labelBuilder().elementLabelSeparator();
  }

  protected String elementLabel(int index) {
    return Labels.collectionLabel(collectionPattern, index,
                                 labeledElement == null ? null : elementLabelSeparator + labeledElement.label(index));
  }
}
