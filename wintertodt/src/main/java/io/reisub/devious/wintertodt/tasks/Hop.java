package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.BossData;
import io.reisub.devious.wintertodt.Config;
import io.reisub.devious.wintertodt.Scouter;
import io.reisub.devious.wintertodt.Wintertodt;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.Worlds;

public class Hop extends Task {
  @Inject public Wintertodt plugin;
  @Inject public Config config;
  @Inject public Scouter scouter;

  private Instant lastAttempt;
  private int bestWorld;

  @Override
  public String getStatus() {
    return "Hopping to other world";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInWintertodtRegion()) {
      return false;
    }

    if (plugin.bossIsUp() || plugin.getRespawnTimer() <= 10) {
      return false;
    }

    bestWorld = findBestWorld();

    return bestWorld != Worlds.getCurrentId()
        && (lastAttempt == null || Duration.between(lastAttempt, Instant.now()).getSeconds() >= 5);
  }

  @Override
  public void execute() {
    Worlds.hopTo(Worlds.getFirst(bestWorld));
    Time.sleepTicksUntil(() -> Game.getState() == GameState.LOGGED_IN, 20);

    lastAttempt = Instant.now();
  }

  private int findBestWorld() {
    List<BossData> dataList = scouter.getData();

    BossData best = scouter.getLocalData();

    for (BossData data : dataList) {
      // ignore data older than 5 seconds
      if (System.currentTimeMillis() - (data.getTime() * 1000) >= 5000) {
        continue;
      }

      if (best.getHealth() > 0) {
        if (data.getHealth() >= config.hopPercentage() && data.getHealth() < best.getHealth()) {
          best = data;
        }
      } else {
        if (data.getHealth() >= config.hopPercentage()) {
          best = data;
        } else if (data.getHealth() <= 0 && data.getTimer() < best.getTimer()) {
          best = data;
        }
      }
    }

    return best.getWorld();
  }
}
