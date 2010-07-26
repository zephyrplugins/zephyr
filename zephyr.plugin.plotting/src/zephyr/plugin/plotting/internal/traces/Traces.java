package zephyr.plugin.plotting.internal.traces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Traces {

  static public Map<ClockTraces, Set<Trace>> orderTraces(Collection<Trace> traces) {
    Map<ClockTraces, Set<Trace>> result = new LinkedHashMap<ClockTraces, Set<Trace>>();
    for (Trace trace : traces) {
      Set<Trace> clockSet = result.get(trace.clockTraces);
      if (clockSet == null) {
        clockSet = new LinkedHashSet<Trace>();
        result.put(trace.clockTraces, clockSet);
      }
      clockSet.add(trace);
    }
    return result;
  }

  public static Map<ClockTraces, Set<Trace>> orderTraces(List<TraceData> traceDatas) {
    List<Trace> traces = new ArrayList<Trace>();
    for (TraceData traceData : traceDatas)
      traces.add(traceData.trace);
    return orderTraces(traces);
  }

  public static List<Trace> getAllTraces(ClockTracesManager tracesManager) {
    List<Trace> allTraces = new ArrayList<Trace>();
    for (ClockTraces clockTraces : tracesManager.getClockTraces())
      allTraces.addAll(clockTraces.getTraces());
    return allTraces;
  }
}
