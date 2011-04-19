package zephyr.plugin.core.api.parsing;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.helpers.Loggers;
import zephyr.plugin.core.api.monitoring.helpers.Parser;

public class CollectionLabelBuilder {
  private final String collectionPattern;
  private final LabeledElement labeledElement;
  private final String elementLabelSeparator;
  private final boolean includeIndex;

  public CollectionLabelBuilder(LabelBuilder labelBuilder, Field field, int size) {
    this(labelBuilder, size, Parser.labelOf(field), Parser.idOf(field), Loggers.isIndexIncluded(field));
  }

  public CollectionLabelBuilder(LabelBuilder labelBuilder, Field field, int size, boolean includeIndex) {
    this(labelBuilder, size, Parser.labelOf(field), Parser.idOf(field), includeIndex);
  }

  public CollectionLabelBuilder(LabelBuilder labelBuilder, int size, String label, String id, boolean includeIndex) {
    collectionPattern = Labels.collectionPattern(label, size, includeIndex);
    labeledElement = labelBuilder.getLabeledElement(id);
    elementLabelSeparator = labelBuilder.elementLabelSeparator();
    this.includeIndex = includeIndex;
  }

  public CollectionLabelBuilder(LabeledElement labeledElement, String elementLabelSeparator, int size,
      boolean includeIndex) {
    collectionPattern = Labels.collectionPattern("", size, includeIndex);
    this.labeledElement = labeledElement;
    this.elementLabelSeparator = elementLabelSeparator;
    this.includeIndex = includeIndex;
  }

  public String elementLabel(int index) {
    String suffix = labeledElement == null ? null : labeledElement.label(index);
    if (includeIndex && suffix != null)
      suffix = elementLabelSeparator + suffix;
    return Labels.collectionLabel(collectionPattern, index, suffix, includeIndex);
  }
}
