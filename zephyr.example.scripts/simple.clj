(ns simple)

(import zephyr.plugin.core.api.synchronization.Clock)
(import zephyr.plugin.core.api.monitoring.abstracts.Monitored)
(import zephyr.plugin.core.api.Zephyr)

(defn toZephyrMonitored [fun]
  (reify Monitored 
    (^double monitoredValue [_] (fun))))

(defn main [args]
  (def clock (new zephyr.plugin.core.api.synchronization.Clock "Simple"))
  (def monitor (.. zephyr.plugin.core.api.Zephyr (getSynchronizedMonitor clock)))
  (def monitoredValue 0)
  (. monitor add "monitoredValue" (toZephyrMonitored (fn [] monitoredValue)))
  (. monitor add "monitoredValueNegative" (toZephyrMonitored (fn [] (* -1 monitoredValue))))
  (while (. clock tick)
    (def monitoredValue (mod (inc monitoredValue) 10))))
