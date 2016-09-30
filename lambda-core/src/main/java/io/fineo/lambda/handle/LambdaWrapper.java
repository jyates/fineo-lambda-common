package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.inject.Module;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Wrapper class that calls the actual lambda function, instantiating the caller class, as
 * necessary.
 */
public abstract class LambdaWrapper<T, C extends LambdaHandler<T>> extends LambdaBaseWrapper<C> {

  public LambdaWrapper(Class<C> handlerClass, List<Module> modules) {
    super(handlerClass, modules);
  }

  /**
   * Subclasses have to implement this method themselves, but all you need to call is
   * {@link #handleInternal(Object, Context)}. This allows AWS to get the java generic type as a
   * real argument and then unpack the event into that type
   *
   * @param event AWS Lambda event
   * @throws IOException on failure
   */
  public abstract void handle(T event, Context context) throws IOException;

  protected final void handleInternal(T event, Context context) throws IOException {
    log(context);
    handleEvent(event, context);
  }

  /**
   * Delegate to {@link #handleEvent(Object)}. Subclasses can override this if they want to use
   * the context
   *
   * @param event   incoming event
   * @param context from aws of the lambda call
   * @throws IOException on failure
   */
  public void handleEvent(T event, Context context) throws IOException {
    handleEvent(event);
  }

  /**
   * Get an instance of the handle and have it handle the event
   *
   * @param event to handle
   * @throws IOException on failure
   */
  public void handleEvent(T event) throws IOException {
    getInstance().handle(event);
  }

  public static void addBasicProperties(List<Module> modules, Properties props) {
    LambdaBaseWrapper.addBasicProperties(modules, props);
  }
}
