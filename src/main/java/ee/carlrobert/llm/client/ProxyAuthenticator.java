package ee.carlrobert.llm.client;

public class ProxyAuthenticator {

  private final String username;
  private final String password;

  public ProxyAuthenticator(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
