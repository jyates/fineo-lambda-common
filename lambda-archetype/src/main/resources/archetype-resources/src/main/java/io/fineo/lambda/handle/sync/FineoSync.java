package io.fineo.lambda.handle.sync;

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
public class FineoSync extends LambdaReponseWrapper<FineoRequest, FineoResponse>{

  public FineoResponse() throws IOException {
    this(getModules(PropertiesLoaderUtil.load()));
  }

  public LambdaWrapper(List<Module> modules) {
    super(FineoRespondsHandler.class, modules);
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

  @Override
  public FineoResponse handle(FineoRequest event, Context context) throws IOException{
    return getInstance().handleRequest(event, context);
  }
}
