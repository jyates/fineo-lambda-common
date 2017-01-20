package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.annotations.VisibleForTesting;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.fineo.lambda.configure.DefaultCredentialsModule;
import io.fineo.lambda.configure.PropertiesModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

public class LambdaBaseWrapper<C> {
  @VisibleForTesting
  static Logger LOG = LoggerFactory.getLogger(LambdaBaseWrapper.class);

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
    try {
      LOG.debug("Getting instance");
      if (this.guice == null) {
        guice = Guice.createInjector(modules);
      }
      LOG.debug("Got injector");
      if (inst == null) {
        this.inst = guice.getInstance(clazz);
      }
    } catch (CreationException e) {
      if (e.getErrorMessages()
           .stream()
           .filter(
             message -> message.getMessage().contains("No implementation for java.lang.String"))
           .findAny().isPresent()) {
        logSetupProperties();
      }
      throw e;
    }
    LOG.debug("Got instance");
    return this.inst;
  }

  private void logSetupProperties() {
    for (Module m : modules) {
      if (m instanceof PropertiesModule) {
        PropertiesModule pm = (PropertiesModule) m;
        Properties props = pm.getProps();
        LOG.info("Initialized with properties: \n{}", props);
        break;
      }
    }
  }

  public static void log(Context context) {
    if (context == null) {
      System.err.println("Context is null. Generally this should only happen in tests. If you are"
                         + " seeing this message in production, something has gone terribly "
                         + "terribly wrong");
      return;
    }
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
