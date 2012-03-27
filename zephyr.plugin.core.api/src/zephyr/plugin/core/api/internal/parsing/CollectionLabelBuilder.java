package zephyr.plugin.core.api.internal.parsing;

import zephyr.plugin.core.api.monitoring.abstracts.LabeledCollection;


public class CollectionLabelBuilder {
  private final String collectionPattern;
  private final LabeledCollection labeledElement;
  private final String elementLabelSeparator;
  private final boolean arrayDecoration;
  private String[] labels;

  public CollectionLabelBuilder(LabelBuilder labelBuilder, int size, String label, String id, boolean arrayDecoration) {
    collectionPattern = CollectionLabelBuilder.collectionPattern(label, size, arrayDecoration);
    labeledElement = labelBuilder.getLabeledElement(id);
    elementLabelSeparator = labelBuilder.elementLabelSeparator();
    this.arrayDecoration = arrayDecoration;
  }

  public CollectionLabelBuilder(LabeledCollection labeledElement, String elementLabelSeparator, int size,
      boolean arrayDecoration) {
    collectionPattern = CollectionLabelBuilder.collectionPattern("", size, arrayDecoration);
    this.labeledElement = labeledElement;
    this.elementLabelSeparator = elementLabelSeparator;
    this.arrayDecoration = arrayDecoration;
  }

  public String elementLabel(int index) {
    String suffix = labeledElement == null ? null : labeledElement.label(index);
    if (arrayDecoration && suffix != null)
      suffix = elementLabelSeparator + suffix;
    return CollectionLabelBuilder.collectionLabel(collectionPattern, index, suffix, arrayDecoration);
  }

  public int indexOf(String label, int size) {
    createLabels(size);
    for (int i = 0; i < labels.length; i++)
      if (labels[i].equals(label))
        return i;
    return -1;
  }

  public String[] createLabels(int size) {
    if (labels != null)
      return labels;
    labels = new String[size];
    for (int i = 0; i < size; i++)
      labels[i] = elementLabel(i);
    return labels;
  }

  public static String collectionLabel(String collectionPattern, int index, String suffix, boolean arrayDecoration) {
    StringBuilder result = arrayDecoration ?
        new StringBuilder(String.format(collectionPattern, index)) :
        new StringBuilder(collectionPattern);
    if (suffix != null)
      result.append(suffix);
    if (arrayDecoration)
      result.append("]");
    return result.toString();
  }

  public static String collectionPattern(String collectionLabel, int size, boolean arrayDecoration) {
    if (!arrayDecoration)
      return collectionLabel;
    if (size < 10)
      return String.format("%s[%%d", collectionLabel);
    return String.format("%s[%%0%dd", collectionLabel, (int) Math.ceil(Math.log10(size)));
  }
}
