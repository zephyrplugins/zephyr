package zephyr.plugin.plotting.internal.view;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.views.ViewProvider;

public class PlotViewProvider implements ViewProvider {
  @Override
  public boolean canViewDraw(CodeNode codeNode) {
    return codeNode instanceof MonitorContainer;
  }
}
