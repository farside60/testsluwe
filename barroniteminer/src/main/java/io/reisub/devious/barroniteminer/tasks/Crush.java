package io.reisub.devious.barroniteminer.tasks;

import io.reisub.devious.barroniteminer.BarroniteMiner;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class Crush extends Task {
  @Inject private BarroniteMiner plugin;
  private TileObject crusher;

  @Override
  public String getStatus() {
    return "Crushing deposits";
  }

  @Override
  public boolean validate() {
    crusher = TileObjects.getNearest(ObjectID.BARRONITE_CRUSHER);

    if (crusher == null) {
      return false;
    }

    return !plugin.isCurrentActivity(BarroniteMiner.SMITHING)
        && (Inventory.isFull()
            || Inventory.contains(ItemID.BARRONITE_DEPOSIT)
                && Players.getLocal().distanceTo(crusher) < 3);
  }

  @Override
  public void execute() {
    crusher.interact("Smith");
    Time.sleepTicksUntil(() -> Players.getLocal().getAnimation() != -1, 30);

    plugin.setActivity(BarroniteMiner.SMITHING);
  }
}
