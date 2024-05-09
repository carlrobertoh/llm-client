<a name="readme-top"></a>
# CodeGPT - LLM Client
A user-friendly Java HTTP client, providing access to the large language model (LLM) APIs and services.

[![Contributions welcome][contributions-welcome-svg]][contributions-welcome]
[![Maven][maven-shield]][maven-url]

## Installation
To use the package, you need to use following Maven dependency:

```xml
<dependency>
    <groupId>ee.carlrobert</groupId>
    <artifactId>llm-client</artifactId>
    <version>0.8.2</version>
</dependency>
```
Gradle dependency:
```kts
dependencies {
  implementation("ee.carlrobert:llm-client:0.8.2")
}
```

## Usage

### Chat Completion SSE
```java
OpenAIClient client = new OpenAIClient.Builder(System.getenv("OPENAI_API_KEY"))
    .setOrganization("MY_ORGANIZATION")
    .build();

EventSource call = client.getChatCompletionAsync(
    new OpenAIChatCompletionRequest.Builder(List.of(new OpenAIChatCompletionStandardMessage("user", prompt)))
        .setModel(OpenAIChatCompletionModel.GPT_4)
        .setTemperature(0.1)
        .build(),
    new CompletionEventListener<String>(){
      @Override
      public void onMessage(String message, EventSource eventSource) {
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
[maven-shield]: https://img.shields.io/maven-central/v/ee.carlrobert/llm-client
[maven-url]: https://central.sonatype.com/namespace/ee.carlrobert
[portfolio]: https://carlrobert.ee
