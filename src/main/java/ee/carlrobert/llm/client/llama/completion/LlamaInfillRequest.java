package ee.carlrobert.llm.client.llama.completion;

public class LlamaInfillRequest extends LlamaCompletionRequest {

  private final String inputPrefix;
  private final String inputSuffix;

  public LlamaInfillRequest(Builder builder, String inputPrefix, String inputSuffix) {
    super(builder);
    this.inputPrefix = inputPrefix;
    this.inputSuffix = inputSuffix;
  }

  public String getInput_prefix() {
    return inputPrefix;
  }

  public String getInput_suffix() {
    return inputSuffix;
  }
}
