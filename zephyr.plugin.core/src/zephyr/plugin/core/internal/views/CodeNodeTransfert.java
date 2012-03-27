package zephyr.plugin.core.internal.views;


import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;

public class CodeNodeTransfert extends ByteArrayTransfer {
  private static final String TypeName = "codenode-transfer-format"
      + (Long.valueOf(System.currentTimeMillis())).toString();

  private static final int TypeID = registerType(TypeName);
  private static final CodeNodeTransfert Instance = new CodeNodeTransfert();
  private CodeNode[] selection;
  private long selectionSetTime;

  private CodeNodeTransfert() {
  }

  public static CodeNodeTransfert instance() {
    return Instance;
  }

  public CodeNode[] codeNodes() {
    return selection;
  }

  private boolean isInvalidNativeType(Object result) {
    return !(result instanceof byte[]) || !TypeName.equals(new String((byte[]) result));
  }

  @Override
  protected int[] getTypeIds() {
    return new int[] { TypeID };
  }

  @Override
  protected String[] getTypeNames() {
    return new String[] { TypeName };
  }

  @Override
  public void javaToNative(Object object, TransferData transferData) {
    byte[] check = TypeName.getBytes();
    super.javaToNative(check, transferData);
  }

  @Override
  public Object nativeToJava(TransferData transferData) {
    Object result = super.nativeToJava(transferData);
    if (isInvalidNativeType(result))
      System.err.println("LocalSelectionTransfer.errorMessage: isInvalidNativeType");
    return selection;
  }

  public void setSelection(CodeNode[] codeNodes) {
    selection = codeNodes;
  }

  public long getSelectionSetTime() {
    return selectionSetTime;
  }

  public void setSelectionSetTime(long time) {
    selectionSetTime = time;
  }
}
