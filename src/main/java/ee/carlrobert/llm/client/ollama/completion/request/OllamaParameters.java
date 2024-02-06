package ee.carlrobert.llm.client.ollama.completion.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/*
 * See <a href="https://github.com/ollama/ollama/blob/main/docs/modelfile.md#parameter">ollama/api</a>
 */
@JsonInclude(Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OllamaParameters {

  private final Integer mirostat;
  private final Double mirostatEta;
  private final Double mirostatTau;
  private final Integer numCtx;
  private final Integer numGqa;
  private final Integer numGpu;
  private final Integer numThread;
  private final Integer repeatLastN;
  private final Double repeatPenalty;
  private final Double temperature;
  private final Integer seed;
  private final String stop;
  private final Double tfsZ;
  private final Integer numPredict;
  private final Integer topK;
  private final Double topP;

  public OllamaParameters(Builder builder) {
    this.mirostat = builder.mirostat;
    this.mirostatEta = builder.mirostatEta;
    this.mirostatTau = builder.mirostatTau;
    this.numCtx = builder.numCtx;
    this.numGqa = builder.numGqa;
    this.numGpu = builder.numGpu;
    this.numThread = builder.numThread;
    this.repeatLastN = builder.repeatLastN;
    this.repeatPenalty = builder.repeatPenalty;
    this.temperature = builder.temperature;
    this.seed = builder.seed;
    this.stop = builder.stop;
    this.tfsZ = builder.tfsZ;
    this.numPredict = builder.numPredict;
    this.topK = builder.topK;
    this.topP = builder.topP;
  }

  public Integer getMirostat() {
    return mirostat;
  }

  public Double getMirostatEta() {
    return mirostatEta;
  }

  public Double getMirostatTau() {
    return mirostatTau;
  }

  public Integer getNumCtx() {
    return numCtx;
  }

  public Integer getNumGqa() {
    return numGqa;
  }

  public Integer getNumGpu() {
    return numGpu;
  }

  public Integer getNumThread() {
    return numThread;
  }

  public Integer getRepeatLastN() {
    return repeatLastN;
  }

  public Double getRepeatPenalty() {
    return repeatPenalty;
  }

  public Double getTemperature() {
    return temperature;
  }

  public Integer getSeed() {
    return seed;
  }

  public String getStop() {
    return stop;
  }

  public Double getTfsZ() {
    return tfsZ;
  }

  public Integer getNumPredict() {
    return numPredict;
  }

  public Integer getTopK() {
    return topK;
  }

  public Double getTopP() {
    return topP;
  }

  public static class Builder {

    private Integer mirostat;
    private Double mirostatEta;
    private Double mirostatTau;
    private Integer numCtx;
    private Integer numGqa;
    private Integer numGpu;
    private Integer numThread;
    private Integer repeatLastN;
    private Double repeatPenalty;
    private Double temperature;
    private Integer seed;
    private String stop;
    private Double tfsZ;
    private Integer numPredict;
    private Integer topK;
    private Double topP;

    public Builder() {
      // Default values
    }

    public Builder mirostat(Integer mirostat) {
      this.mirostat = mirostat;
      return this;
    }

    public Builder mirostatEta(Double mirostatEta) {
      this.mirostatEta = mirostatEta;
      return this;
    }

    public Builder mirostatTau(Double mirostatTau) {
      this.mirostatTau = mirostatTau;
      return this;
    }

    public Builder numCtx(Integer numCtx) {
      this.numCtx = numCtx;
      return this;
    }

    public Builder numGqa(Integer numGqa) {
      this.numGqa = numGqa;
      return this;
    }

    public Builder numGpu(Integer numGpu) {
      this.numGpu = numGpu;
      return this;
    }

    public Builder numThread(Integer numThread) {
      this.numThread = numThread;
      return this;
    }

    public Builder repeatLastN(Integer repeatLastN) {
      this.repeatLastN = repeatLastN;
      return this;
    }

    public Builder repeatPenalty(Double repeatPenalty) {
      this.repeatPenalty = repeatPenalty;
      return this;
    }

    public Builder temperature(Double temperature) {
      this.temperature = temperature;
      return this;
    }

    public Builder seed(Integer seed) {
      this.seed = seed;
      return this;
    }

    public Builder stop(String stop) {
      this.stop = stop;
      return this;
    }

    public Builder tfsZ(Double tfsZ) {
      this.tfsZ = tfsZ;
      return this;
    }

    public Builder numPredict(Integer numPredict) {
      this.numPredict = numPredict;
      return this;
    }

    public Builder topK(Integer topK) {
      this.topK = topK;
      return this;
    }

    public Builder topP(Double topP) {
      this.topP = topP;
      return this;
    }

    public OllamaParameters build() {
      return new OllamaParameters(this);
    }
  }
}
