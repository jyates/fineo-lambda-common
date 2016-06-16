package io.fineo.lambda.handle;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Module;
import io.fineo.lambda.handle.LambdaWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.fineo.lambda.configure.util.SingleInstanceModule.instanceModule;

/**
 * Wrapper to instantiate the raw stage
 */
public class FineoLambdaWrapper extends LambdaWrapper<YOUR_EVENT_TYPE, YOUR_HANDLER> {

  public LambdaWrapper() throws IOException {
    this(getModules(PropertiesLoaderUtil.load()));
  }

  public LambdaWrapper(List<Module> modules) {
    super(YOUR_HANDLER.class, modules);
  }

  @Override
  public void handle(YOUR_EVENT_TYPE event) throws IOException {
    getInstance().handle(event);
  }

  @VisibleForTesting
  public static List<Module> getModules(Properties props) {
    List<Module> modules = new ArrayList<>();
    addBasicProperties(modules, props);
    // add more Guice modules here
    return modules;
  }
}
