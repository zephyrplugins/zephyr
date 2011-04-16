package zephyr.plugin.tests.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.tests.codeparser.codetree.ClassNode;


public interface Parser {
  public boolean canParse(Object fieldValue);

  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue);
}
