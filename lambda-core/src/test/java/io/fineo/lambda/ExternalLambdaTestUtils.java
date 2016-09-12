package io.fineo.lambda;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ExternalLambdaTestUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ExternalLambdaTestUtils.class);

  public static Map<String, Object> unwrapException(Exception e) throws Exception {
    if (e instanceof RuntimeException) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        return mapper.readValue(e.getMessage(), Map.class);
      } catch (JsonParseException | NullPointerException jpe) {
        LOG.error("Failed to read error message!");
        throw e;
      }
    }

    throw e;
  }

  public static void expectError(Exception e, int code, String type) throws Exception {
    Map<String, Object> error = unwrapException(e);
    assertEquals("Got error: " + error, code, error.get("httpStatus"));
    assertEquals("Got error: " + error, type, error.get("errorType"));
  }
}
