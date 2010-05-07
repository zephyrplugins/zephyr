package zephyr.plugin.plotting.graphs;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IMemento;

import zephyr.plugin.common.views.SyncView;
import zephyr.plugin.plotting.plots.PlotData;

class HistoryLength {
  final private static String HistoryLengthKey = "historyLength";
  Spinner historyLength;
  private Integer previousHistoryLength;
  private final PlotData plotdata;
  private SyncView syncView;

  protected HistoryLength(PlotData plotdata) {
    this.plotdata = plotdata;
  }

  protected void createPartControl(Composite composite, SyncView syncView) {
    Label historyLengthLabel = new Label(composite, SWT.NONE);
    this.syncView = syncView;
    historyLengthLabel.setText("History length: ");
    historyLength = new Spinner(composite, SWT.NONE);
    historyLength.setMinimum(PlotData.MinimumTimeLength);
    historyLength.setMaximum(PlotData.MaximumTimeLength - 1);
    historyLength.setDigits(0);
    composite.pack();
    double nbLetters = Math.floor(Math.log10(PlotData.MaximumTimeLength));
    if (previousHistoryLength != null) {
      setHistoryLengthValue(previousHistoryLength);
      historyLength.setSelection(previousHistoryLength);
    } else
      setHistoryLengthValue(PlotData.MinimumTimeLength);
    historyLength.setLayoutData(new RowData((int) (historyLength.getSize().x / 9.0 * nbLetters),
                                            historyLength.getSize().y));
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
      syncView.repaint();
  }

  public void init(IMemento memento) {
    previousHistoryLength = memento.getInteger(HistoryLengthKey);
  }

  public void saveState(IMemento memento) {
    memento.putInteger(HistoryLengthKey, plotdata.historyLength());
  }
}
