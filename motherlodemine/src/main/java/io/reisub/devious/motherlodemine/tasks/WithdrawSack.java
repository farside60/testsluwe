package io.reisub.devious.motherlodemine.tasks;

import io.reisub.devious.motherlodemine.MotherlodeMine;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class WithdrawSack extends Task {
  private static final int X_BETWEEN_TUNNEL_ENTRANCES = 3761;
  @Inject private MotherlodeMine plugin;

  @Override
  public String getStatus() {
    return "Withdrawing from sack";
  }

  @Override
  public boolean validate() {
    TileObjects.getNearest(o -> o.hasAction("Foo"));
    return plugin.isCurrentActivity(Activity.IDLE)
        && !plugin.isUpstairs()
        && Players.getLocal().getWorldLocation().getX() < X_BETWEEN_TUNNEL_ENTRANCES
        && plugin.isSackFull()
        && !Inventory.contains(
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
            ItemID.UNCUT_DRAGONSTONE);
  }

  @Override
  public void execute() {
    TileObject sack =
        TileObjects.getNearest(o -> o.getName().equals("Sack") && o.hasAction("Search"));
    if (sack == null) {
      return;
    }

    plugin.setActivity(Activity.WITHDRAWING);

    sack.interact("Search");
    Time.sleepTicksUntil(() -> plugin.isCurrentActivity(Activity.IDLE), 30);
  }
}
