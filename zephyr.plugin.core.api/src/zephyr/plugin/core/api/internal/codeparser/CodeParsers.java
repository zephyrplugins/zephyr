package zephyr.plugin.core.api.internal.codeparser;

import zephyr.plugin.core.api.internal.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.internal.codeparser.parsers.CodeTreeParser;
import zephyr.plugin.core.api.monitoring.abstracts.LabeledCollection;

public class CodeParsers {

  static public void registerLabeledCollection(LabeledCollection labeledCollection, String... ids) {
    CodeTreeParser.registerLabeledCollection(labeledCollection, ids);
  }

  static public void registerParser(FieldParser parser) {
    CodeTreeParser.registerParser(parser);
  }

}
