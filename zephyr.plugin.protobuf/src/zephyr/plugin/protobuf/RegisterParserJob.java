package zephyr.plugin.protobuf;

import zephyr.plugin.core.api.internal.codeparser.CodeParsers;
import zephyr.plugin.core.internal.startup.StartupJob;
import zephyr.plugin.protobuf.parsers.LoopParser;
import zephyr.plugin.protobuf.parsers.ScalarParser;
import zephyr.plugin.protobuf.parsers.TableParser;
import zephyr.plugin.protobuf.parsers.TensorParser;

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
    CodeParsers.registerParser(new TableParser());
    CodeParsers.registerParser(new ScalarParser());
    CodeParsers.registerParser(new TensorParser());
    CodeParsers.registerParser(new LoopParser());
  }
}
