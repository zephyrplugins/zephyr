package zephyr.plugin.core.api.codeparser.interfaces;

import java.lang.reflect.Field;

public interface FieldParser {
  public boolean canParse(Object fieldValue);

  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, Field instanceField, String instanceLabel, Object instance);
}
