package zephyr.plugin.jython.internal;

import java.io.IOException;
import java.util.List;

import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import zephyr.plugin.core.internal.utils.Helper;
import zephyr.plugin.core.utils.Misc;
import zephyr.plugin.filehandling.IFileHandler;

public class JythonFileHandler implements IFileHandler {
  static private String JythonRoot = "jython2.5.2rc3";

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    runFile(filepath, fileargs);
  }

  static public void runFile(String filepath, String[] fileargs) {
    PyDictionary table = new PyDictionary();
    PySystemState state = new PySystemState();
    state.argv = buildArgumentList(fileargs);
    state.setClassLoader(new ZepyClassLoader());
    for (String pluginPath : getPluginPythonPath())
      state.path.append(new PyString(pluginToPythonPath(pluginPath)));
    PythonInterpreter interpreter = new PythonInterpreter(table, state);
    interpreter.execfile(filepath);
  }

  static private PyList buildArgumentList(String[] fileargs) {
    PyList result = new PyList();
    for (String arg : fileargs)
      result.add(new PyString(arg));
    return result;
  }

  static private String[] getPluginPythonPath() {
    return new String[] { "pysrc", JythonRoot + "/Lib", JythonRoot + "/Lib/site-packages", JythonRoot + "/jython.jar" };
  }

  static protected String pluginToPythonPath(String pluginPath) {
    return Helper.getPluginLocation(JythonPlugin.getDefault().getBundle(), pluginPath);
  }

  @Override
  public List<String> extensions() {
    return Misc.asList("py");
  }
}
