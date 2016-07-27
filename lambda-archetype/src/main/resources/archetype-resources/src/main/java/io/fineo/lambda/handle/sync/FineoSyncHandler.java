package io.fineo.lambda.handle.sync;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A lambda handler that handles Kinesis events
 */
public abstract class FineoSyncHandler
  extends ThrowingRequestHandler<FineoRequest, FineoResponse> {

  private static final Logger LOG = LoggerFactory.getLogger(FineoRespondsHandler.class);

  @Inject
  public FineoRespondsHandler(Provider<?> guiceProvided) {
  }

  @Override
  public FineoResponse handle(FineoRequest input, Context context) {
    LOG.trace("Entering handler");
    return null;
  }
}
