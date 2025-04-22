# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.8.40] - 2025-04-21

### Added

- Google Gemini 2.5 Pro Preview and Gemini 2.5 Flash Preview models
- OpenAI gpt-4.1, gpt-4.1-mini, gpt-4.1-nano models

## [0.8.39] - 2025-04-21

### Fixed

- Google response parsing and reasoning support

## [0.8.38] - 2025-04-01

### Added

- Latest Gemini 2.5 Pro (exp) model (Google)

## [0.8.37] - 2025-02-11

### Added

- Latest Gemini models: 2.0 Pro (exp), 2.0 Flash Thinking (exp), 2.0 Flash (Google)

## [0.8.36] - 2025-02-05

### Added

- `reasoning_effort` chat request parameter (OpenAI)

## [0.8.35] - 2025-02-03

### Added

- Avatar image for chats (CodeGPT)

## [0.8.34] - 2025-02-02

### Added

- o3-mini model (OpenAI)

## [0.8.33] - 2025-01-06

### Added

- Paste Predictions API support (CodeGPT)

## [0.8.32] - 2024-12-26

### Added

- Predictions API support (CodeGPT)

## [0.8.31] - 2024-12-09

### Added

- rewrite_speculation API field

## [0.8.30] - 2024-12-01

### Added

- Cursor offset API field (CodeGPT)

## [0.8.29] - 2024-11-30

### Added

- Stop tokens field (CodeGPT)

## [0.8.28] - 2024-11-14

### Improved

- Completions error handling (CodeGPT)

## [0.8.27] - 2024-11-06

### Added

- Missing stop and prediction properties (OpenAI, Anthropic)

## [0.8.26] - 2024-11-04

### Fixed

- Error handling for non-stream chat completions (CodeGPT, OpenAI)

## [0.8.25] - 2024-11-03

### Added

- Missing `onOpen` event callback (Ollama) 

## [0.8.24] - 2024-10-30

- Support other embedding models (OpenAI)

## [0.8.23] - 2024-10-25

- Auto Apply integration (CodeGPT)

## [0.8.22] - 2024-10-09

### Added

- Gemini 1.5 Pro model (Google)
- Separate class for chat completions request body, breaking change (CodeGPT)

## [0.8.21] - 2024-10-07

### Added

- New context and plugin version request body params (CodeGPT)

## [0.8.20] - 2024-10-01

### Added

- Support for OpenAI completion response usage

## [0.8.19] - 2024-10-01

### Added

- o1 models (OpenAI)

## [0.8.18] - 2024-09-23

### Added

- Missing implementations for request/response concrete classes

## [0.8.17] - 2024-09-12

### Fixed

- Ollama host overriding

## [0.8.16] - 2024-09-05

### Fixed

- Ollama auth

## [0.8.15] - 2024-09-05

### Added

- Azure and OpenAI image generation support

### Fixed

- Ollama streaming completion requests

## [0.8.14] - 2024-08-26

### Added

- Support for suffix in OllamaCompletionRequest
- Support for stop param in OpenAITextCompletionRequest

## [0.8.13] - 2024-08-26

### Added

- New code completion API integration (CodeGPT)

## [0.8.12] - 2024-08-12

### Added

- Documentation suggestion support (CodeGPT)

## [0.8.11] - 2024-07-29

### Added

- Web search support (CodeGPT)

## [0.8.10] - 2024-07-19

### Added

- New gpt-4o-mini model (OpenAI)

## [0.8.9] - 2024-06-20

### Added

- Available model missing fields (CodeGPT)

## [0.8.8] - 2024-06-17

### Added

- Ollama api key configuration support

## [0.8.7] - 2024-06-09

### Added

- Support for listening request open events

## [0.8.6] - 2024-05-28

### Added

- Support for fetching user details (CodeGPT)
- Support for overriding base host (Claude) 

## [0.8.5] - 2024-05-13

### Added

- GPT-4o model (OpenAI)

## [0.8.4] - 2024-05-13

### Removed

- Available CodeGPT models

## [0.8.3] - 2024-05-10

### Added

- Llama 3 (8B) model (CodeGPT)

## [0.8.2] - 2024-05-09

### Added

- Support for non-stream chat completions (CodeGPT)

## [0.8.1] - 2024-05-08

### Added

- Support for testing CodeGPT client

## [0.8.0] - 2024-05-08

### Added

- Support for Gemini models via Google's API

