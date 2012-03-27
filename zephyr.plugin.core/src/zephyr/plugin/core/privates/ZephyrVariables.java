package zephyr.plugin.core.privates;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;

public class ZephyrVariables extends AbstractSourceProvider {
  static private final String ZephyrEnableVariable = "zephyr.plugin.core.isZephyrEnabled";
  static private final Map<String, Boolean> variableValues = new HashMap<String, Boolean>();

  public ZephyrVariables() {
  }

  @Override
  public void dispose() {
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Map getCurrentState() {
    variableValues.put(ZephyrEnableVariable, ZephyrPluginCore.isZephyrEnabled());
    return variableValues;
  }

  @Override
  public String[] getProvidedSourceNames() {
    return new String[] { ZephyrEnableVariable };
  }
}
