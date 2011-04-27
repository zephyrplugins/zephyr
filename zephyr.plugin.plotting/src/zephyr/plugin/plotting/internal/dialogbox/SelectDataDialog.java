package zephyr.plugin.plotting.internal.dialogbox;

import java.text.Collator;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.traces.Trace;
import zephyr.plugin.plotting.internal.traces.Traces;

public class SelectDataDialog extends FilteredItemsSelectionDialog {
  private final class TraceFilter extends ItemsFilter {
    private final int level;

    public TraceFilter(int level) {
      this.level = level;
    }

    @Override
    public boolean equalsFilter(ItemsFilter filter) {
      if (level != ((TraceFilter) filter).level)
        return false;
      return super.equalsFilter(filter);
    }

    @Override
    public boolean isSubFilter(ItemsFilter filter) {
      if (level != ((TraceFilter) filter).level)
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
      return matches(trace.label);
    }
  }

  static private String SettingsID = "SelectDataDialogSettingsID";

  protected int level;

  public SelectDataDialog(Shell parentShell) {
    super(parentShell, true);
    setInitialPattern("**");
  }

  public Set<Trace> getSelectedTraces() {
    Set<Trace> result = new LinkedHashSet<Trace>();
    for (Object o : getResult())
      result.add((Trace) o);
    return result;
  }

  @Override
  protected Control createExtendedContentArea(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new RowLayout(SWT.HORIZONTAL));
    Label historyLengthLabel = new Label(composite, SWT.NONE);
    historyLengthLabel.setText("Level: ");
    final Spinner spinner = new Spinner(composite, SWT.NONE);
    spinner.setDigits(0);
    spinner.setMinimum(Integer.MIN_VALUE);
    spinner.addSelectionListener(new SelectionListener() {
      private void setHistoryLength() {
        try {
          setLevelValue(Integer.parseInt(spinner.getText()));
        } catch (NumberFormatException e) {
          return;
        }
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
        setHistoryLength();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        setHistoryLength();
      }
    });
    return composite;
  }

  protected void setLevelValue(int level) {
    this.level = level;
    applyFilter();
  }

  @Override
  protected ItemsFilter createFilter() {
    return new TraceFilter(level);
  }

  @Override
  protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
      IProgressMonitor progressMonitor) throws CoreException {
    List<Trace> allTraces = Traces.getAllTraces();
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
