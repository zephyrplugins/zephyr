package zephyr.plugin.jnlua;

import java.lang.annotation.Annotation;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import com.naef.jnlua.util.AbstractTableMap;

@SuppressWarnings("restriction")
public class LuaTableHook implements CodeHook {
  private final AbstractTableMap<?> container;
  private final String key;

  public LuaTableHook(AbstractTableMap<?> container, String key) {
    this.container = container;
    this.key = key;
  }

  @Override
  public <T extends Annotation> T getAnnotation(Class<T> c) {
    return null;
  }

  @Override
  public String getName() {
    return key;
  }

  @Override
  public Class<?> getType() {
    return container.get(key).getClass();
  }

  @Override
  public double getDouble(Object container) throws IllegalArgumentException, IllegalAccessException {
    return (Double) this.container.get(key);
  }

  @Override
  public boolean getBoolean(Object container) throws IllegalArgumentException, IllegalAccessException {
    return (Boolean) this.container.get(key);
  }

  @Override
  public boolean isAnnotationPresent(Class<? extends Annotation> c) {
    return false;
  }
}
