package io.fineo.lambda.handle;

import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A lambda handler that handles Kinesis events
 */
public abstract class FineoRespondsHandler implements RequesHandler<FineoRequest, FineoResponse> {

  private static final Logger LOG = LoggerFactory.getLogger(FineoRespondsHandler.class);

  public FineoRespondsHandler(Provider<?> guiceProvided) {
  }

  @Override
  public FineoResponse handleRequest(FineoRequest input, Context context) {
    LOG.trace("Entering handler");
    return null;
  }
}
