package zephyr.plugin.core.views.selector;

public interface ViewCaster<T> {
  T cast(Object drawn);

  boolean canCast(Object advertised);
}
