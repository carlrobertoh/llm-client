package ee.carlrobert.openai.client.completion.text.request;

import ee.carlrobert.openai.client.completion.CompletionRequest;
import ee.carlrobert.openai.client.completion.text.TextCompletionModel;
import java.util.Collections;
import java.util.List;

public class TextCompletionRequest extends CompletionRequest {

  private final String prompt;
  private final String model;
  private final List<String> stop;

  private TextCompletionRequest(Builder builder) {
    super(builder);
    this.model = builder.model;
    this.prompt = builder.prompt;
    this.stop = builder.stop;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getModel() {
    return model;
  }

  public List<String> getStop() {
    return stop;
  }

  public static class Builder extends CompletionRequest.Builder {

    private final String prompt;
    private String model = TextCompletionModel.DAVINCI.getCode();
    private List<String> stop;

    public Builder(String prompt) {
      this.prompt = prompt;
    }

    public Builder setModel(TextCompletionModel model) {
      this.model = model.getCode();
      return this;
    }

    public Builder setModel(String model) {
      this.model = model;
      return this;
    }

    public Builder setStop(List<String> stop) {
      this.stop = Collections.unmodifiableList(stop);
      return this;
    }

    public TextCompletionRequest build() {
      return new TextCompletionRequest(this);
    }
  }
}
