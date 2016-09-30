package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import static io.fineo.lambda.handle.LambdaBaseWrapper.log;

/**
 * Helper class for functions that want bi-directional request handling, but also need to throw
 * an exception
 */
public abstract class ThrowingRequestHandler<INPUT, OUTPUT>
  implements RequestHandler<INPUT, OUTPUT> {

  @Override
  public OUTPUT handleRequest(INPUT input, Context context) {
    log(context);
    try {
      return handle(input, context);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public abstract void extendExternalFacingRequestHandlerInstead();

  protected abstract OUTPUT handle(INPUT input, Context context) throws Exception;
}
