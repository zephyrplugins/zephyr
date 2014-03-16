package zephyr.plugin.jnlua.test;

import java.io.FileInputStream;
import java.io.IOException;
import com.naef.jnlua.LuaState;

public class Main {
  private static final String filepath = "/Users/degris/GoogleDrive/Coding/deepmind/zephyr.plugin.jnlua/lua/test_jnlua.lua";

  @SuppressWarnings("unused")
  private static void testAddMethod(LuaState luaState) {
    // Define a function
    luaState.load("function add(a, b) return a + b end", "=simple");

    // Evaluate the chunk, thus defining the function
    luaState.call(0, 0); // No arguments, no returns

    // Prepare a function call
    luaState.getGlobal("add"); // Push the function on the stack
    luaState.pushInteger(1); // Push argument #1
    luaState.pushInteger(1); // Push argument #2

    // Call
    luaState.call(2, 1); // 2 arguments, 1 return

    // Get and print result
    int result = luaState.toInteger(1);
    luaState.pop(1); // Pop result
    System.out.println("According to Lua, 1 + 1 = " + result);
  }

  private static void testTorch(LuaState luaState) throws IOException {
    FileInputStream file = new FileInputStream(filepath);
    luaState.load(file, filepath);
    luaState.call(0, 0);
    file.close();
  }

  public static void main(String[] args) throws IOException {
    // Create a Lua state
    LuaState luaState = new LuaState();
    luaState.openLibs();
    try {
      testTorch(luaState);
    } finally {
      luaState.close();
    }
  }
}
