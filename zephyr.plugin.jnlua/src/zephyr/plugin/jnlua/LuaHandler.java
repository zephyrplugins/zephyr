package zephyr.plugin.jnlua;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import zephyr.plugin.core.utils.Misc;
import zephyr.plugin.filehandling.IFileHandler;
import com.naef.jnlua.LuaState;

public class LuaHandler implements IFileHandler {

  public LuaHandler() {
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    LuaState luaState = new LuaState();
    luaState.openLibs();
    try {
      FileInputStream file = new FileInputStream(filepath);
      luaState.load(file, filepath);
      luaState.call(0, 0);
      file.close();
    } finally {
      luaState.close();
    }
  }

  @Override
  public List<String> extensions() {
    return Misc.asList("lua");
  }
}
