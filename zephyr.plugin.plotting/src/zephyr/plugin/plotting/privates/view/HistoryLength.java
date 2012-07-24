package zephyr.plugin.plotting.privates.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IMemento;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.internal.ZephyrSync;
import zephyr.plugin.plotting.privates.plots.PlotData;

class HistoryLength {
  final private static String HistoryLengthKey = "historyLength";
  final private static String LinkedKey = "linked";
  final private static Signal<Integer> onLinkedHistoryChanged = new Signal<Integer>();
  static protected HistoryLength signalSource = null;
  protected Spinner historyLength;
  private Integer previousHistoryLength;
  private boolean linked;
  private final PlotData plotdata;
  private PlotView syncView;
  private final Listener<Integer> linkedListener = new Listener<Integer>() {
    @Override
    public void listen(Integer length) {
      if (HistoryLength.this != signalSource) {
        historyLength.setSelection(length);
        setHistoryLengthValue(length);
      }
    }
  };

  protected HistoryLength(PlotData plotdata) {
    this.plotdata = plotdata;
  }

  protected void createPartControl(Composite composite, PlotView syncView) {
    this.syncView = syncView;
    createLinkedButton(composite);
    createHistoryLengthWidget(composite);
  }

  private void createLinkedButton(Composite composite) {
    Button linkedButton = new Button(composite, SWT.CHECK);
    linkedButton.setText("Linked");
    linkedButton.addSelectionListener(new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        setLinked();
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        setLinked();
      }
    });
    linkedButton.setSelection(linked);
  }

  protected void setLinked() {
    linked = !linked;
    if (linked)
      onLinkedHistoryChanged.connect(linkedListener);
    else
      onLinkedHistoryChanged.disconnect(linkedListener);
  }

  private void createHistoryLengthWidget(Composite composite) {
    historyLength = new Spinner(composite, SWT.NONE);
    historyLength.setToolTipText("History Length");
    historyLength.setMinimum(PlotData.MinimumTimeLength);
    historyLength.setMaximum(PlotData.MaximumTimeLength - 1);
    historyLength.setDigits(0);
    if (previousHistoryLength != null)
      setHistoryLengthValue(previousHistoryLength);
    else
      setHistoryLengthValue(length());
    historyLength.setSelection(length());
    historyLength.addSelectionListener(new SelectionListener() {
      private void setHistoryLength() {
        try {
          setHistoryLengthValue(Integer.parseInt(historyLength.getText()));
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
  }

  void setHistoryLengthValue(int value) {
    int increment = (int) (Math.pow(10, Math.floor(Math.log10(value))) / 2);
    historyLength.setIncrement(increment);
    if (plotdata.setHistoryLengthIFN(value))
      ZephyrSync.submitView(syncView, syncView.clocks());
    if (signalSource == null && linked) {
      signalSource = this;
      onLinkedHistoryChanged.fire(length());
      signalSource = null;
    }
  }

  public void init(IMemento memento) {
    previousHistoryLength = memento.getInteger(HistoryLengthKey);
    Boolean linked = memento.getBoolean(LinkedKey);
    if (linked != null && linked)
      setLinked();
  }

  public void saveState(IMemento memento) {
    memento.putInteger(HistoryLengthKey, length());
    memento.putBoolean(LinkedKey, linked);
  }

  protected int length() {
    return plotdata.historyLength();
  }

  public void dispose() {
    if (linked)
      onLinkedHistoryChanged.disconnect(linkedListener);
  }
}
