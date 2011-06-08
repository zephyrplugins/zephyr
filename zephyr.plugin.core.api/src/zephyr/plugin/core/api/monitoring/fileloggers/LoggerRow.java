package zephyr.plugin.core.api.monitoring.fileloggers;

import java.io.IOException;
import java.io.Writer;

public class LoggerRow extends AbstractFileLogger {
  public LoggerRow(String filepath) throws IOException {
    this(filepath, false);
  }

  public LoggerRow(String filepath, boolean temporaryFile) throws IOException {
    super(filepath, temporaryFile);
  }

  public LoggerRow(Writer writer) {
    super(writer);
  }

  public void writeLegend(String... labels) {
    StringBuilder line = new StringBuilder();
    for (String label : labels)
      line.append(label + " ");
    file.println(line.substring(0, line.length() - 1).toString());
  }

  public void writeRow(double... row) {
    StringBuilder line = new StringBuilder();
    for (Double data : row) {
      if (Double.isInfinite(data))
        data = Double.NaN;
      line.append(data + " ");
    }
    file.println(line.substring(0, line.length() - 1).toString());
  }
}
