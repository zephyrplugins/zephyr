package zephyr.plugin.jnlua;

import zephyr.plugin.core.api.internal.codeparser.CodeParsers;
import zephyr.plugin.core.internal.startup.StartupJob;

@SuppressWarnings("restriction")
public class RegisterParserJob implements StartupJob {

  public RegisterParserJob() {
  }

  @Override
  public int level() {
    return 0;
  }

  @Override
  public void run() {
    CodeParsers.registerParser(new LuaClassParser());
  }
}
