package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ClaudeMessageTextContent.class, name = "text"),
    @JsonSubTypes.Type(value = ClaudeMessageImageContent.class, name = "image")})
public abstract class ClaudeMessageContent {
}
