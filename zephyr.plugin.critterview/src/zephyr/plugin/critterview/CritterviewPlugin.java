package zephyr.plugin.critterview;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import zephyr.Zephyr;
import zephyr.plugin.core.api.signals.Listener;
import critterbot.CritterbotProblem;

public class CritterviewPlugin extends AbstractUIPlugin {
  static private CritterviewPlugin plugin;

  public CritterviewPlugin() {
    super();
  }

  @Override
  public void start(BundleContext context) throws Exception {
    plugin = this;
    super.start(context);
    CritterbotProblem.onNewCritterbotProblem.connect(new Listener<CritterbotProblem>() {
      @Override
      public void listen(CritterbotProblem critterbotProblem) {
        Zephyr.advertize(critterbotProblem.clock(), critterbotProblem);
      }
    });
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static CritterviewPlugin getDefault() {
    return plugin;
  }
}
