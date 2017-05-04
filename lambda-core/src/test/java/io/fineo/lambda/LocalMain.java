package io.fineo.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Parent wrapper for running Lambda functions locally
 */
public abstract class LocalMain {

  /**
   * @param args <ol><li>local runner class</li><li>handler class name</li><li>method name</li></ol>
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    String className = args[1];
    String method = args[2];
    Class clazz = Class.forName(className);
    Object o = clazz.newInstance();
    Method m = clazz.getMethod(method, Object.class);
    if (m == null) {
      m = clazz.getMethod(method, Object.class, Object.class);
    }
    if (m == null) {
      throw new RuntimeException(
        "Could not find any method matching: " + method + " in " + className);
    }

    Class<LocalMain> source = (Class<LocalMain>) Class.forName(args[0]);
    String[] remaining = new String[args.length - 3];
    System.arraycopy(args, 3, remaining, 0, remaining.length);
    source.newInstance().run(o, m, remaining);
  }

  protected <T> T map(String json, Class<T> clazz) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(json, clazz);
  }

  abstract void run(Object inst, Method m, String[] args);
}
