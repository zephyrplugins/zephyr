package zephyr.plugin.core.api.advertisement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoProviderMethods {
  private final List<Class<?>> argumentTypes = new ArrayList<Class<?>>();
  private final List<Object> argumentInstances = new ArrayList<Object>();
  private final Object info;

  public InfoProviderMethods(String label, Object advertised, Object info) {
    add(label);
    add(advertised);
    add(info);
    this.info = info;
  }

  public void add(Object o) {
    argumentTypes.add(o != null ? o.getClass() : Object.class);
    argumentInstances.add(o);
  }

  public Method findMethod(Method[] methods) {
    Class<?>[] types = new Class<?>[argumentTypes.size()];
    argumentTypes.toArray(types);
    Method method = findMethod(types, methods);
    if (method != null)
      return method;
    types[1] = Object.class;
    return findMethod(types, methods);
  }

  protected Method findMethod(Class<?>[] types, Method[] methods) {
    for (Method method : methods) {
      if (method.getReturnType() == Void.class)
        continue;
      if (Arrays.equals(method.getParameterTypes(), types))
        return method;
    }
    return null;
  }

  public Object invoke(Object infoProvider, Method method) {
    try {
      return method.invoke(infoProvider, argumentInstances.toArray());
    } catch (Exception e) {
      System.err.println("Zephyr warning:");
      e.printStackTrace();
    }
    return info;
  }
}
