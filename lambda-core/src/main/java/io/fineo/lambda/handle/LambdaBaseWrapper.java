package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.fineo.lambda.configure.DefaultCredentialsModule;
import io.fineo.lambda.configure.PropertiesModule;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class LambdaBaseWrapper<C> {

  private final Class<C> clazz;
  private final List<Module> modules;
  private C inst;

  public LambdaBaseWrapper(Class<C> handlerClass, List<Module> modules) {
    this.clazz = handlerClass;
    this.modules = modules;
  }

  protected C getInstance(){
    if (inst == null) {
      Injector guice = Guice.createInjector(modules);
      this.inst = guice.getInstance(clazz);
    }
    return this.inst;
  }

  public static void addBasicProperties(List<Module> modules, Properties props) {
    modules.add(new PropertiesModule(props));
    modules.add(new DefaultCredentialsModule());
  }
}
