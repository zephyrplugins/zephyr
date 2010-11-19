package zephyr.plugin.core.api.advertizement;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Stack;

import zephyr.plugin.core.api.labels.CollectionLabelBuilder;
import zephyr.plugin.core.api.monitoring.LabelBuilder;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class Advertizement {
  static public class Advertized {
    public final Object advertized;
    public final Object info;
    public final Clock clock;

    public Advertized(Clock clock, Object advertized, Object info) {
      this.clock = clock;
      this.advertized = advertized;
      this.info = info;
    }
  }

  static public class DefaultInfoProvider implements InfoProvider {
    @Override
    public Object provideInfo(String label, Object advertized, Stack<Object> parents, Object info) {
      return info;
    }
  };

  public final Signal<Advertized> onAdvertize = new Signal<Advertized>();

  public void advertizeInstance(Clock clock, Object drawn) {
    onAdvertize.fire(new Advertized(clock, drawn, null));
  }

  public void advertizeInstance(Clock clock, Object drawn, Object info) {
    onAdvertize.fire(new Advertized(clock, drawn, info));
  }

  public void parse(Clock clock, Object advertized, Object info) {
    advertizeInstance(clock, advertized, info);
    Stack<Object> parents = new Stack<Object>();
    parents.push(advertized);
    recursiveParse(clock, new LabelBuilder(), advertized, parents, info);
  }

  private void recursiveParse(Clock clock, LabelBuilder labelBuilder, Object advertized, Stack<Object> parents,
      Object info) {
    Class<?> objectClass = advertized.getClass();
    boolean classIsAdvertized = false;
    while (objectClass != null) {
      Advertize advertize = objectClass.getAnnotation(Advertize.class);
      classIsAdvertized = classIsAdvertized || advertize != null;
      parseFields(classIsAdvertized, objectClass, labelBuilder, advertized, parents, clock, info);
      objectClass = objectClass.getSuperclass();
    }
  }

  private static Field[] getFieldList(Class<?> objectClass) {
    Field[] fields = objectClass.getDeclaredFields();
    Arrays.sort(fields, new Comparator<Field>() {
      @Override
      public int compare(Field o1, Field o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    return fields;
  }

  private void parseFields(boolean classIsAdvertized, Class<?> advertizedClass, LabelBuilder labelBuilder,
      Object advertized, Stack<Object> parents, Clock clock, Object info) {
    for (Field field : getFieldList(advertizedClass)) {
      if (field.isSynthetic() || field.getType().isPrimitive())
        continue;
      Advertize advertize = field.getAnnotation(Advertize.class);
      if (!classIsAdvertized && advertize == null)
        continue;
      field.setAccessible(true);
      Object fieldValue = getFieldValue(advertized, field);
      if (fieldValue == null)
        continue;
      if (field.getType().isArray()) {
        advertizeElements(labelBuilder, parents, clock, info, field, fieldValue);
      } else
        advertizeAndParse(labelBuilder, parents, clock, info, field, fieldValue);
    }
  }

  protected String labelOf(LabelBuilder labelBuilder, Field field) {
    return labelBuilder.buildLabel(field.getName());
  }

  protected void advertizeElements(LabelBuilder labelBuilder, Stack<Object> parents, Clock clock, Object info,
      Field field, Object fieldValue) {
    if (fieldValue.getClass().getComponentType().isPrimitive())
      return;
    int length = Array.getLength(fieldValue);
    CollectionLabelBuilder collectionLabelBuilder = new CollectionLabelBuilder(labelBuilder, field, length);
    for (int i = 0; i < length; i++) {
      String label = collectionLabelBuilder.elementLabel(i);
      Object advertized = Array.get(fieldValue, i);
      advertizeAndParse(labelBuilder, label, parents, clock, info, field, advertized);
    }
  }

  protected void advertizeAndParse(LabelBuilder labelBuilder, Stack<Object> parents, Clock clock,
      Object info, Field field, Object advertized) {
    advertizeAndParse(labelBuilder, labelOf(labelBuilder, field), parents, clock, info, field, advertized);
  }

  protected void advertizeAndParse(LabelBuilder labelBuilder, String label, Stack<Object> parents, Clock clock,
      Object info, Field field, Object advertized) {
    advertizeField(field, label, parents, advertized, clock, info);
    labelBuilder.push(label);
    parents.push(advertized);
    recursiveParse(clock, labelBuilder, advertized, parents, info);
    labelBuilder.pop();
    parents.pop();
  }

  private void advertizeField(Field field, String label, Stack<Object> parents, Object advertized,
      Clock clock, Object info) {
    Advertize advertize = field.getAnnotation(Advertize.class);
    Class<?> infoProviderClass = advertize != null ? advertize.infoProvider() : DefaultInfoProvider.class;
    Object infoProvider = newInfoProviderInstance(infoProviderClass);
    Object providedInfo = info;
    if (infoProvider instanceof InfoProvider) {
      InfoProvider castedInfoProvider = (InfoProvider) infoProvider;
      providedInfo = castedInfoProvider.provideInfo(label, advertized, parents, info);
    }
    advertizeInstance(clock, advertized, providedInfo);
  }

  protected Object newInfoProviderInstance(Class<?> infoProviderClass) {
    try {
      return infoProviderClass.newInstance();
    } catch (InstantiationException e) {
      System.err.println("Zephyr warning:");
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      System.err.println("Zephyr warning:");
      e.printStackTrace();
    }
    return null;
  }

  protected Object getFieldValue(Object parent, Field field) {
    if (!Modifier.isFinal(field.getModifiers())) {
      String messagePattern = "Warning: Zephyr advertizes %s but it is not final: new values will not be advertized";
      System.err.println(String.format(messagePattern, field.toGenericString()));
    }
    try {
      Object instance = field.get(parent);
      if (instance == null)
        System.err.println(String.format("Warning: Zephyr cannot advertize %s because it is null",
                                         field.toGenericString()));
      return instance;
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }
}
