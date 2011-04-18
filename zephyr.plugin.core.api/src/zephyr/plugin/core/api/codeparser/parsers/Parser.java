package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;


public interface Parser {
  public boolean canParse(Object fieldValue);

  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue);
}
