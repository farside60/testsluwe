package io.reisub.devious.wintertodt;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.unethicalite.api.game.Worlds;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

@Singleton
@Slf4j
public class Scouter {
  @Getter private final List<BossData> data = new ArrayList<>(4);
  @Inject public Wintertodt plugin;
  @Inject public OkHttpClient okHttpClient;
  private Instant lastUpdate;

  public void onGameTick() {
    if (lastUpdate == null || Duration.between(lastUpdate, Instant.now()).getSeconds() > 2) {
      lastUpdate = Instant.now();

      getRemoteData();
    }
  }

  public BossData getLocalData() {
    return new BossData(
        plugin.getBossHealth(),
        Worlds.getCurrentId(),
        System.currentTimeMillis() / 1000,
        plugin.getRespawnTimer());
  }

  @SuppressWarnings("checkstyle:EmptyCatchBlock")
  private void getRemoteData() {
    try {
      Request r =
          new Request.Builder()
              .url("https://www.wintertodt.com/scouter/")
              .addHeader("User-Agent", "RuneLite")
              .addHeader("Authorization", "2")
              .build();

      okHttpClient
          .newCall(r)
          .enqueue(
              new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                  log.debug("Error retrieving Wintertodt boss data", e);
                }

                @SuppressWarnings("checkstyle:EmptyCatchBlock")
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                  if (response.isSuccessful()) {
                    try {
                      JsonArray jsonArray =
                          new Gson()
                              .fromJson(
                                  Objects.requireNonNull(response.body()).string(),
                                  JsonArray.class);
                      parseData(jsonArray);
                    } catch (IOException | JsonSyntaxException ignored) {

                    }
                  }
                }
              });
    } catch (IllegalArgumentException ignored) {

    }
  }

  @SuppressWarnings("checkstyle:EmptyCatchBlock")
  private void parseData(JsonArray jsonArray) {
    for (JsonElement jsonElement : jsonArray) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      try {
        BossData bossData =
            new BossData(
                jsonObject.get("a").getAsInt(),
                jsonObject.get("b").getAsInt(),
                jsonObject.get("c").getAsLong(),
                jsonObject.get("d").getAsInt());
        if (bossData.getWorld() != Worlds.getCurrentId()) {
          data.add(bossData);
        }
      } catch (UnsupportedOperationException ignored) {

      }
    }
  }
}
