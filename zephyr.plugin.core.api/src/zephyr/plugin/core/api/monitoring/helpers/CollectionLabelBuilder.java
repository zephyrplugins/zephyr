package zephyr.plugin.core.api.monitoring.helpers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.labels.LabeledElement;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;

public class CollectionLabelBuilder {
  private final String collectionPattern;
  private final LabeledElement labeledElement;
  private final String elementLabelSeparator;
  private final boolean includeIndex;

  protected CollectionLabelBuilder(DataMonitor logger, Field field, int size) {
    this(logger, size, Parser.labelOf(field), Parser.idOf(field), Loggers.isIndexIncluded(field));
  }

  protected CollectionLabelBuilder(DataMonitor logger, int size, String label, String id, boolean includeIndex) {
    collectionPattern = Labels.collectionPattern(label, size, includeIndex);
    labeledElement = logger.labelBuilder().getLabeledElement(id);
    elementLabelSeparator = logger.labelBuilder().elementLabelSeparator();
    this.includeIndex = includeIndex;
  }

  protected String elementLabel(int index) {
    String suffix = labeledElement == null ? null : labeledElement.label(index);
    if (includeIndex && suffix != null)
      suffix = elementLabelSeparator + suffix;
    return Labels.collectionLabel(collectionPattern, index, suffix, includeIndex);
  }
}
