package zephyr.plugin.core.api.advertisement;

import java.util.Stack;

public interface InfoProvider {
  Object provideInfo(String shortLabel, String fullLabel, Object advertised, Stack<Object> parents, Object info);
}
