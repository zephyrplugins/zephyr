package zephyr.plugin.core.startup;

public interface StartupJob {
  int level();

  void run();
}
