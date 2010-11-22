package zephyr.plugin.core.api.advertisement;

import java.util.Stack;

public interface InfoProvider {
  Object provideInfo(String label, Object advertised, Stack<Object> parents, Object info);
}
