package io.reisub.devious.barroniteminer;

import com.google.inject.Provides;
import io.reisub.devious.barroniteminer.tasks.Crush;
import io.reisub.devious.barroniteminer.tasks.HandleBank;
import io.reisub.devious.barroniteminer.tasks.Mine;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.events.InventoryChanged;
import net.unethicalite.api.items.Inventory;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Barronite Miner",
    description = "Let's get an Imcando hammer",
    enabledByDefault = false)
@Slf4j
public class BarroniteMiner extends TickScript {
  public static final Activity MINING = new Activity("Mining");
  public static final Activity SMITHING = new Activity("Smithing");

  @Inject private Config config;
  @Getter private WorldPoint lastDespawn;
  private Mine mineTask;

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

    mineTask = injector.getInstance(Mine.class);

    addTask(HandleBank.class);
    addTask(Crush.class);
    addTask(mineTask);
  }

  @Subscribe
  private void onGameObjectChanged(WallObjectDespawned wallObjectDespawned) {
    if (!isRunning()) {
      return;
    }

    final int id = wallObjectDespawned.getWallObject().getId();

    if (id == ObjectID.BARRONITE_ROCKS || id == ObjectID.BARRONITE_ROCKS_41548) {
      if (mineTask != null && mineTask.getCurrentVeinLocation() != null) {
        if (mineTask
            .getCurrentVeinLocation()
            .equals(wallObjectDespawned.getWallObject().getWorldLocation())) {
          setActivity(Activity.IDLE);
        }
      }
    }
  }

  @Subscribe
  private void onInventoryChanged(InventoryChanged inventoryChanged) {
    if (!isRunning()) {
      return;
    }

    if (isCurrentActivity(SMITHING) && !Inventory.contains(ItemID.BARRONITE_DEPOSIT)) {
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
