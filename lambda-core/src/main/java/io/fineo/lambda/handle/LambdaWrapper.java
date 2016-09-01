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
public abstract class LambdaWrapper<T, C extends LambdaHandler<?>> extends LambdaBaseWrapper<C>{

  private static final String AWS_REQUEST_ID = "AWSRequestId";

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
    MDC.clear();
    MDC.put(AWS_REQUEST_ID, context.getAwsRequestId());
    handleEvent(event, context);
  }

  public void handleEvent(T event, Context context) throws IOException {
    handleEvent(event);
  }

  public void handleEvent(T event) throws IOException {
    handleEvent(event);
  }

  public static void addBasicProperties(List<Module> modules, Properties props) {
    LambdaBaseWrapper.addBasicProperties(modules, props);
  }
}
