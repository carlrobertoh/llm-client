package ee.carlrobert.llm.client.openai.completion.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OpenAIMessageTextContent.class, name = "text"),
    @JsonSubTypes.Type(value = OpenAIMessageImageURLContent.class, name = "image_url")})
public abstract class OpenAIMessageContent {
}
