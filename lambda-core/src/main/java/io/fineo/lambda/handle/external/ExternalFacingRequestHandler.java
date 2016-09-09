package io.fineo.lambda.handle.external;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Joiner;
import io.fineo.lambda.handle.ThrowingRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public abstract class ExternalFacingRequestHandler<INPUT, OUTPUT>
  extends ThrowingRequestHandler<INPUT, OUTPUT> {
  private static final Logger LOG = LoggerFactory.getLogger(ExternalFacingRequestHandler.class);

  @Override
  public OUTPUT handleRequest(INPUT input, Context context) {
    try {
      return super.handleRequest(input, context);
    } catch (Exception e) {
      e = buildErrorMessage(e, context);
      if (e instanceof RuntimeException) {
        throw (RuntimeException) e;
      }
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private Exception buildErrorMessage(Exception e, Context context) {
    String msg = e.getMessage();
    // wrap the exception into better error handling
    if (msg == null || !msg.startsWith("{")) {
      StringBuffer sb = new StringBuffer(
        "Internal Server Error. Please send this output to errors@fineo.io\n Error:\n ");
      sb.append(e.getClass().getName());
      sb.append("\n");
      if (msg != null) {
        sb.append("Message: ");
        sb.append(msg);
      }
      sb.append("\n\nStack Trace:");
      try {
        Joiner.on("\n").appendTo(sb, e.getStackTrace());
        return ExternalErrorsUtil.get500(context, sb.toString());
      } catch (IOException ioe) {
        LOG.error("Failed to serialize exception!", e);
        try {
          throw ExternalErrorsUtil.get500(context, e.getMessage() + Arrays.toString(e
            .getStackTrace()));
        } catch (JsonProcessingException e1) {
          throw new RuntimeException("Internal server error building root error message. Please"
                                     + " report error to: errors@fineo.io", e);
        }
      }
    }
    return e;
  }
}
