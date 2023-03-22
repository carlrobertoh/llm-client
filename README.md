<a name="readme-top"></a>
# OpenAI Client
Java http client wrapped around the OkHttp3 library, providing an easy way to access **OpenAI api** interface.

[![Contributions welcome][contributions-welcome-svg]][contributions-welcome]
[![Maven][maven-shield]][maven-url]

## Installation
To use the package, you need to use following Maven dependency:

```xml
<dependency>
    <groupId>ee.carlrobert</groupId>
    <artifactId>openai-client</artifactId>
    <version>1.0.3</version>
</dependency>
```
Gradle dependency:
```kts
dependencies {
  implementation("ee.carlrobert:openai-client:1.0.3")
}
```

## Usage

### Client Builder
```java
var builder = new OpenAIClient.Builder("sk-")
    .setConnectTimeout(60L, TimeUnit.SECONDS)
    .setReadTimeout(30L, TimeUnit.SECONDS)
    .setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1234)))
    .setProxyAuthenticator(new ProxyAuthenticator("proxyUsername", "proxyPassword"));
```

#### Billing client
```java
BillingClient billingClient = clientBuilder.buildBillingClient();
billingClient.getCreditUsageAsync(creditUsage -> {
  System.out.println(creditUsage.getTotalGranted());
  System.out.println(creditUsage.getTotalUsed());
});
```

#### Chat Completion SSE
```java
ChatCompletionClient completionClient = clientBuilder.buildChatCompletionClient();
ChatCompletionRequest request = new ChatCompletionRequest.Builder(List.of(new ChatCompletionMessage("user", "Hi there!")))
    .setModel(ChatCompletionModel.GPT_4)
    .build();
EventSource call = completionClient.stream(request, new CompletionEventListener() {
  @Override
  public void onMessage(String message) {
    System.out.println(message);
  }
});
call.cancel();
```

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributions-welcome-svg]: http://img.shields.io/badge/contributions-welcome-brightgreen
[contributions-welcome]: https://github.com/JetBrains/ideavim/blob/master/CONTRIBUTING.md
[maven-shield]: https://img.shields.io/maven-central/v/ee.carlrobert/openai-client
[maven-url]: https://central.sonatype.com/namespace/ee.carlrobert
[portfolio]: https://carlrobert.ee
