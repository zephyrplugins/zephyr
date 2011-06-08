package zephyr.plugin.core.api.synchronization;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.api.labels.Labeled;

public class ClockInfo implements Labeled, Serializable {
  private static final long serialVersionUID = 5033841109094791785L;
  private final String label;
  private final Map<String, String> captionToValue = new LinkedHashMap<String, String>();
  private final Map<String, String> captionToInfo = new LinkedHashMap<String, String>();
  public final boolean isSuspendable;
  public final boolean isTerminable;

  public ClockInfo(String label, boolean isSuspendable, boolean isTerminable) {
    this.label = label;
    this.isSuspendable = isSuspendable;
    this.isTerminable = isTerminable;
  }

  @Override
  public String label() {
    return label;
  }

  public void putFile(String filepath) {
    File file = new File(filepath);
    put("File", file.getName(), file.getAbsolutePath());
  }

  public void put(String caption, String value) {
    put(caption, value, "");
  }

  public void put(String caption, String value, String info) {
    captionToValue.put(caption, value);
    captionToInfo.put(caption, info);
  }

  public List<String> captions() {
    return new ArrayList<String>(captionToValue.keySet());
  }

  public String value(String caption) {
    return captionToValue.get(caption);
  }

  public String info(String caption) {
    return captionToInfo.get(caption);
  }
}
