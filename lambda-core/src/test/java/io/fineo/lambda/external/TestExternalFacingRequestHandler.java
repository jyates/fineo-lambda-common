package io.fineo.lambda.external;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.fineo.lambda.ExternalLambdaTestUtils;
import io.fineo.lambda.handle.external.ExternalErrorsUtil;
import io.fineo.lambda.handle.external.ExternalFacingRequestHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.Assert.fail;

public class TestExternalFacingRequestHandler {

  public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testNoErrorMessageInExceptionCauses500() throws Exception {
    thrown.expect(RuntimeException.class);
    thrown.expectMessage(ExternalLambdaTestUtils.codedErrorMatcher(500, INTERNAL_SERVER_ERROR));
    throwOnHandle(new RuntimeException("fail"));
  }

  @Test
  public void testThrowBack400() throws Exception {
    throw40XCodeAndExpectType(0, "Bad Request", "test message");
  }

  @Test
  public void testThrowBack403() throws Exception {
    throw40XCodeAndExpectType(3, "Forbidden", "test message");
  }

  @Test
  public void testThrowBack404() throws Exception {
    throw40XCodeAndExpectType(4, "Not Found", "test message");
  }

  @Test
  public void testBadErrorCode() throws Exception {
    Context context = Mockito.mock(Context.class);
    Mockito.when(context.getAwsRequestId()).thenReturn(UUID.randomUUID().toString());

    thrown.expect(RuntimeException.class);
    thrown.expectMessage(ExternalLambdaTestUtils.codedErrorMatcher(500, INTERNAL_SERVER_ERROR));
    new ThrowingRequestHandler(() -> {
      Exception cause = null;
      try {
        // 401 is not an error code we support
        ExternalErrorsUtil.get40X(context, 1, "bad request");
        fail("Should never reach here");
      } catch (Exception e) {
        cause = e;
      }
      return cause;
    }).handleRequest(null, context);
  }

  private void throw40XCodeAndExpectType(int code, String type, String message)
    throws JsonProcessingException {
    Context context = Mockito.mock(Context.class);
    Mockito.when(context.getAwsRequestId()).thenReturn(UUID.randomUUID().toString());
    RuntimeException cause = ExternalErrorsUtil.get40X(context, code, message);

    thrown.expect(RuntimeException.class);
    thrown.expectMessage(ExternalLambdaTestUtils.codedErrorMatcher(400 + code, type));
    new ThrowingRequestHandler(cause).handleRequest(null, context);
  }

  private void throwOnHandle(Exception e) throws Exception {
    Context context = Mockito.mock(Context.class);
    Mockito.when(context.getAwsRequestId()).thenReturn(UUID.randomUUID().toString());
    new ThrowingRequestHandler(e).handleRequest(null, context);
  }

  private class ThrowingRequestHandler extends ExternalFacingRequestHandler<Object, Object> {

    private final Exception throwing;

    public ThrowingRequestHandler(Exception e) {
      this.throwing = e;
    }

    public ThrowingRequestHandler(Supplier<Exception> supplier) {
      this.throwing = supplier.get();
    }

    @Override
    protected Object handle(Object o, Context context) throws Exception {
      throw throwing;
    }
  }
}
