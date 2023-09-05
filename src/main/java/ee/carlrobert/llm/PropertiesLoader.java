package ee.carlrobert.llm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

  public static Properties loadProperties() {
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

  public static String getValue(String key) {
    return (String) loadProperties().get(key);
  }
}