## [0.7.5] - 2024-05-03

### Improved

- CodeGPT Client error handling

## [0.7.4] - 2024-04-28

### Added

- Initial CodeGPT Client implementation

### Removed

- Together.ai client

## [0.7.3] - 2024-04-27

### Added

- Together.ai client
- Separate Ollama completion and chat completion API 

## [0.7.2] - 2024-04-15

### Fixed

- Anomalies upon streaming

## [0.7.1] - 2024-04-12

### Added

- New 'response_format' OpenAI chat completions body param

### Fixed

- Azure base url construction
- Completion's response choice handling

## [0.7.0] - 2024-04-01

### Added

- Vision support for OpenAI, Azure and Anthropic(Claude)

## [0.6.2] - 2024-03-08

### Added

- Support for You Pro modes

## [0.6.1] - 2024-03-06

### Fixed

- Anthropic Claude completion error handling

## [0.6.0] - 2024-03-06

### Added

- Anthropic Claude client

### Fixed

- `IndexOutOfBoundsException` for Azure completions

## [0.5.0] - 2024-02-23

### Added

- Access to EventSource on message callbacks (breaking change)

## [0.4.3] - 2024-02-09

### Changed

- Ollama stop parameter type

## [0.4.2] - 2024-02-06

### Changed

- Restore OpenAI legacy models to ensure backward compatibility

## [0.4.1] - 2024-02-06

### Changed

- GPT-3.5 Turbo model description

## [0.4.0] - 2024-02-06

### Added

- Ollama API client
- Changelog

### Changed

- Upgrade OpenAI chat models: **gpt-4-0125-preview**, **gpt-3.5-turbo-0125**

