package zephyr.plugin.core.api.advertisement;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import zephyr.plugin.core.api.labels.CollectionLabelBuilder;
import zephyr.plugin.core.api.monitoring.LabelBuilder;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Clock;

public class Advertisement {
  static public class Advertised {
    public final Object advertised;
    public final Object info;
    public final Clock clock;

    public Advertised(Clock clock, Object advertised, Object info) {
      this.clock = clock;
      this.advertised = advertised;
      this.info = info;
    }
  }

  static public class DefaultInfoProvider implements InfoProvider {
    @Override
    public Object provideInfo(String label, Object advertised, Stack<Object> parents, Object info) {
      return info;
    }
  };

  public final Signal<Advertised> onAdvertiseNode = new Signal<Advertised>();
  public final Signal<Advertised> onAdvertiseRoot = new Signal<Advertised>();

  private boolean verbose = false;

  public void advertiseInstance(Clock clock, Object drawn, Object info) {
    printMessageOut(String.format("Clock: %s, Instance of: %s Info: %s", clock.info().label(), String.valueOf(drawn),
                                  String.valueOf(info)));
    onAdvertiseNode.fire(new Advertised(clock, drawn, info));
  }

  protected Object provideInfo(String label, Stack<Object> parents, Object advertised, Object info, Object infoProvider) {
    if (infoProvider instanceof InfoProvider) {
      InfoProvider castedInfoProvider = (InfoProvider) infoProvider;
      return castedInfoProvider.provideInfo(label, advertised, parents, info);
    }
    List<Object> orderedParents = new ArrayList<Object>(parents);
    Collections.reverse(orderedParents);
    Method[] methods = infoProvider.getClass().getMethods();
    InfoProviderMethods infoProviderMethods = new InfoProviderMethods(label, advertised, info);
    Method method = infoProviderMethods.findMethod(methods);
    if (method != null)
      return infoProviderMethods.invoke(infoProvider, method);
    for (Object parent : orderedParents) {
      infoProviderMethods.add(parent);
      method = infoProviderMethods.findMethod(methods);
      if (method == null)
        continue;
      return infoProviderMethods.invoke(infoProvider, method);
    }
    return info;
  }

  private Object annotationToInfoProvider(Advertise advertise, Object advertised) {
    Class<?> infoProviderClass = advertise != null ? advertise.infoProvider() : DefaultInfoProvider.class;
    if (DefaultInfoProvider.class.equals(infoProviderClass)
        && advertised.getClass().isAnnotationPresent(Advertise.class))
      infoProviderClass = advertised.getClass().getAnnotation(Advertise.class).infoProvider();
    return newInfoProviderInstance(infoProviderClass);
  }

  public void parse(Clock clock, Object advertised, Object info) {
    Stack<Object> parents = new Stack<Object>();
    Object infoProvider = annotationToInfoProvider(advertised.getClass().getAnnotation(Advertise.class), advertised);
    Object providedInfo = provideInfo("", parents, advertised, info, infoProvider);
    onAdvertiseRoot.fire(new Advertised(clock, advertised, info));
    advertiseInstance(clock, advertised, providedInfo);
    parents.push(advertised);
    recursiveParse(clock, new LabelBuilder(), advertised, parents, info);
  }

  private void recursiveParse(Clock clock, LabelBuilder labelBuilder, Object advertised, Stack<Object> parents,
      Object info) {
    Class<?> objectClass = advertised.getClass();
    while (objectClass != null) {
      Advertise classAdvertise = objectClass.getAnnotation(Advertise.class);
      parseFields(classAdvertise, objectClass, labelBuilder, advertised, parents, clock, info);
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

  private void parseFields(Advertise classAdvertise, Class<?> advertisedClass, LabelBuilder labelBuilder,
      Object advertised, Stack<Object> parents, Clock clock, Object info) {
    for (Field field : getFieldList(advertisedClass)) {
      if (field.isSynthetic() || field.getType().isPrimitive())
        continue;
      if (field.isAnnotationPresent(IgnoreAdvertise.class))
        continue;
      Advertise advertise = field.getAnnotation(Advertise.class);
      if (classAdvertise == null && advertise == null)
        continue;
      field.setAccessible(true);
      Object fieldValue = getFieldValue(advertised, field);
      if (fieldValue == null)
        continue;
      if (field.getType().isArray())
        advertiseElements(labelBuilder, parents, clock, info, field, fieldValue, classAdvertise);
      else
        advertiseAndParse(labelBuilder, parents, clock, info, field, fieldValue, classAdvertise);
    }
  }

  protected String labelOf(LabelBuilder labelBuilder, Field field) {
    return labelBuilder.buildLabel(field.getName());
  }

  protected void advertiseElements(LabelBuilder labelBuilder, Stack<Object> parents, Clock clock, Object info,
      Field field, Object fieldValue, Advertise classAdvertise) {
    if (fieldValue.getClass().getComponentType().isPrimitive())
      return;
    int length = Array.getLength(fieldValue);
    CollectionLabelBuilder collectionLabelBuilder = new CollectionLabelBuilder(labelBuilder, field, length);
    for (int i = 0; i < length; i++) {
      String label = collectionLabelBuilder.elementLabel(i);
      Object advertised = Array.get(fieldValue, i);
      if (advertised == null)
        continue;
      advertiseAndParse(labelBuilder, label, parents, clock, info, field, advertised, classAdvertise);
    }
  }

  protected void advertiseAndParse(LabelBuilder labelBuilder, Stack<Object> parents, Clock clock,
      Object info, Field field, Object advertised, Advertise classAdvertise) {
    advertiseAndParse(labelBuilder, labelOf(labelBuilder, field), parents, clock, info, field, advertised,
                      classAdvertise);
  }

  protected void advertiseAndParse(LabelBuilder labelBuilder, String label, Stack<Object> parents, Clock clock,
      Object info, Field field, Object advertised, Advertise classAdvertise) {
    advertiseField(field, label, parents, advertised, clock, info, classAdvertise);
    labelBuilder.push(label);
    parents.push(advertised);
    recursiveParse(clock, labelBuilder, advertised, parents, info);
    labelBuilder.pop();
    parents.pop();
  }

  private void advertiseField(Field field, String label, Stack<Object> parents, Object advertised,
      Clock clock, Object info, Advertise classAdvertise) {
    if (field.isAnnotationPresent(IgnoreAdvertise.class))
      return;
    Advertise advertise = field.isAnnotationPresent(Advertise.class) ?
        field.getAnnotation(Advertise.class) : classAdvertise;
    Object infoProvider = annotationToInfoProvider(advertise, advertised);
    Object providedInfo = provideInfo(label, parents, advertised, info, infoProvider);
    advertiseInstance(clock, advertised, providedInfo);
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
      String messagePattern = "Warning: Zephyr advertises %s but it is not final: new values will not be advertised";
      printMessageErr(String.format(messagePattern, field.toGenericString()));
    }
    try {
      Object instance = field.get(parent);
      if (instance == null)
        printMessageErr(String
            .format("Warning: Zephyr cannot advertise %s because it is null", field.toGenericString()));
      return instance;
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void printMessageOut(String message) {
    if (verbose)
      System.out.println(message);
  }

  private void printMessageErr(String message) {
    if (verbose)
      System.err.println(message);
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }
}
