package io.fineo.lambda.handle;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestLambdaWrapper {

  @After
  public void clear() {
    MDC.clear();
  }

  @Test
  public void testEnableLogging() throws Exception {
    LambdaWrapperForTest lambda = new LambdaWrapperForTest();
    Context context = Mockito.mock(Context.class);
    String id = "requestid";
    Mockito.when(context.getAwsRequestId()).thenReturn(id);
    lambda.handle(new Object(), context);
    assertEquals(id, MDC.get(LambdaBaseWrapper.AWS_REQUEST_ID));
  }

  private static class LambdaWrapperForTest extends LambdaWrapper<Object, ObjectHandler> {

    public LambdaWrapperForTest() {
      super(ObjectHandler.class, Arrays.asList());
    }

    @Override
    public void handle(Object event, Context context) throws IOException {
      handleInternal(event, context);
    }
  }

  public static class ObjectHandler implements LambdaHandler<Object> {
    @Override
    public void handle(Object event) throws IOException {
      // noop
    }
  }
}
