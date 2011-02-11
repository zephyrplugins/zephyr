package zephyr.plugin.jython.internal;

import java.io.IOException;
import java.util.List;

import org.python.core.PyDictionary;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import zephyr.plugin.core.Utils;
import zephyr.plugin.core.utils.Helper;
import zephyr.plugin.filehandling.IFileHandler;

public class JythonFileHandler implements IFileHandler {
  static private String JythonRoot = "jython2.5.2rc3";

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    PyDictionary table = new PyDictionary();
    PySystemState state = new PySystemState();
    state.setClassLoader(new ZepyClassLoader());
    for (String pluginPath : getPluginPythonPath())
      state.path.append(new PyString(pluginToPythonPath(pluginPath)));
    PythonInterpreter interpreter = new PythonInterpreter(table, state);
    interpreter.execfile(filepath);
  }

  private String[] getPluginPythonPath() {
    return new String[] { "pysrc", JythonRoot + "/Lib", JythonRoot + "/Lib/site-packages", JythonRoot + "/jython.jar" };
  }

  protected String pluginToPythonPath(String pluginPath) {
    return Helper.getPluginLocation(JythonPlugin.getDefault(), pluginPath);
  }

  @Override
  public List<String> extensions() {
    return Utils.asList("py");
  }
}
