package io.reisub.devious.motherlodemine.tasks;

import io.reisub.devious.motherlodemine.Config;
import io.reisub.devious.motherlodemine.MiningArea;
import io.reisub.devious.motherlodemine.MotherlodeMine;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class Mine extends Task {

  @Inject private MotherlodeMine plugin;
  @Inject private Config config;
  private TileObject oreVein;

  @Override
  public String getStatus() {
    return "Mining";
  }

  @Override
  public boolean validate() {
    if (plugin.getMiningArea() == MiningArea.NORTH) {
      if (Players.getLocal().getWorldLocation().getY() < 5684) {
        return false;
      }
    }

    return plugin.isCurrentActivity(Activity.IDLE)
        && !Inventory.isFull()
        && (plugin.isUpstairs() || !config.upstairs())
        && (oreVein = plugin.getMiningArea().getNearestVein()) != null
        && SluweMovement.isInteractable(oreVein);
  }

  @Override
  public void execute() {
    plugin.setActivity(MotherlodeMine.MINING);
    oreVein.interact("Mine");
  }

  @Subscribe
  private void onGameTick(GameTick event) {
    if (plugin.isRunning() && plugin.isCurrentActivity(MotherlodeMine.MINING)) {
      if (oreVein == null) {
        plugin.setActivity(Activity.IDLE);
      } else {
        final TileObject oreVeinCheck =
            TileObjects.getFirstAt(oreVein.getWorldLocation(), o -> o.hasAction("Mine"));

        if (oreVeinCheck == null) {
          oreVein = null;
          plugin.setActivity(Activity.IDLE);
        }
      }
    }
  }
}
