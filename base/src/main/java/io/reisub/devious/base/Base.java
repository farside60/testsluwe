package io.reisub.devious.base;

import com.google.inject.Provides;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Base",
    description = "",
    enabledByDefault = false
)
@Slf4j
public class Base extends TickScript {
  @Inject private Config config;

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // addTask();
  }
}
