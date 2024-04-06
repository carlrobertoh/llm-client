package ee.carlrobert.llm.client.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;

import static ee.carlrobert.llm.client.DeserializationUtil.OBJECT_MAPPER;

public class JSONUtil {

  @SafeVarargs
  public static String jsonMapResponse(Map.Entry<String, ?>... entries) {
    try {
      return OBJECT_MAPPER.writeValueAsString(Map.ofEntries(entries));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to map to json string", e);
    }
  }

  public static String jsonMapResponse(String key, Object value) {
    return jsonMapResponse(e(key, value));
  }

  @SafeVarargs
  public static Map<String, ?> jsonMap(Map.Entry<String, ?>... entries) {
    return Map.ofEntries(entries);
  }

  public static Map<String, ?> jsonMap(String key, Object value) {
    return jsonMap(e(key, value));
  }

  @SafeVarargs
  public static List<?> jsonArray(Map<String, ?>... objects) {
    return List.of(objects);
  }

  public static Map.Entry<String, ?> e(String key, Object value) {
    return Map.entry(key, value);
  }
}
