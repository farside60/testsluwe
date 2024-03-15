package io.reisub.devious.woodcutting.tasks;

import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.woodcutting.Woodcutting;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class Burn extends Task {
  @Inject private Woodcutting plugin;

  private WorldPoint current;

  @Override
  public String getStatus() {
    return "Burning logs";
  }

  @Override
  public Activity getActivity() {
    return Woodcutting.BURNING;
  }

  @Override
  public boolean validate() {
    current = Players.getLocal().getWorldLocation();

    // If we're standing on a fire or don't have any logs we should stop trying to burn
    if (TileObjects.getFirstAt(current, a -> a instanceof GameObject) != null
        || !Inventory.contains(Predicates.ids(Constants.LOG_IDS))) {
      plugin.setActivity(Activity.IDLE);
      return false;
    }

    return plugin.isCurrentActivity(Woodcutting.BURNING) && Inventory.contains(ItemID.TINDERBOX);
  }

  @Override
  public void execute() {
    final Item tinderbox = Inventory.getFirst(ItemID.TINDERBOX);
    final Item log = Inventory.getFirst(Predicates.ids(Constants.LOG_IDS));
    if (tinderbox == null || log == null) {
      return;
    }

    tinderbox.useOn(log);

    Time.sleepTicksUntil(() -> !Players.getLocal().getWorldLocation().equals(current), 20);
    Time.sleepTick();
  }
}
