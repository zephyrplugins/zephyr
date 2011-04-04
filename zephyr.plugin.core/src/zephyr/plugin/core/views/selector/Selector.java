package zephyr.plugin.core.views.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;

import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.advertisement.DataInfo;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.views.selector.SelectAction.SelectedDisplay;

public class Selector<T> implements Listener<Advertised> {
  private static final String ElementLabelKey = "ElementLabelKey";
  private final Map<Object, Advertised> elements = new LinkedHashMap<Object, Advertised>();
  private final Map<String, List<SelectedDisplay<T>>> sentinels = new LinkedHashMap<String, List<SelectedDisplay<T>>>();
  private final ViewCaster<T> provider;

  public Selector(ViewCaster<T> provider) {
    this.provider = provider;
  }

  synchronized public void register(Advertised advertised) {
    assert advertised.advertised != null;
    elements.put(advertised.advertised, advertised);
    checkSentinels(advertised);
  }

  private void checkSentinels(Advertised advertised) {
    List<SelectedDisplay<T>> displays = sentinels.get(advertised.label());
    if (displays == null)
      return;
    for (SelectedDisplay<T> display : displays)
      selectElement(display, advertised);
    displays.clear();
  }

  @Override
  public void listen(Advertised advertised) {
    if (!provider.canCast(advertised.advertised))
      return;
    register(advertised);
  }

  public SelectAction<T> createAction(SelectedDisplay<T> display) {
    return new SelectAction<T>(this, display);
  }

  public Collection<Advertised> elements() {
    return elements.values();
  }

  public ViewCaster<T> provider() {
    return provider;
  }

  public void registerSentinel(String fullLabel, SelectedDisplay<T> display) {
    Advertised advertised = findAdvertised(fullLabel);
    if (advertised != null) {
      selectElement(display, advertised);
      return;
    }
    List<SelectedDisplay<T>> displayList = sentinels.get(fullLabel);
    if (displayList == null) {
      displayList = new ArrayList<SelectAction.SelectedDisplay<T>>();
      sentinels.put(fullLabel, displayList);
    }
    displayList.add(display);
  }

  private Advertised findAdvertised(String label) {
    for (Advertised advertised : elements.values())
      if (advertised.label().equals(label))
        return advertised;
    return null;
  }

  public void selectElement(final SelectedDisplay<T> display, final Advertised advertised) {
    final T casted = provider.cast(advertised.advertised);
    if (casted == null)
      return;
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        display.select(advertised.clock, casted, (DataInfo) advertised.info);
      }
    });
  }

  public void unregisterSentinel(SelectedDisplay<T> display) {
    for (List<SelectedDisplay<T>> displayList : sentinels.values())
      displayList.remove(display);
  }

  public void parseMemento(IMemento memento, SelectedDisplay<T> display) {
    if (memento == null)
      return;
    String label = memento.getString(ElementLabelKey);
    if (label == null || label.isEmpty())
      return;
    registerSentinel(label, display);
  }

  public void saveState(IMemento memento, String label) {
    memento.putString(ElementLabelKey, label);
  }
}
