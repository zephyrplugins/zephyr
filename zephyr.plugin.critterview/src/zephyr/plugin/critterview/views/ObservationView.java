package zephyr.plugin.critterview.views;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;

import rlpark.plugin.utils.events.Signal;
import rlpark.plugin.utils.time.Clock;
import zephyr.plugin.common.views.SyncView;
import zephyr.plugin.common.views.TimedView;
import critterbot.CritterbotProblem;

public class ObservationView extends ViewPart implements TimedView {

  public static final String ID = "zephyr.plugin.critterview.view.observation";
  protected double[] currentObservation;
  private CritterbotProblem environment;
  private CritterViz critterViz;
  private Frame awtFrame;
  private Composite swtAwtComponent;
  private final Signal<SyncView> onDispose = new Signal<SyncView>();
  private final rlpark.plugin.utils.events.Listener<Clock> clockKilled = new rlpark.plugin.utils.events.Listener<Clock>() {
    @Override
    public void listen(Clock clock) {
      setTimed(null);
    }
  };

  public ObservationView() {
  }

  @Override
  public boolean canDraw(Object drawn) {
    return drawn instanceof CritterbotProblem;
  }


  @Override
  synchronized public void setTimed(Object drawn) {
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
    critterViz = new CritterViz(awtFrame, environment);
    Point point = swtAwtComponent.getSize();
    critterViz.setSize(point.x, point.y);
    critterViz.setPreferredSize(new Dimension(point.x, point.y));
    awtFrame.pack();
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
                    @SuppressWarnings("synthetic-access")
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
    onDispose.fire(this);
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
  public Object drawn() {
    return environment;
  }


  @Override
  public String viewID() {
    return ID;
  }

  @Override
  public Signal<SyncView> onDispose() {
    return onDispose;
  }
}
