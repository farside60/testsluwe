package io.reisub.devious.stallstealer;

import com.google.inject.Provides;
import io.reisub.devious.stallstealer.tasks.Drop;
import io.reisub.devious.stallstealer.tasks.GoToBank;
import io.reisub.devious.stallstealer.tasks.GoToStall;
import io.reisub.devious.stallstealer.tasks.HandleBank;
import io.reisub.devious.stallstealer.tasks.Steal;
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
    name = "Sluwe Stall Stealer",
    description = "Steals from filthy bourgeoisie around Gielinor and gives to the poor (you)",
    enabledByDefault = false)
@Slf4j
public class StallStealer extends TickScript {
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

    addTask(Drop.class);
    addTask(GoToBank.class);
    addTask(HandleBank.class);
    addTask(GoToStall.class);
    addTask(Steal.class);
  }
}
