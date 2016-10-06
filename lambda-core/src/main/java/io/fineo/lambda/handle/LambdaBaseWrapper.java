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
    LOG.debug("{}: successfully enabled slf4j log factory", success);
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
    LOG.debug("Getting instance");
    if (this.guice == null) {
      guice = Guice.createInjector(modules);
    }
    LOG.debug("Got injector");
    if (inst == null) {
      this.inst = guice.getInstance(clazz);
    }
    LOG.debug("Got instance");
    return this.inst;
  }

  public static void log(Context context) {
    if (context == null) {
      System.err.println("Context is null. Generally this should only happen in tests. If you are"
                         + " seeing this message in production, something has gone terribly "
                         + "terribly wrong");
      return;
    }
    LOG.debug("Setting MDC");
    MDC.clear();
    MDC.put(AWS_REQUEST_ID, context.getAwsRequestId());
    LOG.debug("Finished setting MDC");
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
