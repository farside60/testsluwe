package io.reisub.devious.motherlodemine.tasks;

import io.reisub.devious.motherlodemine.Config;
import io.reisub.devious.motherlodemine.MotherlodeMine;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class UseShortcut extends Task {
  private static final int X_BETWEEN_TUNNEL_ENTRANCES = 3761;

  @Inject private MotherlodeMine plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Using shortcut";
  }

  @Override
  public boolean validate() {
    if (config.upstairs() || !config.shortcut() || !plugin.isCurrentActivity(Activity.IDLE)) {
      return false;
    }

    if (!Inventory.contains(
            ItemID.PAYDIRT,
            ItemID.RUNITE_ORE,
            ItemID.ADAMANTITE_ORE,
            ItemID.MITHRIL_ORE,
            ItemID.GOLD_ORE,
            ItemID.COAL,
            ItemID.UNCUT_SAPPHIRE,
            ItemID.UNCUT_EMERALD,
            ItemID.UNCUT_RUBY,
            ItemID.UNCUT_DIAMOND,
            ItemID.UNCUT_DRAGONSTONE)
        && Players.getLocal().getWorldLocation().getX() < X_BETWEEN_TUNNEL_ENTRANCES) {
      return true;
    }

    return Inventory.isFull()
        && Players.getLocal().getWorldLocation().getX() > X_BETWEEN_TUNNEL_ENTRANCES;
  }

  @Override
  public void execute() {
    plugin.setActivity(MotherlodeMine.ENTERING_TUNNEL);
    final TileObject tunnel = TileObjects.getNearest(ObjectID.DARK_TUNNEL_10047);

    if (tunnel == null) {
      return;
    }

    final WorldPoint destination =
        Players.getLocal().getWorldLocation().getX() > X_BETWEEN_TUNNEL_ENTRANCES
            ? new WorldPoint(3759, 5670, 0)
            : new WorldPoint(3765, 5671, 0);

    if (Players.getLocal().getWorldLocation().getX() > X_BETWEEN_TUNNEL_ENTRANCES) {
      plugin.mineRockfall(3766, 5670);
    }

    tunnel.interact("Enter");
    Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().equals(destination), 20);
    Time.sleepTick();
    plugin.setActivity(Activity.IDLE);
  }
}
