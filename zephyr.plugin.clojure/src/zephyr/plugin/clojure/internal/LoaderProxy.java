package zephyr.plugin.clojure.internal;

public class LoaderProxy extends ClassLoader {
  public LoaderProxy(ClassLoader classLoader) {
    super(classLoader);
  }
}
