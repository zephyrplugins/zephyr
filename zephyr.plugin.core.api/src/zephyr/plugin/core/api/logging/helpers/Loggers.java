package zephyr.plugin.core.api.logging.helpers;

import zephyr.plugin.core.api.logging.abstracts.Logged;
import zephyr.plugin.core.api.logging.abstracts.Logger;

public class Loggers {
  public static void add(Logger logger, String[] elementLabels, final double[] data) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      logger.add(elementLabels[i], new Logged() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      });
    }
  }

  public static void add(Logger logger, String[] elementLabels, final int[] data) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      logger.add(elementLabels[i], new Logged() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      });
    }
  }

  public static void add(Logger logger, String[] elementLabels, final float[] data) {
    assert elementLabels.length == data.length;
    for (int i = 0; i < data.length; i++) {
      final int elementIndex = i;
      logger.add(elementLabels[i], new Logged() {
        @Override
        public double loggedValue(long stepTime) {
          return data[elementIndex];
        }
      });
    }
  }
}
