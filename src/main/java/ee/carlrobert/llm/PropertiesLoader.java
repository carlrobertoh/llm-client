package ee.carlrobert.llm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

  public static String getValue(String key) {
    var value = (String) loadProperties().get(key);
    if (value == null) {
      return System.getProperty(key);
    }
    return value;
  }

  private static Properties loadProperties() {
    try (InputStream inputStream = PropertiesLoader.class
        .getClassLoader()
        .getResourceAsStream("application.properties")) {
      Properties configuration = new Properties();
      configuration.load(inputStream);
      return configuration;
    } catch (IOException e) {
      throw new RuntimeException("Unable to load application properties", e);
    }
  }
}
