package io.reisub.sluwe.fletching;

import com.google.inject.Provides;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import io.reisub.sluwe.fletching.tasks.Attach;
import io.reisub.sluwe.fletching.tasks.Dive;
import io.reisub.sluwe.fletching.tasks.Fletch;
import io.reisub.sluwe.fletching.tasks.HandleBank;
import io.reisub.sluwe.fletching.tasks.PickupSeed;
import io.reisub.sluwe.fletching.tasks.Resurface;
import io.reisub.sluwe.fletching.tasks.Stringing;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.items.Inventory;
import org.pf4j.Extension;
import org.slf4j.Logger;

@PluginDescriptor(name = "Sluwe Fletching", description = "Y fletch?", enabledByDefault = false)
@PluginDependency(Utils.class)
@Slf4j
@Extension
public class Fletching extends TickScript {
  public static final Activity ATTACHING = new Activity("Attaching");
  public static final Activity FLETCHING = new Activity("Fletching");
  public static final Activity STRINGING = new Activity("Stringing");
  public static final int FOSSIL_ISLAND_SMALL_ISLAND_REGION = 14908;
  public static final int FOSSIL_ISLAND_SEAWEED_REGION = 15008;
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
    addTask(Resurface.class);
    addTask(Dive.class);
    addTask(PickupSeed.class);
    addTask(Fletch.class);
    addTask(Stringing.class);
    addTask(Attach.class);

    idleCheckInventoryChange = true;
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    if (!isRunning()) {
      return;
    }

    if (isCurrentActivity(FLETCHING) && !Inventory.contains(config.logType().getId())) {
      setActivity(Activity.IDLE);
    } else if (isCurrentActivity(STRINGING) && !Inventory.contains(ItemID.BOW_STRING)) {
      setActivity(Activity.IDLE);
    }
  }
}
