package zephyr.plugin.tests.codeparser.parsers;

import java.lang.reflect.Field;
import java.util.Collection;

import zephyr.plugin.tests.codeparser.codetree.ClassNode;


public class PrimitiveCollectionParser implements Parser {
  @Override
  public boolean canParse(Object fieldValue) {
    return fieldValue instanceof Collection;
  }

  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
  }
}
