package ee.carlrobert.llm.client;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class InterceptorUtil {

  public static final Interceptor REWRITE_X_NDJSON_CONTENT_INTERCEPTOR = chain -> {
    Response response = chain.proceed(chain.request());
    if ("application/x-ndjson".equals(response.header("Content-Type", ""))) {
      try (ResponseBody originalBody = response.body()) {
        if (originalBody == null) {
          return response;
        }

        BufferedSource source = originalBody.source();
        try (Buffer buffer = new Buffer()) {
          while (!source.exhausted()) {
            String line = source.readUtf8LineStrict();
            buffer.writeUtf8("data: ").writeUtf8(line).writeUtf8("\n\n");
          }
          return response.newBuilder()
              .header("Content-Type", "text/event-stream")
              .body(ResponseBody.create(
                  buffer.readByteString(),
                  MediaType.parse("text/event-stream")))
              .build();
        }
      }
    }
    return response;
  };
}
