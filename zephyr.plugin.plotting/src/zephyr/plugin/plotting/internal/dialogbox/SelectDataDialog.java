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

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitorAdapter;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.abstracts.MonitoredDataTraverser;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;
import zephyr.plugin.plotting.internal.traces.TraceExtended;
import zephyr.plugin.plotting.internal.traces.Trace;

public class SelectDataDialog extends FilteredItemsSelectionDialog {
  final class TraceFilter extends ItemsFilter {
    private final int level;

    public TraceFilter(int level) {
      this.level = level;
    }

    @Override
    public boolean isConsistentItem(Object item) {
      return true;
    }

    @Override
    public boolean matchItem(Object item) {
      TraceExtended trace = (TraceExtended) item;
      if (trace.codeNode.level() > level)
        return false;
      return matches(trace.name);
    }

    @Override
    public boolean isSubFilter(ItemsFilter filter) {
      if (!super.isSubFilter(filter))
        return false;
      return ((TraceFilter) filter).level <= level;
    }

    @Override
    public boolean equalsFilter(ItemsFilter filter) {
      if (!super.equalsFilter(filter))
        return false;
      return ((TraceFilter) filter).level == level;
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
      private void setLevel() {
        try {
          setLevelValue(Integer.parseInt(spinner.getText()));
        } catch (NumberFormatException e) {
          return;
        }
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
        setLevel();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        setLevel();
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
  protected void fillContentProvider(final AbstractContentProvider contentProvider, final ItemsFilter itemsFilter,
      IProgressMonitor progressMonitor) throws CoreException {
    DataMonitorAdapter monitorAdapter = new DataMonitorAdapter() {
      @Override
      public void add(MonitorContainerNode codeNode) {
        String[] labels = codeNode.createLabels();
        Monitored[] monitored = codeNode.createMonitored();
        for (int i = 0; i < monitored.length; i++)
          contentProvider.add(new TraceExtended(labels[i], (CodeNode) codeNode, monitored[i]), itemsFilter);
      }
    };
    MonitoredDataTraverser traverser = new MonitoredDataTraverser(monitorAdapter, level);
    List<ClockNode> clockNodes = ZephyrCore.syncCode().getClockNodes();
    progressMonitor.beginTask("Looking...", clockNodes.size());
    for (ClockNode clockNode : clockNodes) {
      CodeTrees.traverse(traverser, clockNode);
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
    return ((TraceExtended) item).name;
  }

  @Override
  protected Comparator<TraceExtended> getItemsComparator() {
    return new Comparator<TraceExtended>() {
      @Override
      public int compare(TraceExtended trace1, TraceExtended trace2) {
        return Collator.getInstance().compare(trace1.name, trace2.name);
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
