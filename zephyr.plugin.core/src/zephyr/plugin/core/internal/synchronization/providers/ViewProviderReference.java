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

  public boolean popUpView() {
    String attribute = element.getAttribute("popup");
    if (attribute == null)
      return true;
    return Boolean.parseBoolean(attribute);
  }

  public String pluginID() {
    return element.getContributor().getName();
  }

  public String iconPath() {
    return element.getAttribute("icon");
  }
}
