package zephyr.plugin.core.api.internal.monitoring.fileloggers;

import java.io.IOException;
import java.util.ArrayList;


public class AverageLoggerRow extends LoggerRow {
  private final int stepsToAvg;
  private final ArrayList<Double> arrayList;

  public AverageLoggerRow(String filepath, int stepsToAvg) throws IOException {
    super(filepath);
    this.stepsToAvg = stepsToAvg;
    arrayList = new ArrayList<Double>(stepsToAvg);
  }

  @Override
  public void writeRow(double... row) {
    assert row.length == 2;
    int time = (int) row[0];
    double errorSquare = row[1];
    arrayList.add(errorSquare);
    if (time % stepsToAvg == stepsToAvg - 1) {
      double avg = 0.0;
      for (Double val : arrayList)
        avg += val;
      avg = avg / stepsToAvg;
      super.writeRow(time - stepsToAvg / 2, avg);
      arrayList.clear();
      arrayList.ensureCapacity(stepsToAvg);
    }
  }
}
