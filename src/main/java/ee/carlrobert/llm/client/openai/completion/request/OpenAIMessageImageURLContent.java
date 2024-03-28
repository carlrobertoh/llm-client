package ee.carlrobert.llm.client.openai.completion.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Messages with image content are supported by OpenAIs vision models.
 */
@JsonTypeName("image_url")
public class OpenAIMessageImageURLContent extends OpenAIMessageContent {


  @JsonProperty("image_url")
  private OpenAIImageUrl imageUrl;


  public OpenAIMessageImageURLContent() {
  }

  public OpenAIMessageImageURLContent(OpenAIImageUrl imageUrl) {
    this.imageUrl = imageUrl;
  }


  public OpenAIImageUrl getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(OpenAIImageUrl imageUrl) {
    this.imageUrl = imageUrl;
  }
}
