package io.reisub.devious.enchanter;

import com.google.inject.Provides;
import io.reisub.devious.enchanter.tasks.Enchant;
import io.reisub.devious.enchanter.tasks.HandleBank;
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
    name = "Sluwe Enchanter",
    description = "Such an enchanting plugin",
    enabledByDefault = false)
@Slf4j
public class Enchanter extends TickScript {
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

    addTask(HandleBank.class);
    addTask(Enchant.class);
  }
}
