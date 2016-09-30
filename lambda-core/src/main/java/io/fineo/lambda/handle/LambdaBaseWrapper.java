package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.fineo.lambda.configure.DefaultCredentialsModule;
import io.fineo.lambda.configure.PropertiesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.List;
import java.util.Properties;

public class LambdaBaseWrapper<C> {

  static final String AWS_REQUEST_ID = "AWSRequestId";
  private static final Logger LOG = LoggerFactory.getLogger(LambdaBaseWrapper.class);

  // run all aws logging through slf4j.
  static {
    boolean success = Slf4jLogFactory.enable();
    LOG.info("{}: successfully enabled slf4j log factory", success);
  }

  private final Class<C> clazz;
  private final List<Module> modules;
  private C inst;
  private Injector guice;

  public LambdaBaseWrapper(Class<C> handlerClass, List<Module> modules) {
    this.clazz = handlerClass;
    this.modules = modules;
  }

  protected C getInstance() {
    if (this.guice == null) {
      guice = Guice.createInjector(modules);
    }
    if (inst == null) {
      this.inst = guice.getInstance(clazz);
    }
    return this.inst;
  }

  public static void log(Context context) {
    MDC.clear();
    MDC.put(AWS_REQUEST_ID, context.getAwsRequestId());
  }

  public static void addBasicProperties(List<Module> modules, Properties props) {
    modules.add(new PropertiesModule(props));
    modules.add(new DefaultCredentialsModule());
  }

  @VisibleForTesting
  public Injector getGuiceForTesting() {
    return guice;
  }
}
