package zephyr.plugin.jython.internal;

import java.io.IOException;
import java.util.List;

import org.python.core.PyDictionary;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import zephyr.ZephyrCore;
import zephyr.plugin.core.Utils;
import zephyr.plugin.filehandling.IFileHandler;

public class JythonFileHandler implements IFileHandler {
  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    PyDictionary table = new PyDictionary();
    PySystemState state = new PySystemState();
    state.setClassLoader(ZephyrCore.classLoader());
    PythonInterpreter interpreter = new PythonInterpreter(table, state);
    interpreter.execfile(filepath);
  }

  @Override
  public List<String> extensions() {
    return Utils.asList("py");
  }
}
