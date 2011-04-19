package zephyr.plugin.core.internal.synchronization.providers;

import org.eclipse.core.runtime.IConfigurationElement;

import zephyr.plugin.core.views.ViewProvider;

public class ViewProviderReference {
  private final ViewProvider provider;
  private final IConfigurationElement element;

  public ViewProviderReference(IConfigurationElement element, ViewProvider provider) {
    this.provider = provider;
    this.element = element;
  }

  public String viewID() {
    return element.getAttribute("viewid");
  }

  public ViewProvider provider() {
    return provider;
  }

  public boolean allowNewView() {
    return true;
  }
}
