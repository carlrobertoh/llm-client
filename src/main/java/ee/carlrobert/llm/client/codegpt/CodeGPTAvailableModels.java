package ee.carlrobert.llm.client.codegpt;

import ee.carlrobert.llm.client.codegpt.CodeGPTModel.ModelType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class CodeGPTAvailableModels {

  public static final List<CodeGPTModel> AVAILABLE_CHAT_MODELS = List.of(
      new CodeGPTModel(
          "Llama 3 (70B)",
          "meta-llama/Llama-3-70b-chat-hf",
          ModelType.CHAT),
      new CodeGPTModel(
          "Llama 3 (8B)",
          "meta-llama/Llama-3-8b-chat-hf",
          ModelType.CHAT),
      new CodeGPTModel(
          "Code Llama (70B)",
          "codellama/CodeLlama-70b-Instruct-hf",
          ModelType.CHAT),
      new CodeGPTModel(
          "Code Llama (13B)",
          "codellama/CodeLlama-13b-Instruct-hf",
          ModelType.CHAT),
      new CodeGPTModel(
          "Mixtral (8x22B)",
          "mistralai/Mixtral-8x22B-Instruct-v0.1",
          ModelType.CHAT),
      new CodeGPTModel(
          "DBRX (132B)",
          "databricks/dbrx-instruct",
          ModelType.CHAT),
      new CodeGPTModel(
          "DeepSeek Coder (33B)",
          "deepseek-ai/deepseek-coder-33b-instruct",
          ModelType.CHAT),
      new CodeGPTModel(
          "WizardLM-2 (8x22B)",
          "microsoft/WizardLM-2-8x22B",
          ModelType.CHAT)
  );

  public static final List<CodeGPTModel> AVAILABLE_CODE_MODELS = List.of(
      new CodeGPTModel(
          "Code Llama (70B)",
          "codellama/CodeLlama-70b-hf",
          ModelType.CODE),
      new CodeGPTModel(
          "Code Llama Python (70B)",
          "codellama/CodeLlama-70b-Python-hf",
          ModelType.CODE),
      new CodeGPTModel(
          "Code Llama Python (7B)",
          "codellama/CodeLlama-7b-Python-hf",
          ModelType.CODE),
      new CodeGPTModel(
          "WizardCoder Python (34B)",
          "WizardLM/WizardCoder-Python-34B-V1.0",
          ModelType.CODE),
      new CodeGPTModel(
          "Phind Code LLaMA v2 (34B)",
          "Phind/Phind-CodeLlama-34B-v2",
          ModelType.CODE)
  );

  public static CodeGPTModel findByCode(String code) {
    return Stream.of(AVAILABLE_CHAT_MODELS, AVAILABLE_CODE_MODELS)
        .flatMap(Collection::stream)
        .filter(it -> it.getCode().equals(code))
        .findFirst()
        .orElse(null);
  }
}
