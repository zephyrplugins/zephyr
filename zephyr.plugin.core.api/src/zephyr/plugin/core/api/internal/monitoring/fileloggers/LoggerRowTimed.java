package zephyr.plugin.core.api.internal.monitoring.fileloggers;

import java.io.IOException;
import java.io.Writer;

public class LoggerRowTimed extends LoggerRow {
  public LoggerRowTimed(String filepath) throws IOException {
    this(filepath, false);
  }

  public LoggerRowTimed(String filepath, boolean temporaryFile) throws IOException {
    super(filepath, temporaryFile);
  }

  public LoggerRowTimed(Writer writer) {
    super(writer);
  }

  @Override
  public void writeLegend(String... labels) {
    file.print("TimeStamp ");
    super.writeLegend(labels);
  }

  @Override
  public void writeRow(double... row) {
    file.print(String.valueOf(System.currentTimeMillis()) + " ");
    super.writeRow(row);
  }
}
