package io.fineo.lambda;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class ExternalLambdaTestUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ExternalLambdaTestUtils.class);

  public static Map<String, Object> unwrapException(Exception e) throws Exception {
    if (e instanceof RuntimeException) {
      try {
        return decode(e.getMessage());
      } catch (JsonParseException | NullPointerException jpe) {
        LOG.error("Failed to read error message!");
        throw e;
      }
    }
    throw e;
  }

  private static Map<String, Object> decode(String message) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(message, Map.class);
  }

  public static Matcher<String> codedErrorMatcher(int code, String type) {
    return new BaseMatcher<String>() {
      public String errorMessage;
      public Object errorObj;
      private String message;

      @Override
      public boolean matches(Object o) {
        String msg = (String) o;
        if (msg == null) {
          message = "Null message!";
          return false;
        }

        if (!msg.startsWith("{")) {
          message = "Message not formatted correctly! Message: " + msg;
          return false;
        }

        try {
          Map<String, Object> error = decode(msg);
          if (check(error, "httpStatus", code)) {
            if (check(error, "errorType", type)) {
              return true;
            }
          }
          return false;
        } catch (IOException e) {
          message = "Failed to decode message! Cause: " + e.getMessage();
          return false;
        }
      }

      private boolean check(Map<String, Object> error, String key, Object value) {
        Object actual = error.get(key);
        if (actual == null || !actual.equals(value)) {
          errorMessage = key;
          errorObj = actual;
          return false;
        }
        return true;
      }

      @Override
      public void describeTo(Description description) {
        if (message != null) {
          description.appendText("Failed to decode message ").appendValue(message);
        } else {
          description.appendText("httpStatus should be ").appendValue(code);
          description.appendText(" and errorType should be ").appendValue(type);
        }
      }

      @Override
      public void describeMismatch(Object item, Description description) {
        if (message != null) {
          description.appendText("Failed to decode message ").appendValue(message);
        } else {
          description.appendText("\nand got:\n " + errorMessage + " was ").appendValue(errorObj);
        }
      }
    };
  }

  public static Matcher<Exception> hasErrorCodeAndeType(int code, String type) throws Exception {
    return new BaseMatcher<Exception>() {
      public String message;
      private Matcher<String> messageMatcher = codedErrorMatcher(code, type);

      @Override
      public boolean matches(Object o) {
        Exception cause = (Exception) o;
        if (cause instanceof RuntimeException) {
          return messageMatcher.matches(cause.getMessage());
        }

        message = "Must be a RuntimeException, but found: " + cause;
        return false;
      }

      @Override
      public void describeTo(Description description) {
        if (message != null) {
          description.appendText(message);
        } else {
          messageMatcher.describeTo(description);
        }
      }
    };
  }

  public static void expectError(Exception e, int code, String type) throws Exception {
    MatcherAssert.assertThat(e, hasErrorCodeAndeType(code, type));
  }
}
