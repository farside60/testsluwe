package io.reisub.devious.roguesden;

import com.google.inject.Provides;
import io.reisub.devious.roguesden.tasks.CrackSafe;
import io.reisub.devious.roguesden.tasks.HandleBank;
import io.reisub.devious.roguesden.tasks.HandleObstacle;
import io.reisub.devious.roguesden.tasks.OpenTileDoor;
import io.reisub.devious.roguesden.tasks.PickupPowder;
import io.reisub.devious.roguesden.tasks.PickupTile;
import io.reisub.devious.roguesden.tasks.Start;
import io.reisub.devious.roguesden.tasks.ThrowPowder;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Rogues Den",
    description = "Rogue outfit for lazy people",
    enabledByDefault = false
)
@Slf4j
public class RoguesDen extends TickScript {
  @Inject private Config config;

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    super.onStart();

    addTask(CrackSafe.class);
    addTask(PickupTile.class);
    addTask(PickupPowder.class);
    addTask(ThrowPowder.class);
    addTask(OpenTileDoor.class);
    addTask(HandleBank.class);
    addTask(Start.class);
    addTask(HandleObstacle.class);
  }
}
