package ee.carlrobert.llm.client.openai.completion.request;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Base64;

/**
 * Messages with image content are supported by OpenAIs Vision models.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenAIImageUrl {

  private String url;

  private ImageDetail detail;

  public OpenAIImageUrl(String mediaType, byte[] imageData, ImageDetail detail) {
    this.setImageUrl(mediaType, imageData);
    this.detail = detail;
  }

  public OpenAIImageUrl(String mediaType, byte[] imageData) {
    this.setImageUrl(mediaType, imageData);
  }

  public OpenAIImageUrl(String imageUrl, ImageDetail detail) {
    this.url = imageUrl;
    this.detail = detail;
  }

  public OpenAIImageUrl(String imageUrl) {
    this.url = imageUrl;
  }

  public OpenAIImageUrl() {
  }

  public ImageDetail getDetail() {
    return detail;
  }

  public void setDetail(ImageDetail detail) {
    this.detail = detail;
  }


  public String getUrl() {
    return url;
  }

  /**
   * Sets the URL for the image.
   * This can be any publicly accessible URL.
   *
   * @param url URL to the image
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Sets the url in a way so that data is directly uploaded to OpenAI.
   *
   * @param mediaType media type of the image (e.g. "image/jpeg")
   * @param imageData byte data of the image
   */
  @JsonIgnore
  public void setImageUrl(String mediaType, byte[] imageData) {
    this.url = "data:" + mediaType + ";base64,"
            + Base64.getEncoder().encodeToString(imageData);
  }

  public enum ImageDetail {
    HIGH("high"),
    LOW("low"),
    @JsonEnumDefaultValue
    AUTO("auto");
    private final String detail;

    ImageDetail(String detail) {
      this.detail = detail;
    }

    @JsonValue
    public String getDetail() {
      return this.detail;
    }
  }
}
