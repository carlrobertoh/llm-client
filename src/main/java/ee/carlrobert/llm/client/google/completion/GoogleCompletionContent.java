package ee.carlrobert.llm.client.google.completion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(Include.NON_NULL)
public class GoogleCompletionContent {

  private List<GoogleContentPart> parts;
  private String role;

  public GoogleCompletionContent() {
  }

  public GoogleCompletionContent(List<String> texts) {
    this(null, texts);
  }

  public GoogleCompletionContent(String role, List<String> texts) {
    this.parts = texts.stream().map(GoogleContentPart::new).collect(Collectors.toList());
    this.role = role;
  }

  public GoogleCompletionContent(List<GoogleContentPart> parts, String role) {
    this.parts = parts;
    this.role = role;
  }

  public List<GoogleContentPart> getParts() {
    return parts;
  }

  public void setParts(
      List<GoogleContentPart> parts) {
    this.parts = parts;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
