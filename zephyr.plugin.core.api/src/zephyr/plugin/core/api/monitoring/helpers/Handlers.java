package zephyr.plugin.core.api.monitoring.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.FieldHandler;

public class Handlers {
  static List<ArrayHandler> arrayHandlers = new ArrayList<ArrayHandler>();
  static List<FieldHandler> fieldHandlers = new ArrayList<FieldHandler>();

  static {
    Handlers.arrayHandlers.add(new ObjectArrayHandler());
    Handlers.arrayHandlers.add(new PrimitiveArrayHandler());
    Handlers.addFieldHandler(new ObjectTypeHandler());
    Handlers.addFieldHandler(new PrimitiveTypeHandler());
    Handlers.addFieldHandler(new CollectionHandler());
    for (ArrayHandler handler : Handlers.arrayHandlers)
      Handlers.addFieldHandler((FieldHandler) handler);
  }

  public static void addFieldHandler(FieldHandler fieldHandler) {
    fieldHandlers.add(fieldHandler);
  }

  protected static List<FieldHandler> getFieldHandlers() {
    List<FieldHandler> handlers = new ArrayList<FieldHandler>(fieldHandlers);
    Collections.reverse(handlers);
    return handlers;
  }

}
