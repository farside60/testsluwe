package io.reisub.devious.smithing;

import com.google.inject.Provides;
import io.reisub.devious.smithing.tasks.HandleBank;
import io.reisub.devious.smithing.tasks.Smith;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Run;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.InventoryID;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Smithing",
    description = "I shall make weapons from your bones!",
    enabledByDefault = false)
@Slf4j
public class Smithing extends TickScript {
  public static final Activity SMITHING = new Activity("Smithing");
  @Inject private Config config;
  private int lastBarCount;
  @Getter private int itemsMade;

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

    lastActionTimeout = 9;
    itemsMade = 0;
    lastBarCount = Inventory.getCount(config.metal().getBarId());

    addTask(Run.class);
    tasks.add(new HandleBank(this, config));
    tasks.add(new Smith(this, config));
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    if (event.getContainerId() != InventoryID.INVENTORY.getId()) {
      return;
    }

    final int count = Inventory.getCount(config.metal().getBarId());

    if (count < lastBarCount) {
      itemsMade++;
    }

    lastBarCount = count;

    if (count < config.product().getRequiredBars() && isCurrentActivity(SMITHING)) {
      setActivity(Activity.IDLE);
    }
  }

  @Subscribe
  private void onAnimationChanged(AnimationChanged event) {
    if (!Utils.isLoggedIn()) {
      return;
    }

    switch (Players.getLocal().getAnimation()) {
      case AnimationID.SMITHING_ANVIL:
      case AnimationID.SMITHING_IMCANDO_HAMMER:
        setActivity(SMITHING);
        break;
      default:
    }
  }
}
