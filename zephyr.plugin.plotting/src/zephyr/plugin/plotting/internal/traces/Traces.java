package zephyr.plugin.plotting.internal.traces;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.plotting.internal.ZephyrPluginPlotting;

public class Traces {

  static public Map<ClockTraces, Set<Trace>> orderTraces(Collection<Trace> traces) {
    Map<ClockTraces, Set<Trace>> result = new LinkedHashMap<ClockTraces, Set<Trace>>();
    ClockTracesManager tracesManager = ZephyrPluginPlotting.tracesManager();
    for (Trace trace : traces) {
      ClockTraces clockTraces = tracesManager.clockTraces(trace.clock());
      Set<Trace> clockSet = result.get(clockTraces);
      if (clockSet == null) {
        clockSet = new LinkedHashSet<Trace>();
        result.put(clockTraces, clockSet);
      }
      clockSet.add(trace);
    }
    return result;
  }

  public static Collection<? extends Trace> labelToTraces(MonitorContainerNode codeNode) {
    Set<Trace> traces = new LinkedHashSet<Trace>();
    for (String label : codeNode.createLabels())
      traces.add(new Trace(label, (CodeNode) codeNode));
    return traces;
  }
}
