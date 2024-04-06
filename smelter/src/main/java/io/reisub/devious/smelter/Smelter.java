package io.reisub.devious.smelter;

import com.google.inject.Provides;
import io.reisub.devious.smelter.tasks.HandleBank;
import io.reisub.devious.smelter.tasks.Smelt;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.events.InventoryChanged;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Smelter",
    description = "You load 16 tons, what do you get? Another day older and deeper in debt.",
    enabledByDefault = false)
@Slf4j
public class Smelter extends TickScript {
  public static final Activity SMELTING = new Activity("Smelting");
  @Inject private Config config;
  @Getter @Setter private int lastBankTick;

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
    addTask(Smelt.class);
  }

  @Subscribe
  private void onInventoryChanged(InventoryChanged inventoryChanged) {
    if (!isRunning()) {
      return;
    }

    if (isCurrentActivity(SMELTING) && !config.product().hasMaterials()) {
      setActivity(Activity.IDLE);
    }
  }

  @Subscribe
  private void onChatMessage(ChatMessage chatMessage) {
    if (!isRunning()) {
      return;
    }

    if (chatMessage.getMessage().startsWith("Congratulations, you've just advanced")) {
      setActivity(Activity.IDLE);
    }
  }
}
