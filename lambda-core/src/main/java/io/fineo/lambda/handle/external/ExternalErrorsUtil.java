package io.fineo.lambda.handle.external;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ExternalErrorsUtil {
  private ExternalErrorsUtil() {
  }

  public static RuntimeException get40X(Context context, int code, String message)
    throws JsonProcessingException {
    String type = null;
    switch (code) {
      case 0:
        type = "Bad Request";
        break;
      case 3:
        type = "Forbidden";
        break;
      case 4:
        type = "Not Found";
        break;
      default:
        throw new RuntimeException(
          "Could not determine error message type. Expected: 40" + code + ": " + message);
    }
    return getExternalError(context, 400 + code, type, message);
  }

  public static RuntimeException get500(Context context, String message)
    throws JsonProcessingException {
    return getExternalError(context, 500, "Internal Server Error", message);
  }

  public static RuntimeException getExternalError(Context context, int code, String type,
    String message) throws JsonProcessingException {
    Map<String, Object> errorPayload = new HashMap();
    errorPayload.put("errorType", type);
    errorPayload.put("httpStatus", code);
    if (context == null) {
      System.err.println("Context is null. Generally this should only happen in tests. If you are"
                         + " seeing this message in production, something has gone terribly "
                         + "terribly wrong");
      errorPayload.put("requestId", "--- missing ---");
    } else {
      errorPayload.put("requestId", context.getAwsRequestId());
    }
    errorPayload.put("message", message);
    return new RuntimeException(new ObjectMapper().writeValueAsString(errorPayload));
  }
}
