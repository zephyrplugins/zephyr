package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.logging.abstracts.FieldHandler;
import zephyr.plugin.core.api.logging.abstracts.Logged;
import zephyr.plugin.core.api.logging.abstracts.Logger;

public class PrimitiveTypeHandler implements FieldHandler {

  @Override
  public void addField(Logger logger, final Object container, final Field field) {
    if (field.getType().equals(Boolean.TYPE))
      logger.add(Parser.labelOf(field), createBooleanLogged(container, field));
    else
      logger.add(Parser.labelOf(field), createValueLogged(container, field));
  }

  private Logged createValueLogged(final Object container, final Field field) {
    return new Logged() {
      @Override
      public double loggedValue(long stepTime) {
        try {
          return field.getDouble(container);
        } catch (Exception e) {
        }
        return 0.0;
      }
    };
  }

  private Logged createBooleanLogged(final Object container, final Field field) {
    return new Logged() {
      @Override
      public double loggedValue(long stepTime) {
        try {
          return field.getBoolean(container) ? 1 : 0;
        } catch (Exception e) {
        }
        return 0.0;
      }
    };
  }

  @Override
  public boolean canHandle(Field field, Object container) {
    if ("serialVersionUID".equals(field.getName()))
      return false;
    return field.getType().isPrimitive();
  }
}
