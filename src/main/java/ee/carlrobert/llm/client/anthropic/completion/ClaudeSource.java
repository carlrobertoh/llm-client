package ee.carlrobert.llm.client.anthropic.completion;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClaudeBase64Source.class, name = "base64")})
public abstract class ClaudeSource {

}
