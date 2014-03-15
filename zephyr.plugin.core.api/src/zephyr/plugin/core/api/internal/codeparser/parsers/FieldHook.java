package zephyr.plugin.core.api.internal.codeparser.parsers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;

public class FieldHook implements CodeHook {
  private final Field field;

  public FieldHook(Field field) {
    this.field = field;
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> c) {
    return field.getAnnotation(c);
  }

  @Override
  public String getName() {
    return field.getName();
  }

  @Override
  public Class<?> getType() {
    return field.getType();
  }

  @Override
  public double getDouble(Object obj) throws IllegalArgumentException, IllegalAccessException {
    return field.getDouble(obj);
  }

  @Override
  public boolean getBoolean(Object obj) throws IllegalArgumentException, IllegalAccessException {
    return field.getBoolean(obj);
  }

  @Override
  public boolean isAnnotationPresent(Class<? extends Annotation> c) {
    return field.isAnnotationPresent(c);
  }
}
