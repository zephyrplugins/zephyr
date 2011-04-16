package zephyr.plugin.tests.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.tests.codeparser.codetree.ClassNode;


public class PrimitiveArrayParser implements Parser {

  @Override
  public boolean canParse(Object fieldValue) {
    return false;
  }

  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
  }
}
