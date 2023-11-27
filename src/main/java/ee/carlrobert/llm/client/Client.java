package ee.carlrobert.llm.client;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class Client {

  final OkHttpClient httpClient;
  final String apiKey;
  final String host;
  final Proxy proxy;
  final ProxyAuthenticator proxyAuthenticator;
  final Long connectTimeout;
  final TimeUnit connectTimeoutUnit;
  final Long readTimeout;
  final TimeUnit readTimeoutUnit;
  final boolean retryOnReadTimeout;

  public OkHttpClient getHttpClient() {
    return httpClient;
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getHost() {
    return host;
  }

  public boolean isRetryOnReadTimeout() {
    return retryOnReadTimeout;
  }

  protected Client(Builder builder) {
    this.httpClient = builder.buildHttpClient();
    this.apiKey = builder.apiKey;
    this.host = builder.host;
    this.proxy = builder.proxy;
    this.proxyAuthenticator = builder.proxyAuthenticator;
    this.connectTimeout = builder.connectTimeout;
    this.connectTimeoutUnit = builder.connectTimeoutUnit;
    this.readTimeout = builder.readTimeout;
    this.readTimeoutUnit = builder.readTimeoutUnit;
    this.retryOnReadTimeout = builder.retryOnReadTimeout;
  }

  public static class Builder {

    private final String apiKey;
    private String host;
    private Proxy proxy;
    private ProxyAuthenticator proxyAuthenticator;
    private Long connectTimeout;
    private TimeUnit connectTimeoutUnit;
    private Long readTimeout;
    private TimeUnit readTimeoutUnit;
    private boolean retryOnReadTimeout;
    private Interceptor interceptor;

    public Builder() {
      this(null);
    }

    public Builder(String apiKey) {
      this.apiKey = apiKey;
    }

    public Builder setHost(String host) {
      this.host = host;
      return this;
    }

    public Builder setProxy(Proxy proxy) {
      this.proxy = proxy;
      return this;
    }

    public Builder setProxyAuthenticator(ProxyAuthenticator proxyAuthenticator) {
      this.proxyAuthenticator = proxyAuthenticator;
      return this;
    }

    public Builder setConnectTimeout(Long timeout, TimeUnit unit) {
      this.connectTimeout = timeout;
      this.connectTimeoutUnit = unit;
      return this;
    }

    public Builder setReadTimeout(Long timeout, TimeUnit unit) {
      this.readTimeout = timeout;
      this.readTimeoutUnit = unit;
      return this;
    }

    public Builder setRetryOnReadTimeout(boolean retryOnReadTimeout) {
      this.retryOnReadTimeout = retryOnReadTimeout;
      return this;
    }

    public Builder setInterceptor(Interceptor interceptor) {
      this.interceptor = interceptor;
      return this;
    }

    public Client build() {
      return new Client(this);
    }

    public OkHttpClient buildHttpClient() {
      OkHttpClient.Builder builder = new OkHttpClient.Builder();

      if (connectTimeout != null && connectTimeoutUnit != null) {
        builder.connectTimeout(connectTimeout, connectTimeoutUnit);
      }
      if (readTimeout != null && readTimeoutUnit != null) {
        builder.readTimeout(readTimeout, readTimeoutUnit);
      }
      if (interceptor != null) {
        builder.addInterceptor(interceptor);
      }

      if (proxy != null) {
        builder.proxy(proxy);

        if (proxyAuthenticator != null) {
          builder.proxyAuthenticator((route, response) ->
              response.request()
                  .newBuilder()
                  .header("Proxy-Authorization", Credentials.basic(
                      proxyAuthenticator.getUsername(),
                      proxyAuthenticator.getPassword()))
                  .build());
        }
      }
      return builder.build();
    }
  }
}
