package ee.carlrobert.llm.client.you.completion;

public class YouCompletionRequestMessage {

  private final String question;
  private final String answer;

  public YouCompletionRequestMessage(String question, String answer) {
    this.question = question;
    this.answer = answer;
  }

  public String getQuestion() {
    return question;
  }

  public String getAnswer() {
    return answer;
  }
}
