package zephyr.plugin.plotting.privates.view;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.internal.views.ViewProvider;

public class PlotViewProvider implements ViewProvider {
  @Override
  public boolean canViewDraw(CodeNode codeNode) {
    return codeNode instanceof MonitorContainerNode;
  }
}
