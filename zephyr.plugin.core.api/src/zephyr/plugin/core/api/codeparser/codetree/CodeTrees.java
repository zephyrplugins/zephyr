package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.codeparser.traverser.Traverser;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

public class CodeTrees {
  static private Class<?>[] primitives = { Double.class, Float.class, Byte.class, Boolean.class, Integer.class,
      Short.class };

  static public boolean isPrimitive(Class<? extends Object> fieldClass) {
    if (fieldClass.isPrimitive())
      return true;
    for (Class<?> c : primitives)
      if (c.equals(fieldClass))
        return true;
    return false;
  }

  static public Object getValueFromField(Field field, Object parentInstance) {
    try {
      return field.get(parentInstance);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  static public void traverse(Traverser traverser, CodeNode root) {
    boolean visitChildren = traverser.inNode(root);
    if (visitChildren && root instanceof ParentNode) {
      ParentNode parent = (ParentNode) root;
      for (int i = 0; i < parent.nbChildren(); i++)
        traverse(traverser, parent.getChild(i));
    }
    traverser.outNode(root);
  }

  public static String labelOf(Field field) {
    if (field == null)
      return "";
    Monitor annotation = field.getAnnotation(Monitor.class);
    if (annotation == null)
      return field.getName();
    String label = annotation.label();
    if (label.isEmpty() && !annotation.emptyLabel())
      label = field.getName();
    return label;
  }

  public static int levelOf(Field field) {
    if (field == null)
      return 0;
    Monitor annotation = field.getAnnotation(Monitor.class);
    if (annotation == null)
      return 0;
    return annotation.level();
  }

  public static ClassNode findParent(CodeNode codeNode, Class<?> target) {
    CodeNode currentNode = codeNode;
    while (currentNode != null) {
      if (currentNode instanceof ClassNode && target.isInstance(((ClassNode) currentNode).instance()))
        return (ClassNode) currentNode;
      currentNode = currentNode.parent();
    }
    return null;
  }

  public static Clock clockOf(CodeNode codeNode) {
    CodeNode currentNode = codeNode;
    while (currentNode != null) {
      if (currentNode instanceof ClockNode)
        return ((ClockNode) currentNode).clock();
      currentNode = currentNode.parent();
    }
    return null;
  }

  public static Map<Clock, Collection<CodeNode>> sortByClock(CodeNode[] supported) {
    Map<Clock, Collection<CodeNode>> result = new HashMap<Clock, Collection<CodeNode>>();
    for (CodeNode codeNode : supported) {
      Clock clockNode = clockOf(codeNode);
      Collection<CodeNode> nodes = result.get(clockNode);
      if (nodes == null) {
        nodes = new ArrayList<CodeNode>();
        result.put(clockNode, nodes);
      }
      nodes.add(codeNode);
    }
    return result;
  }

  public static boolean[] toBooleans(CodeNode[] codeNodes, int displayedIndex) {
    boolean[] result = new boolean[codeNodes.length];
    Arrays.fill(result, false);
    if (displayedIndex != -1)
      result[displayedIndex] = true;
    return result;
  }

  public static boolean[] toBooleansAsTrue(CodeNode[] codeNodes) {
    boolean[] result = new boolean[codeNodes.length];
    Arrays.fill(result, true);
    return result;
  }

  public static Set<String> parseLabels(CodeNode codeNode) {
    final Set<String> labels = new HashSet<String>();
    DataMonitor monitor = new DataMonitor() {
      @Override
      public void add(String label, int level, Monitored monitored) {
        labels.add(label);
      }
    };
    MonitoredDataTraverser traverser = new MonitoredDataTraverser(monitor);
    CodeTrees.traverse(traverser, codeNode);
    return labels;
  }
}
