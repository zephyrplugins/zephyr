package zephyr.plugin.common.startup;

public interface StartupJob {
  int level();

  void run();
}
