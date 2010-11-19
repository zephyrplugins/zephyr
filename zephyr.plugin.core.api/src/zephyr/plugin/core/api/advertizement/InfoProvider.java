package zephyr.plugin.core.api.advertizement;

import java.util.Stack;

public interface InfoProvider {
  Object provideInfo(String label, Object advertized, Stack<Object> parents, Object info);
}
