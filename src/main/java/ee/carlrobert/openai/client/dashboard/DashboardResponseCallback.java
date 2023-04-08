package ee.carlrobert.openai.client.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.function.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DashboardResponseCallback<T> implements Callback {

  private static final Logger LOG = LoggerFactory.getLogger(DashboardResponseCallback.class);

  private final Consumer<T> responseConsumer;
  private final Class<T> clazz;

  DashboardResponseCallback(Consumer<T> responseConsumer, Class<T> clazz) {
    this.responseConsumer = responseConsumer;
    this.clazz = clazz;
  }
  @Override
  public void onFailure(@NotNull Call call, @NotNull IOException e) {
    LOG.error("Unable to retrieve dashboard info", e);
  }

  @Override
  public void onResponse(@NotNull Call call, @NotNull Response response) {
    if (response.body() != null) {
      try {
        responseConsumer.accept(new ObjectMapper().readValue(response.body().string(), clazz));
      } catch (IOException ex) {
        LOG.error("Unable to deserialize dashboard info response", ex);
      }
    }
  }
}
