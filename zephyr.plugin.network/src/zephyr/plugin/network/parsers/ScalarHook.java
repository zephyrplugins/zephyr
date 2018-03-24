package zephyr.plugin.network.parsers;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;

import java.lang.annotation.Annotation;

@SuppressWarnings("restriction")
public class ScalarHook implements CodeHook {
  private final String name;
  private final NetworkLoop loop;
  private final long id;
  private final double initialValue;

  public ScalarHook(NetworkLoop loop, String name, long id) {
    this(loop, name, id, Double.NaN);
  }

  public ScalarHook(NetworkLoop loop, String name, long id, double initialValue) {
    this.loop = loop;
    this.name = name;
    this.id = id;
    this.initialValue = initialValue;
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
    return Double.class;
  }

  @Override
  public double getDouble(Object container) throws IllegalArgumentException, IllegalAccessException {
    Double lastValue = loop.getLastValue(id);
    return lastValue == null ? initialValue : lastValue;
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

  public Long id() {
    return id;
  }
}
