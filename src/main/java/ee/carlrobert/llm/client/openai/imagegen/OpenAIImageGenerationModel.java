package ee.carlrobert.llm.client.openai.imagegen;

import ee.carlrobert.llm.imagegen.ImageGenerationModel;
import java.util.Arrays;

public enum OpenAIImageGenerationModel implements ImageGenerationModel {
  DALL_E_2("dall-e-2", "DALL·E 2"),
  DALL_E_3("dall-e-3", "DALL·E 3");
  private final String code;
  private final String description;

  OpenAIImageGenerationModel(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }


  @Override
  public String toString() {
    return description;
  }

  public static OpenAIImageGenerationModel findByCode(String code) {
    return Arrays.stream(OpenAIImageGenerationModel.values())
        .filter(item -> item.getCode().equals(code))
        .findFirst().orElseThrow();
  }
}

