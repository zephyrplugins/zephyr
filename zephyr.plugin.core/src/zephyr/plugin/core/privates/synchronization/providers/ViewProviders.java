package zephyr.plugin.core.privates.synchronization.providers;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.internal.views.ViewProvider;

public class ViewProviders {
  private List<ViewProviderReference> providerReferences = null;

  private static ViewProviderReference createReference(IConfigurationElement element) {
    Object o = null;
    try {
      o = element.createExecutableExtension("class");
      if (o == null || !(o instanceof ViewProvider))
        return null;
    } catch (CoreException e) {
      e.printStackTrace();
    }
    return new ViewProviderReference(element, (ViewProvider) o);
  }

  private static List<ViewProviderReference> createProviders() {
    List<ViewProviderReference> providers = new ArrayList<ViewProviderReference>();
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("zephyr.viewprovider");
    for (IConfigurationElement element : config) {
      ViewProviderReference reference = createReference(element);
      if (reference != null)
        providers.add(reference);
    }
    return providers;
  }

  public synchronized List<ViewProviderReference> findViews(CodeNode codeNode) {
    if (providerReferences == null)
      providerReferences = createProviders();
    List<ViewProviderReference> viewProviders = new ArrayList<ViewProviderReference>();
    for (ViewProviderReference reference : providerReferences)
      if (reference.provider().canViewDraw(codeNode))
        viewProviders.add(reference);
    return viewProviders;
  }
}
