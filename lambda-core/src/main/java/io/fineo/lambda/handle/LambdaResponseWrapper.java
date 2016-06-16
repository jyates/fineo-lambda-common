package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.fineo.lambda.configure.DefaultCredentialsModule;
import io.fineo.lambda.configure.PropertiesModule;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Wrapper similar to the {@link LambdaWrapper}, but supports a response type as well
 */
public abstract class
  LambdaResponseWrapper<INPUT, OUTPUT, C extends RequestHandler<INPUT, OUTPUT>> {

  private final Class<C> clazz;
  private final List<Module> modules;
  private C inst;

  public LambdaResponseWrapper(Class<C> handlerClass, List<Module> modules) {
    this.clazz = handlerClass;
    this.modules = modules;
  }

  protected C getInstance() {
    if (inst == null) {
      Injector guice = Guice.createInjector(modules);
      this.inst = guice.getInstance(clazz);
    }
    return this.inst;
  }

  /**
   * Subclasses have to implement this method themselves. Otherwise, AWS Lambda for some reason
   * thinks we are casting the event to a LinkedHashMap. I don't know. Its weird. You shouldn't
   * have to do much in the method beyond {@link #getInstance()} and then
   * {@link LambdaHandler#handle(Object)}.
   *
   * @param event AWS Lambda event
   * @throws IOException on failure
   */
  public OUTPUT handle(INPUT event, Context context) throws IOException {
    return handle(event);
  }

  /**
   * Subclasses have to implement this method themselves. Otherwise, AWS Lambda for some reason
   * thinks we are casting the event to a LinkedHashMap. I don't know. Its weird. You shouldn't
   * have to do much in the method beyond {@link #getInstance()} and then
   * {@link LambdaHandler#handle(Object)}
   *
   * @param event AWS Lambda event
   * @throws IOException on failure
   */
  protected abstract OUTPUT handle(INPUT event) throws IOException;


  public static void addBasicProperties(List<Module> modules, Properties props) {
    modules.add(new PropertiesModule(props));
    modules.add(new DefaultCredentialsModule());
  }
}
