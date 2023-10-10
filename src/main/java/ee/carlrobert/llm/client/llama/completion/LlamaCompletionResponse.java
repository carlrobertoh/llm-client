package ee.carlrobert.llm.client.llama.completion;

public class LlamaCompletionResponse {

  private String content;
  private boolean stop;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isStop() {
    return stop;
  }

  public void setStop(boolean stop) {
    this.stop = stop;
  }
}
