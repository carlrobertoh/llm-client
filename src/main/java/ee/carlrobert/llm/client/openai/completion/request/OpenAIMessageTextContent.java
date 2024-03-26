package ee.carlrobert.llm.client.openai.completion.request;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("text")
public class OpenAIMessageTextContent extends OpenAIMessageContent {

  private String text;

  public OpenAIMessageTextContent() {
  }

  public OpenAIMessageTextContent(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
