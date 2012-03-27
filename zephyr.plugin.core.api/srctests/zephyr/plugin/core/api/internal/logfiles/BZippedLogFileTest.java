package zephyr.plugin.core.api.internal.logfiles;

import java.io.IOException;

public class BZippedLogFileTest extends LogFileTest {

  @Override
  String getUnitTestLogFile() throws IOException {
    return LogFileTest.getDataPath("zephyr.plugin.core.api", "unittesting01.crtrlog.bz2");
  }

}
