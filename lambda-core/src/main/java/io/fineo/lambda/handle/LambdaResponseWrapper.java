package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Module;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper similar to the {@link LambdaWrapper}, but supports a response type as well
 */
public abstract class
LambdaResponseWrapper<INPUT, OUTPUT, C extends RequestHandler<INPUT, OUTPUT>>
  extends LambdaBaseWrapper<C> {
  private C inst;

  public LambdaResponseWrapper(Class<C> handlerClass, List<Module> modules) {
    super(handlerClass, modules);
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
  public abstract OUTPUT handle(INPUT event, Context context) throws IOException;
}
