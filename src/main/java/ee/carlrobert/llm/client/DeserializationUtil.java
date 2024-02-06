package ee.carlrobert.llm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.Response;

public class DeserializationUtil {

  public static <T> T mapResponse(Response response, Class<T> clazz) {
    var body = response.body();
    if (body == null) {
      return null;
    }

    try {
      return new ObjectMapper().readValue(body.string(), clazz);
    } catch (IOException ex) {
      throw new RuntimeException("Could not deserialize response", ex);
    }
  }
}
