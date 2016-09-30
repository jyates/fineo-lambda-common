package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import static org.junit.Assert.assertEquals;

public class TestThrowingRequestHandler {

  @After
  public void clear() {
    MDC.clear();
  }

  @Test
  public void testEnabledLogging() throws Exception {
    HandlerForTesting handle = new HandlerForTesting();
    Context context = Mockito.mock(Context.class);
    String id = "requestid";
    Mockito.when(context.getAwsRequestId()).thenReturn(id);

    handle.handleRequest(new Object(), context);
    assertEquals(id, MDC.get(LambdaBaseWrapper.AWS_REQUEST_ID));
  }

  private class HandlerForTesting extends ThrowingRequestHandler<Object, Object> {

    @Override
    public void extendExternalFacingRequestHandlerInstead() {
    }

    @Override
    protected Object handle(Object o, Context context) throws Exception {
      return null;
    }
  }
}
