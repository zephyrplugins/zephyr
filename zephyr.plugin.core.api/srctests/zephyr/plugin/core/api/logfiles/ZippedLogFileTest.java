package zephyr.plugin.core.api.logfiles;

import java.io.IOException;

public class ZippedLogFileTest extends LogFileTest {

  @Override
  String getUnitTestLogFile() throws IOException {
    return LogFileTest.getDataPath("zephyr.plugin.core.api", "unittesting01.crtrlog.zip");
  }

}
