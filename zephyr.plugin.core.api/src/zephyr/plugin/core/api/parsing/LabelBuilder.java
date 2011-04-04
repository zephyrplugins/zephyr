package zephyr.plugin.core.api.parsing;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;


public class LabelBuilder {
  public static final String DefaultSeparator = "/";
  public static final String DefaultElementSeparator = ":";

  private final String separator;
  private final Stack<String> prefixStack = new Stack<String>();
  private final Stack<Map<String, LabeledElement>> labelsMapStack = new Stack<Map<String, LabeledElement>>();
  private final String elementSeparator;

  public LabelBuilder() {
    this(DefaultSeparator, DefaultElementSeparator);
  }

  public LabelBuilder(String separator, String elementSeparator) {
    this.separator = separator;
    this.elementSeparator = elementSeparator;
  }

  public void push(String prefix) {
    prefixStack.push(prefix);
  }

  public String pop() {
    return prefixStack.pop();
  }

  public String prefixed(String label) {
    return toString() + separator + label;
  }

  @Override
  public String toString() {
    StringBuilder prefixes = new StringBuilder();
    int prefixIndex = 0;
    for (String prefix : prefixStack) {
      if (prefixIndex > 0)
        prefixes.append(separator);
      prefixes.append(prefix);
      prefixIndex++;
    }
    return prefixes.toString();
  }

  public String buildLabel(String label) {
    String labelPrefix = toString();
    if (labelPrefix.isEmpty())
      return label;
    return labelPrefix + separator + label;
  }

  public void pushLabelMap(Map<String, LabeledElement> labelsMap) {
    labelsMapStack.push(labelsMap);
  }

  public void pushLabelMap(String id, final String[] labels) {
    Map<String, LabeledElement> labelsMap = new LinkedHashMap<String, LabeledElement>();
    labelsMap.put(id, new LabeledElement() {
      @Override
      public String label(int index) {
        return labels[index];
      }
    });
    pushLabelMap(labelsMap);
  }

  public void popLabelMaps() {
    labelsMapStack.pop();
  }

  public LabeledElement getLabeledElement(String id) {
    for (Map<String, LabeledElement> labelsMap : labelsMapStack) {
      LabeledElement labeledElement = labelsMap.get(id);
      if (labeledElement != null)
        return labeledElement;
    }
    return null;
  }

  public String elementLabelSeparator() {
    return elementSeparator;
  }
}
