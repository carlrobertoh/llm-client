package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.*;

import java.util.Base64;

/**
 * Represents a base64-encoded data source in Claude.
 * <br>
 * Allowed media types include the following:
 * image/jpeg, image/png, image/gif, and image/webp
 */
@JsonTypeName("base64")
public class ClaudeBase64Source extends ClaudeSource {
    @JsonProperty("media_type")
    private String mediaType;
    private byte[] data;

    public ClaudeBase64Source(String mediaType, byte[] data) {
        this.mediaType = mediaType;
        this.data = data;
    }

    public ClaudeBase64Source() {
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    @JsonIgnore
    public byte[] getData() {
        return this.data;
    }
    @JsonIgnore
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Gets the data as base64 encoded string.
     */
    @JsonGetter("data")
    public String getBase64EncodedData() {
        return Base64.getEncoder().encodeToString(this.data);
    }

    /**
     * Sets the data. Only base64 encoded strings should be used.
     */
    @JsonSetter("data")
    public void setBase64EncodedData(String base64Data) {
        this.data = Base64.getDecoder().decode(base64Data);
    }
}
