package zephyr.plugin.plotting.internal.dialogbox;

import java.text.Collator;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import zephyr.plugin.plotting.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.traces.ClockTraces;
import zephyr.plugin.plotting.internal.traces.ClockTracesManager;
import zephyr.plugin.plotting.internal.traces.Trace;
import zephyr.plugin.plotting.internal.traces.Traces;

public class SelectDataDialog extends FilteredItemsSelectionDialog {
  private final class ClockItemsFilter extends ItemsFilter {
    private final ClockTraces clockTraces;

    public ClockItemsFilter(ClockTraces currentClockTraces) {
      clockTraces = currentClockTraces;
    }

    @Override
    public boolean equalsFilter(ItemsFilter filter) {
      if (clockTraces != ((ClockItemsFilter) filter).clockTraces)
        return false;
      return super.equalsFilter(filter);
    }

    @Override
    public boolean isSubFilter(ItemsFilter filter) {
      if (clockTraces != ((ClockItemsFilter) filter).clockTraces)
        return false;
      return super.isSubFilter(filter);
    }

    @Override
    public boolean isConsistentItem(Object item) {
      return true;
    }

    @Override
    public boolean matchItem(Object item) {
      Trace trace = (Trace) item;
      if (trace.clockTraces != clockTraces)
        return false;
      return matches(trace.label);
    }
  }

  static private String SettingsID = "SelectDataDialogSettingsID";

  protected ClockTraces currentClockTraces;
  private final Map<String, ClockTraces> labelToClockTraces;
  private final int initialIndex = 0;

  public SelectDataDialog(Shell parentShell) {
    super(parentShell, true);
    setInitialPattern("**");
    labelToClockTraces = buildClockLabels();
  }

  private Map<String, ClockTraces> buildClockLabels() {
    Map<String, ClockTraces> labelToClockTraces = new LinkedHashMap<String, ClockTraces>();
    List<ClockTraces> clocks = ClockTracesManager.manager().getClockTraces();
    for (ClockTraces clockTraces : clocks)
      labelToClockTraces.put(clockTraces.clockLabel(), clockTraces);
    return labelToClockTraces;
  }

  public Set<Trace> getSelectedTraces() {
    Set<Trace> result = new LinkedHashSet<Trace>();
    for (Object o : getResult())
      result.add((Trace) o);
    return result;
  }

  @Override
  protected Control createExtendedContentArea(Composite parent) {
    final Combo combo = new Combo(parent, SWT.READ_ONLY);
    String[] labels = new String[labelToClockTraces.size()];
    labelToClockTraces.keySet().toArray(labels);
    combo.setItems(labels);
    combo.select(initialIndex);
    combo.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        updateContent(combo.getText());
      }
    });
    currentClockTraces = labelToClockTraces.get(combo.getText());
    return combo;
  }

  protected void updateContent(String clockLabel) {
    currentClockTraces = labelToClockTraces.get(clockLabel);
    applyFilter();
  }

  @Override
  protected ItemsFilter createFilter() {
    return new ClockItemsFilter(currentClockTraces);
  }

  @Override
  protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
      IProgressMonitor progressMonitor) throws CoreException {
    List<Trace> allTraces = Traces.getAllTraces(ClockTracesManager.manager());
    progressMonitor.beginTask("Looking...", allTraces.size());
    for (Trace trace : allTraces) {
      contentProvider.add(trace, itemsFilter);
      progressMonitor.worked(1);
    }
    progressMonitor.done();
  }

  @Override
  protected IDialogSettings getDialogSettings() {
    IDialogSettings settings = ZephyrPluginPlotting.getDefault().getDialogSettings();
    IDialogSettings section = settings.getSection(SettingsID);
    if (section == null)
      section = settings.addNewSection(SettingsID);
    return section;
  }

  @Override
  public String getElementName(Object item) {
    return ((Trace) item).label;
  }

  @Override
  protected Comparator<Trace> getItemsComparator() {
    return new Comparator<Trace>() {
      @Override
      public int compare(Trace trace1, Trace trace2) {
        return Collator.getInstance().compare(trace1.label, trace2.label);
      }
    };
  }

  @Override
  protected IStatus validateItem(Object item) {
    return Status.OK_STATUS;
  }

  @Override
  protected void accessedHistoryItem(Object item) {
  }
}
