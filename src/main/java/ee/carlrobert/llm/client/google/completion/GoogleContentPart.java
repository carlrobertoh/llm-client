package ee.carlrobert.llm.client.google.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class GoogleContentPart {

  private String text;
  private Blob inlineData;

  public GoogleContentPart() {
  }

  public GoogleContentPart(String text) {
    this(text, null);
  }

  public GoogleContentPart(String text, Blob inlineData) {
    this.text = text;
    this.inlineData = inlineData;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Blob getInlineData() {
    return inlineData;
  }

  public void setInlineData(Blob inlineData) {
    this.inlineData = inlineData;
  }

  /**
   * <a href="https://ai.google.dev/api/rest/v1/Content?authuser=1#Blob">Blob</a>.
   */
  public static class Blob {

    private String mimeType;
    private String data;

    public String getMimeType() {
      return mimeType;
    }

    public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }
  }
}
