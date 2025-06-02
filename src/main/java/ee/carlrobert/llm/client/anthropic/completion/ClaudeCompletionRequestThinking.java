package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClaudeCompletionRequestThinking {

  private String type;
  @JsonProperty("budget_tokens")
  private long budgetTokens;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public long getBudgetTokens() {
    return budgetTokens;
  }

  public void setBudgetTokens(long budgetTokens) {
    this.budgetTokens = budgetTokens;
  }
}
