package zephyr.plugin.core.api.internal.codeparser.interfaces;


public interface FieldParser {
  public boolean canParse(Object fieldValue);

  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook instanceField, String instanceLabel, Object instance);
}
