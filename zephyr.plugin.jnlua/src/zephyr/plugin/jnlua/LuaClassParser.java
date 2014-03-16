package zephyr.plugin.jnlua;

import java.util.Map.Entry;
import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import com.naef.jnlua.util.AbstractTableMap;

@SuppressWarnings("restriction")
public class LuaClassParser implements FieldParser {

  @Override
  public boolean canParse(Object fieldValue) {
    return fieldValue instanceof AbstractTableMap;
  }

  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook instanceField,
      String instanceLabel, Object instance) {
    String label = instanceLabel != null ? instanceLabel : "LuaTable";
    ClassNode node = new ClassNode(label, parentNode, instance, instanceField);
    parentNode.addChild(node);
    recursiveParseClass(codeParser, node, (AbstractTableMap<?>) node.instance());
    CodeTrees.popupIFN(codeParser, instanceField, node);
    return node;
  }

  public void recursiveParseClass(CodeParser codeParser, ClassNode classNode, AbstractTableMap<?> container) {
    for (Entry<?, Object> entry : container.entrySet()) {
      String key = (String) entry.getKey();
      codeParser.recursiveParseInstance(classNode, new LuaTableHook(container, key), key, entry.getValue());
    }
  }
}
