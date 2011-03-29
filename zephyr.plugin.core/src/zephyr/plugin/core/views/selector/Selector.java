package zephyr.plugin.core.views.selector;

import java.util.LinkedHashMap;
import java.util.Map;

import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.views.selector.SelectAction.SelectedDisplay;

public class Selector<T> implements Listener<Advertised> {
  private final Map<Object, Advertised> elements = new LinkedHashMap<Object, Advertised>();
  private final ViewCaster<T> provider;

  public Selector(ViewCaster<T> provider) {
    this.provider = provider;
  }

  synchronized public void register(Advertised advertised) {
    System.out.print(elements.size());
    System.out.print(" -> ");
    if (advertised.advertised == null)
      return;
    elements.put(advertised.advertised, advertised);
    System.out.print(elements.size());
    System.out.print(" " + String.valueOf(advertised.info) + " ");
    System.out.println(String.valueOf(advertised.advertised));
  }

  @Override
  public void listen(Advertised advertised) {
    if (!provider.canViewDraw(advertised.advertised))
      return;
    register(advertised);
  }

  public SelectAction<T> createAction(SelectedDisplay<T> display) {
    return new SelectAction<T>(this, display);
  }

  public Object[] elements() {
    Object[] result = new Object[elements.size()];
    elements.values().toArray(result);
    return result;
  }

  public ViewCaster<T> provider() {
    return provider;
  }
}
