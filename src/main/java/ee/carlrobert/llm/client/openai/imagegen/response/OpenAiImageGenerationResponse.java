package ee.carlrobert.llm.client.openai.imagegen.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ee.carlrobert.llm.client.openai.completion.ErrorDetails;
import ee.carlrobert.llm.imagegen.ImageGenerationResponse;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiImageGenerationResponse implements ImageGenerationResponse {

    @JsonProperty("data")
    private final List<OpenAiImageGenerationResponseData> data;

    @JsonIgnore
    private final Date created;

    @JsonProperty("error")
    private final ErrorDetails error;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public OpenAiImageGenerationResponse(@JsonProperty("data") List<OpenAiImageGenerationResponseData> data,
                                         @JsonProperty("created") Long created,
                                         @JsonProperty("error") ErrorDetails error) {
        this.data = data;
        this.created = created == null ? null : new Date(created * 1000L); // date is given as unix timestamp
        this.error = error;
    }

    public List<OpenAiImageGenerationResponseData> getData() {
        return data;
    }

    public ErrorDetails getError() {
        return error;
    }

    @JsonIgnore
    public Date getCreated() {
        return created;
    }

    @JsonProperty("created")
    public Long getCreatedAsLong() {
        if (created == null) {
            return null;
        }
        return created.getTime() / 1000L;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OpenAiImageGenerationResponseData implements ImageGenerationResponse {
        @JsonProperty("url")
        private final String url;

        @JsonIgnore
        private final byte[] imageData;

        @JsonProperty("revised_prompt")
        private final String revisedPrompt;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public OpenAiImageGenerationResponseData(
                @JsonProperty("url") String url,
                @JsonProperty("b64_json") String base64json,
                @JsonProperty("revised_prompt") String revisedPrompt) {
            this.url = url;
            this.imageData = base64json == null ? null : Base64.getDecoder().decode(base64json);
            this.revisedPrompt = revisedPrompt;
        }

        public String getUrl() {
            return url;
        }

        @JsonIgnore
        public byte[] getImageData() {
            return this.imageData;
        }

        @JsonProperty("b64_json")
        public String getImageDataBase64() {
            return this.imageData == null ? null : Base64.getEncoder().encodeToString(this.imageData);
        }

        public String getRevisedPrompt() {
            return revisedPrompt;
        }
    }
}