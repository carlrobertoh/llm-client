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
    <version>1.0.5</version>
</dependency>
```
Gradle dependency:
```kts
dependencies {
  implementation("ee.carlrobert:openai-client:1.0.5")
}
```

## Usage

### Client Builder
```java
var builder = new OpenAIClient.Builder(System.getenv("MY_SECRET_KEY"))
    .setConnectTimeout(60L, TimeUnit.SECONDS)
    .setReadTimeout(30L, TimeUnit.SECONDS)
    .setProxy(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1234)))
    .setProxyAuthenticator(new ProxyAuthenticator("proxyUsername", "proxyPassword"));
```

#### Dashboard client
```java
DashboardClient dashboardClient = clientBuilder.buildDashboardClient();
dashboardClient.getSubscriptionAsync(subscription -> {
  System.out.println(subscription.getAccountName());
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

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This is an unofficial OpenAI library and is not associated with or endorsed by OpenAI.

MIT © [Carl-Robert Linnupuu][portfolio]


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributions-welcome-svg]: http://img.shields.io/badge/contributions-welcome-brightgreen
[contributions-welcome]: https://github.com/JetBrains/ideavim/blob/master/CONTRIBUTING.md
[maven-shield]: https://img.shields.io/maven-central/v/ee.carlrobert/openai-client
[maven-url]: https://central.sonatype.com/namespace/ee.carlrobert
[portfolio]: https://carlrobert.ee
