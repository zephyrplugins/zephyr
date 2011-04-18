package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;
import java.util.Collection;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;


public class PrimitiveListParser implements Parser {
  @Override
  public boolean canParse(Object fieldValue) {
    return fieldValue instanceof Collection;
  }

  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
  }
}