[0.8.39]: https://github.com/carlrobertoh/llm-client/compare/f0586cc8adc182a253c96c53bd8c8aa94d286401...HEAD
[0.8.38]: https://github.com/carlrobertoh/llm-client/compare/87c3dc1e0501a07ddb877f45d4a8cbf1120a3a4e...f0586cc8adc182a253c96c53bd8c8aa94d286401
[0.8.37]: https://github.com/carlrobertoh/llm-client/compare/d97cb4865c8502f7fb5e62d1f96f4631535675bd...87c3dc1e0501a07ddb877f45d4a8cbf1120a3a4e
[0.8.36]: https://github.com/carlrobertoh/llm-client/compare/216c4ee6c2a9477bc51db184df07f2b47544edd1...d97cb4865c8502f7fb5e62d1f96f4631535675bd
[0.8.35]: https://github.com/carlrobertoh/llm-client/compare/4a99162ee5cb5ba9068f5b966cf1ead71d82e39d...216c4ee6c2a9477bc51db184df07f2b47544edd1
[0.8.34]: https://github.com/carlrobertoh/llm-client/compare/6a61d6fa69b50707fca12a260748629bf45bb353...4a99162ee5cb5ba9068f5b966cf1ead71d82e39d
[0.8.33]: https://github.com/carlrobertoh/llm-client/compare/43b11112a443bbbdb9b057423173bb10be002f9d...6a61d6fa69b50707fca12a260748629bf45bb353
[0.8.32]: https://github.com/carlrobertoh/llm-client/compare/a6bef6a750aec3dd735b5b2bd1758efa763ee075...43b11112a443bbbdb9b057423173bb10be002f9d
[0.8.31]: https://github.com/carlrobertoh/llm-client/compare/158ca83fee59bbc1b01e8bdd6666dc5276562a1e...a6bef6a750aec3dd735b5b2bd1758efa763ee075
[0.8.30]: https://github.com/carlrobertoh/llm-client/compare/80fa1759a7ef6818204f9c351ad777e4e53a7666...158ca83fee59bbc1b01e8bdd6666dc5276562a1e
[0.8.29]: https://github.com/carlrobertoh/llm-client/compare/b50ad56c96078a85605cbe130ea171a1d8476aa1...80fa1759a7ef6818204f9c351ad777e4e53a7666
[0.8.28]: https://github.com/carlrobertoh/llm-client/compare/c6d311690a6a47f4a8cad521118b37ead11eca39...b50ad56c96078a85605cbe130ea171a1d8476aa1
[0.8.27]: https://github.com/carlrobertoh/llm-client/compare/84b2d6204381e7eb4aa7c0504b7f0c46ee87f880...c6d311690a6a47f4a8cad521118b37ead11eca39
[0.8.26]: https://github.com/carlrobertoh/llm-client/compare/a315976138c73fbfcc00dfdcfa6ffbea01c0c6dd...84b2d6204381e7eb4aa7c0504b7f0c46ee87f880
[0.8.25]: https://github.com/carlrobertoh/llm-client/compare/aad0d7187cbc1d2d1f46efd0f0d929ca74357764...a315976138c73fbfcc00dfdcfa6ffbea01c0c6dd
[0.8.24]: https://github.com/carlrobertoh/llm-client/compare/9c8a34339402d765d83f50273b1fa02c2a4a4b2c...aad0d7187cbc1d2d1f46efd0f0d929ca74357764
[0.8.23]: https://github.com/carlrobertoh/llm-client/compare/488583ec3c6b47d75d309bd9381a048743f9c891...9c8a34339402d765d83f50273b1fa02c2a4a4b2c
[0.8.22]: https://github.com/carlrobertoh/llm-client/compare/cc6299c71e18c7c52665ff49eba78f357402fe78...488583ec3c6b47d75d309bd9381a048743f9c891
[0.8.21]: https://github.com/carlrobertoh/llm-client/compare/b492dc4a2dabca6467985e464b25ab56eb30fc48...cc6299c71e18c7c52665ff49eba78f357402fe78
[0.8.20]: https://github.com/carlrobertoh/llm-client/compare/59a92c5d9db18856b9c35a70e5f2ebe8d23ea29e...b492dc4a2dabca6467985e464b25ab56eb30fc48
[0.8.19]: https://github.com/carlrobertoh/llm-client/compare/933b363c24947884b0fcf7fc9be7c9f34d3b2d33...59a92c5d9db18856b9c35a70e5f2ebe8d23ea29e
[0.8.18]: https://github.com/carlrobertoh/llm-client/compare/0940ecb8e83956c273f62e63f007b4f1bc4550cb...933b363c24947884b0fcf7fc9be7c9f34d3b2d33
[0.8.17]: https://github.com/carlrobertoh/llm-client/compare/6b7e26477b8e3454e78c8c639e97c8803fa5a301...0940ecb8e83956c273f62e63f007b4f1bc4550cb
[0.8.16]: https://github.com/carlrobertoh/llm-client/compare/d714854331915387da583c9a5b24877cc06286e...6b7e26477b8e3454e78c8c639e97c8803fa5a301
[0.8.15]: https://github.com/carlrobertoh/llm-client/compare/fa0539e06d6cd8d21a4d0fa3336c747c2cb68fcc...d714854331915387da583c9a5b24877cc06286e
[0.8.14]: https://github.com/carlrobertoh/llm-client/compare/6461c8458325e7b2a33670fc09493b3357eb094c...fa0539e06d6cd8d21a4d0fa3336c747c2cb68fcc
[0.8.13]: https://github.com/carlrobertoh/llm-client/compare/a55fe7dcefbe6b911d5b99950d402dd06a66ec1e...6461c8458325e7b2a33670fc09493b3357eb094c
[0.8.12]: https://github.com/carlrobertoh/llm-client/compare/6fdf91d29194bfed92c7e23280953d47614e62a5...a55fe7dcefbe6b911d5b99950d402dd06a66ec1e
[0.8.11]: https://github.com/carlrobertoh/llm-client/compare/f381269da86b7f909d2f0fa2e5ecf11ec3c05da4...6fdf91d29194bfed92c7e23280953d47614e62a5
[0.8.10]: https://github.com/carlrobertoh/llm-client/compare/0d78d9b74fd163847a868f61fc2a82419f00def0...f381269da86b7f909d2f0fa2e5ecf11ec3c05da4
[0.8.9]: https://github.com/carlrobertoh/llm-client/compare/d4732400ead0f9d3a57c1a8fc0d1669cd7ffb984...0d78d9b74fd163847a868f61fc2a82419f00def0
[0.8.8]: https://github.com/carlrobertoh/llm-client/compare/283c1a658971876d98c0592bd315fa0850d6db0f...d4732400ead0f9d3a57c1a8fc0d1669cd7ffb984
[0.8.7]: https://github.com/carlrobertoh/llm-client/compare/3a029ac535a7dbf8d3710a2034446dbaca75301b...283c1a658971876d98c0592bd315fa0850d6db0f
[0.8.6]: https://github.com/carlrobertoh/llm-client/compare/5d1b3f4e4d8ae9c75a57425a06fbbfec4b608228...3a029ac535a7dbf8d3710a2034446dbaca75301b
[0.8.5]: https://github.com/carlrobertoh/llm-client/compare/becc0223bd07bda1b493b13e2e9aa423accafc3a...5d1b3f4e4d8ae9c75a57425a06fbbfec4b608228
[0.8.4]: https://github.com/carlrobertoh/llm-client/compare/f79729ebf02e423b3e5595c44accc8bb25ec0526...becc0223bd07bda1b493b13e2e9aa423accafc3a
[0.8.3]: https://github.com/carlrobertoh/llm-client/compare/21808c5c1273282fc0a177a381c88a605db1bb10...f79729ebf02e423b3e5595c44accc8bb25ec0526
[0.8.2]: https://github.com/carlrobertoh/llm-client/compare/cfa5330d0ca853e3edac7541dffec5dbe4f54e2a...21808c5c1273282fc0a177a381c88a605db1bb10
[0.8.1]: https://github.com/carlrobertoh/llm-client/compare/aedb56e983e23432f0be68be242f846566cd1f99...cfa5330d0ca853e3edac7541dffec5dbe4f54e2a
[0.8.0]: https://github.com/carlrobertoh/llm-client/compare/b48bcdbe7b5218fee085aeddbccdf95458fc6d19...aedb56e983e23432f0be68be242f846566cd1f99
[0.7.5]: https://github.com/carlrobertoh/llm-client/compare/2a0405ec2608475e6519a1d8322bb6f33037b620...b48bcdbe7b5218fee085aeddbccdf95458fc6d19
[0.7.4]: https://github.com/carlrobertoh/llm-client/compare/9074c1482f8253276fa6b9f0bb0497c0b741a5bb...2a0405ec2608475e6519a1d8322bb6f33037b620
[0.7.3]: https://github.com/carlrobertoh/llm-client/compare/7c05ab491fbcdfddd74518260d6df78d878e7ce3...9074c1482f8253276fa6b9f0bb0497c0b741a5bb
[0.7.2]: https://github.com/carlrobertoh/llm-client/compare/21e72691b73e997764d5db3e56fc0fed3d6dc6c4...7c05ab491fbcdfddd74518260d6df78d878e7ce3
[0.7.1]: https://github.com/carlrobertoh/llm-client/compare/27e8046091f1489790a7ec2bde4edadb5cc3d14b...21e72691b73e997764d5db3e56fc0fed3d6dc6c4
[0.7.0]: https://github.com/carlrobertoh/llm-client/compare/0179d0c1e1d4281fe6d93bf84a23ca6714931500...0179d0c1e1d4281fe6d93bf84a23ca6714931500
[0.6.2]: https://github.com/carlrobertoh/llm-client/compare/57039edbe25796bca08bd6cce2a92a02160607e9...0179d0c1e1d4281fe6d93bf84a23ca6714931500
[0.6.1]: https://github.com/carlrobertoh/llm-client/compare/f7791cbf92af7fe8b8b6a0226cdc3d3f8bf56f14...57039edbe25796bca08bd6cce2a92a02160607e9
[0.6.0]: https://github.com/carlrobertoh/llm-client/compare/3831d02c7ac0bd932dfad844605f3bd41d709d2b...f7791cbf92af7fe8b8b6a0226cdc3d3f8bf56f14
[0.5.0]: https://github.com/carlrobertoh/llm-client/compare/50f636ef1796286ca259ad9d3fc0000ae180687e...3831d02c7ac0bd932dfad844605f3bd41d709d2b
[0.4.3]: https://github.com/carlrobertoh/llm-client/compare/ae03125494754cf220eb9e3908d3be61bb2f9fe9..50f636ef1796286ca259ad9d3fc0000ae180687e
[0.4.2]: https://github.com/carlrobertoh/llm-client/compare/eedae7c6e0eca726620e3f1abb4610eda53cc502..ae03125494754cf220eb9e3908d3be61bb2f9fe9
[0.4.1]: https://github.com/carlrobertoh/llm-client/compare/0c6e33c925ddd01a65ca4692111d12eb0047dfbb..eedae7c6e0eca726620e3f1abb4610eda53cc502
[0.4.0]: https://github.com/carlrobertoh/llm-client/compare/c9d73c08b61a3ee368a163a34f64fd401ee4ce94..0c6e33c925ddd01a65ca4692111d12eb0047dfbb
