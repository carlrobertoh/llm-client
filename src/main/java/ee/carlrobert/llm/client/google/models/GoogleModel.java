package ee.carlrobert.llm.client.google.models;

import java.util.Arrays;

public enum GoogleModel {
  GEMINI_1_0_PRO("gemini-1.0-pro", "Gemini 1.0 Pro (32k)", 30720 + 2048),
  GEMINI_1_0_PRO_001("gemini-1.0-pro-001", "Gemini 1.0 Pro 001 (32k)", 30720 + 2048),
  GEMINI_1_0_PRO_LATEST("gemini-1.0-pro-latest", "Gemini 1.0 Pro Latest (32k)", 30720 + 2048),
  GEMINI_1_0_PRO_VISION_LATEST("gemini-1.0-pro-vision-latest", "Gemini 1.0 Pro Vision Latest (16k)",
      12288 + 4096),
  GEMINI_PRO("gemini-pro", "Gemini Pro (32k)", 30720 + 2048),
  GEMINI_1_5_PRO("gemini-1.5-pro", "Gemini 1.5 Pro", 2000000 + 8192),
  GEMINI_PRO_VISION("gemini-pro-vision", "Gemini Pro Vision (16k)", 12288 + 4096),
  EMBEDDING_001("embedding-001", "Embedding 001 (2k)", 2048 + 1),
  TEXT_EMBEDDING_004("text-embedding-004", "Text Embedding (2k)", 2048 + 1);

  private final String code;
  private final String description;
  private final int maxTokens;

  GoogleModel(String code, String description, int maxTokens) {
    this.code = code;
    this.description = description;
    this.maxTokens = maxTokens;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public int getMaxTokens() {
    return maxTokens;
  }

  @Override
  public String toString() {
    return description;
  }

  public static GoogleModel findByCode(String code) {
    return Arrays.stream(GoogleModel.values())
        .filter(item -> item.getCode().equals(code))
        .findFirst().orElseThrow();
  }
}
