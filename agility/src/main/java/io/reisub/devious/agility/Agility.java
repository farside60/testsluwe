package io.reisub.devious.agility;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import io.reisub.devious.agility.tasks.Alch;
import io.reisub.devious.agility.tasks.HandleObstacle;
import io.reisub.devious.agility.tasks.PickupMark;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Agility",
    description = "Hippity hoppity, jumps on your property",
    enabledByDefault = false)
@Slf4j
public class Agility extends TickScript {
  public static final Set<WorldPoint> DELAY_POINTS =
      ImmutableSet.of(
          new WorldPoint(3363, 2998, 0),
          new WorldPoint(2653, 3676, 0),
          new WorldPoint(3103, 3261, 0));
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

    addTask(PickupMark.class);
    addTask(HandleObstacle.class);
    addTask(Alch.class);
  }
}
