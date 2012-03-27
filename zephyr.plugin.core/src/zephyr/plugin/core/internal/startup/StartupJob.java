package zephyr.plugin.core.internal.startup;

public interface StartupJob {
  int level();

  void run();
}
