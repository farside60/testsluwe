package io.reisub.devious.superglassmake;

import com.google.inject.Provides;
import io.reisub.devious.superglassmake.tasks.CastSuperglassMake;
import io.reisub.devious.superglassmake.tasks.HandleBank;
import io.reisub.devious.superglassmake.tasks.PickupGlass;
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
    name = "Sluwe Superglass Make",
    description = "A super Superglass Make caster",
    enabledByDefault = false)
@Slf4j
public class SuperglassMake extends TickScript {
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

    addTask(PickupGlass.class);
    addTask(HandleBank.class);
    addTask(CastSuperglassMake.class);
  }
}
