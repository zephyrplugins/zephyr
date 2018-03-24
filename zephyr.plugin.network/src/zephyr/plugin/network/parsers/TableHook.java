package zephyr.plugin.network.parsers;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;

import java.lang.annotation.Annotation;

@SuppressWarnings("restriction")
public class TableHook implements CodeHook {
  private final String name;
  private final NetworkLoop loop;

  public TableHook(NetworkLoop loop, String name) {
    this.loop = loop;
    this.name = name;
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> c) {
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Class<?> getType() {
    return null;
  }

  @Override
  public double getDouble(Object container) throws IllegalArgumentException, IllegalAccessException {
    return 0;
  }

  @Override
  public boolean getBoolean(Object container) throws IllegalArgumentException, IllegalAccessException {
    return false;
  }

  @Override
  public boolean isAnnotationPresent(Class<? extends Annotation> c) {
    return false;
  }

  public NetworkLoop networkLoop() {
    return loop;
  }
}
