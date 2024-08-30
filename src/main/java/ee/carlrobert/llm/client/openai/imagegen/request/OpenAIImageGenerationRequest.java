package ee.carlrobert.llm.client.openai.imagegen.request;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import ee.carlrobert.llm.imagegen.ImageGenerationRequest;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAIImageGenerationRequest implements ImageGenerationRequest {

  /**
   * A text description of the desired image(s). The maximum length is 4000 characters.
   */
  @JsonProperty("prompt")
  private final String prompt;


  /**
   * A text description of the desired image(s). The maximum length is 4000 characters.
   * </br>
   * Optional, default values will apply if no value is provided.
   */
  @JsonProperty("size")
  private final ImageSize size;


  /**
   * The number of images to generate.
   * </br>
   * Optional, default values will apply if no value is provided.
   */
  @JsonProperty("n")
  private final int numberOfImages;

  /**
   * The format in which the generated images are returned.
   * </br>
   * Optional, default values will apply if no value is provided.
   */
  @JsonProperty("response_format")
  private final ImagesResponseFormat responseFormat;

  /**
   * The quality of the image that will be generated.
   * </br>
   * Optional, default values will apply if no value is provided.
   */
  @JsonProperty("quality")
  private final ImageQuality quality;

  /**
   * The style of the generated images.
   * </br>
   * Optional, default values will apply if no value is provided.
   */
  @JsonProperty("style")
  private final ImageStyle style;

  /**
   * Custom request path.
   */
  @JsonIgnore
  private final String overriddenPath;




  private OpenAIImageGenerationRequest(Builder builder) {
    this.prompt = builder.prompt;
    this.size = builder.size;
    this.style = builder.style;
    this.responseFormat = builder.responseFormat;
    this.quality = builder.quality;
    this.numberOfImages = builder.numberOfImages;
    this.overriddenPath = builder.overriddenPath;
  }

  public String getPrompt() {
    return prompt;
  }

  public ImageSize getSize() {
    return size;
  }

  public int getNumberOfImages() {
    return numberOfImages;
  }

  public ImagesResponseFormat getResponseFormat() {
    return responseFormat;
  }

  public ImageQuality getQuality() {
    return quality;
  }

  public ImageStyle getStyle() {
    return style;
  }

  @JsonIgnore
  public String getOverriddenPath() {
    return this.overriddenPath;
  }


  public static class Builder {
    private String prompt;
    private ImageSize size;
    private int numberOfImages;
    private ImagesResponseFormat responseFormat;
    private ImageQuality quality;
    private ImageStyle style;
    private String overriddenPath;

    public Builder(String prompt) {
      this.prompt = prompt;
      this.numberOfImages = 1;
    }

    public Builder setNumberOfImages(int numberOfImages) {
      this.numberOfImages = numberOfImages;
      return this;
    }

    public Builder setPrompt(String prompt) {
      this.prompt = prompt;
      return this;
    }

    public Builder setSize(ImageSize size) {
      this.size = size;
      return this;
    }

    public Builder setResponseFormat(ImagesResponseFormat responseFormat) {
      this.responseFormat = responseFormat;
      return this;
    }

    public Builder setQuality(ImageQuality quality) {
      this.quality = quality;
      return this;
    }

    public Builder setStyle(ImageStyle style) {
      this.style = style;
      return this;
    }

    public OpenAIImageGenerationRequest build() {
      return new OpenAIImageGenerationRequest(this);
    }

    public void setOverriddenPath(String overriddenPath) {
        this.overriddenPath = overriddenPath;
    }
  }


  public enum ImageQuality {
    @JsonEnumDefaultValue
    STANDARD("standard"),
    HD("hd");

    private final String quality;

    ImageQuality(String detail) {
      this.quality = detail;
    }

    @JsonValue
    public String getQuality() {
      return this.quality;
    }
  }

  public enum ImageStyle {
    @JsonEnumDefaultValue
    VIVID("vivid"),
    NATURAL("natural");

    private final String style;

    ImageStyle(String detail) {
      this.style = detail;
    }

    @JsonValue
    public String getStyle() {
      return this.style;
    }
  }

  public enum ImagesResponseFormat {
    @JsonEnumDefaultValue
    URL("url"),
    BASE_64_JSON("b64_json");

    private final String responseFormat;

    ImagesResponseFormat(String detail) {
      this.responseFormat = detail;
    }

    @JsonValue
    public String getResponseFormat() {
      return this.responseFormat;
    }
  }

  public enum ImageSize {
    LANDSCAPE_1792_1024("1792x1024"),
    PORTRAIT_1024_1792("1024x1792"),
    @JsonEnumDefaultValue
    SQUARE_1024_1024("1024x1024");

    private final String size;

    ImageSize(String size) {
      this.size = size;
    }

    @JsonValue
    public String getSize() {
      return this.size;
    }
  }
}
