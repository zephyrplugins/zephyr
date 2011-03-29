package zephyr.plugin.core.views.selector;

import zephyr.plugin.core.views.ViewProvider;

public interface ViewCaster<T> extends ViewProvider {
  T cast(Object drawn);
}
