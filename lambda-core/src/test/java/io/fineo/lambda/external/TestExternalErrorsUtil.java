package io.fineo.lambda.external;

import io.fineo.lambda.handle.external.ExternalErrorsUtil;
import org.junit.Test;

/**
 *
 */
public class TestExternalErrorsUtil {

  @Test
  public void testDoNotFailIfNoContext() throws Exception {
    ExternalErrorsUtil.getExternalError(null, 500, "type", "message");
  }
}
