package zephyr.plugin.core.internal.synchronization.providers;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

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

  public String pluginID() {
    return element.getContributor().getName();
  }

  public String iconPath() {
    return element.getAttribute("icon");
  }

  public String name() {
    String name = element.getAttribute("name");
    if (!name.isEmpty())
      return name;
    IViewRegistry viewRegistry = PlatformUI.getWorkbench().getViewRegistry();
    IViewDescriptor descriptor = viewRegistry.find(viewID());
    if (descriptor == null)
      return viewID() + " not found";
    return descriptor.getLabel();
  }
}
