package zephyr.plugin.core.api;

public interface ParameterizedRunnable extends Runnable {
  void setParameters(String[] parameters);
}
