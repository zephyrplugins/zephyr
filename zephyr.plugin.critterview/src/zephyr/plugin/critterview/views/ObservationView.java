package zephyr.plugin.critterview.views;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.views.TimedView;
import critterbot.CritterbotProblem;

public class ObservationView extends ViewPart implements TimedView {

  public static final String ID = "zephyr.plugin.critterview.view.observation";
  protected double[] currentObservation;
  CritterbotProblem environment;
  CritterViz critterViz;
  Frame awtFrame;
  Composite swtAwtComponent;
  private final zephyr.plugin.core.api.signals.Listener<Clock> clockKilled = new zephyr.plugin.core.api.signals.Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      addTimed("", null);
    }
  };

  @Override
  synchronized public void addTimed(String info, Object drawn) {
    if (environment != null)
      environment.clock().onKill.disconnect(clockKilled);
    if (critterViz != null) {
      critterViz.dispose();
      critterViz = null;
    }
    environment = (CritterbotProblem) drawn;
    if (environment == null)
      return;
    environment.clock().onKill.connect(clockKilled);
    createVizualizer();
  }

  private void createVizualizer() {
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        critterViz = new CritterViz(awtFrame, environment);
        Point point = swtAwtComponent.getSize();
        critterViz.setSize(point.x, point.y);
        critterViz.setPreferredSize(new Dimension(point.x, point.y));
        awtFrame.pack();
      }
    });
  }

  @Override
  public boolean synchronize() {
    currentObservation = environment.currentStep().o_tp1;
    return true;
  }

  @Override
  public void createPartControl(final Composite parent) {
    swtAwtComponent = new Composite(parent, SWT.EMBEDDED | SWT.DOUBLE_BUFFERED);
    awtFrame = SWT_AWT.new_Frame(swtAwtComponent);
    Listener listener = new Listener() {
      @Override
      public void handleEvent(Event e) {
        switch (e.type) {
        case SWT.Dispose:
                  parent.setVisible(false);
                  EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                      awtFrame.dispose();
                    }
                  });
                  break;
                }
              }
    };
    addListenerObject(listener);
  }

  @Override
  public void setFocus() {
  }

  @Override
  synchronized public void dispose() {
    if (environment != null)
      environment.clock().kill();
    super.dispose();
  }

  @Override
  synchronized public void repaint() {
    if (currentObservation != null && environment != null) {
      critterViz.updateDisplay(environment.clock().period(), currentObservation);
      critterViz.repaint();
    } else
      awtFrame.repaint();
  }

  @Override
  public boolean canTimedAdded() {
    return environment == null;
  }

  @Override
  public boolean isDisposed() {
    return swtAwtComponent.isDisposed();
  }
}
