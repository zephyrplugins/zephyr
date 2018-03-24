package zephyr.plugin.core.api.internal.codeparser.codetree;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public interface MonitoredWithCodeHook extends Monitored {
  CodeHook hook();
}
