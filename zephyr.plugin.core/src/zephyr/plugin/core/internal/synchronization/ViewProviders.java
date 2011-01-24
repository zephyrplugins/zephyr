package zephyr.plugin.core.internal.synchronization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import zephyr.plugin.core.views.ViewProvider;

public class ViewProviders {
  private List<ViewProvider> providers = null;

  private List<ViewProvider> createProviders() {
    List<ViewProvider> providers = new ArrayList<ViewProvider>();
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("zephyr.viewprovider");
    for (IConfigurationElement element : config) {
      Object o;
      try {
        o = element.createExecutableExtension("class");
        if (o instanceof ViewProvider)
          providers.add((ViewProvider) o);
      } catch (CoreException e) {
        e.printStackTrace();
      }
    }
    return providers;
  }

  synchronized protected List<ViewProvider> findViews(Object advertised) {
    if (providers == null)
      providers = createProviders();
    List<ViewProvider> viewProviders = new ArrayList<ViewProvider>();
    for (ViewProvider provider : providers)
      if (provider.canViewDraw(advertised))
        viewProviders.add(provider);
    return viewProviders;
  }
}
