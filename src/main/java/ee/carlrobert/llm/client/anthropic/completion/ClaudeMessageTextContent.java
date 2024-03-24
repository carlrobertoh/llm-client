package ee.carlrobert.llm.client.anthropic.completion;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("text")
public class ClaudeMessageTextContent extends ClaudeMessageContent {

    private String text;

    public ClaudeMessageTextContent() {
    }

    public ClaudeMessageTextContent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
