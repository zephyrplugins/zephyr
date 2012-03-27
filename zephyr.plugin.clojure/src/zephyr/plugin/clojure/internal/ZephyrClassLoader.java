package zephyr.plugin.clojure.internal;

import zephyr.plugin.core.ZephyrCore;
import clojure.lang.Compiler;

public class ZephyrClassLoader extends ClassLoader {
  private final LoaderProxy loaderProxy;

  public ZephyrClassLoader(LoaderProxy loaderProxy) {
    super(loaderProxy.getParent());
    this.loaderProxy = loaderProxy;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Class<?> result = null;
    try {
      result = ZephyrCore.classLoader().loadClass(name);
    } catch (ClassNotFoundException e) {
    }
    if (result != null)
      return result;
    try {
      result = Compiler.class.getClassLoader().loadClass(name);
    } catch (ClassNotFoundException e) {
    }
    if (result != null)
      return result;
    return loaderProxy.loadClass(name);
  }
}
