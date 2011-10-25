package zephyr.plugin.core.api.parsing;

import zephyr.plugin.core.api.labels.Labels;

public class CollectionLabelBuilder {
  private final String collectionPattern;
  private final LabeledCollection labeledElement;
  private final String elementLabelSeparator;
  private final boolean arrayDecoration;

  public CollectionLabelBuilder(LabelBuilder labelBuilder, int size, String label, String id, boolean arrayDecoration) {
    collectionPattern = Labels.collectionPattern(label, size, arrayDecoration);
    labeledElement = labelBuilder.getLabeledElement(id);
    elementLabelSeparator = labelBuilder.elementLabelSeparator();
    this.arrayDecoration = arrayDecoration;
  }

  public CollectionLabelBuilder(LabeledCollection labeledElement, String elementLabelSeparator, int size,
      boolean arrayDecoration) {
    collectionPattern = Labels.collectionPattern("", size, arrayDecoration);
    this.labeledElement = labeledElement;
    this.elementLabelSeparator = elementLabelSeparator;
    this.arrayDecoration = arrayDecoration;
  }

  public String elementLabel(int index) {
    String suffix = labeledElement == null ? null : labeledElement.label(index);
    if (arrayDecoration && suffix != null)
      suffix = elementLabelSeparator + suffix;
    return Labels.collectionLabel(collectionPattern, index, suffix, arrayDecoration);
  }
}