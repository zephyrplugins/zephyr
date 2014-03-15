package zephyr.plugin.core.api.internal.codeparser.interfaces;

import java.lang.annotation.Annotation;

public interface CodeHook {
  public <T extends Annotation> T getAnnotation(Class<T> c);

  public String getName();

  public Class<?> getType();

  public double getDouble(Object container) throws IllegalArgumentException, IllegalAccessException;

  public boolean getBoolean(Object container) throws IllegalArgumentException, IllegalAccessException;

  public boolean isAnnotationPresent(Class<? extends Annotation> c);
}
