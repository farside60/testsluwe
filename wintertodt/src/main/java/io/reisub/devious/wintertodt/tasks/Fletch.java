package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Config;
import io.reisub.devious.wintertodt.Wintertodt;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class Fletch extends Task {
  @Inject public Wintertodt plugin;
  @Inject public Config config;

  @Override
  public String getStatus() {
    return "Cutting roots into kindlings";
  }

  @Override
  public boolean validate() {
    if (!plugin.bossIsUp()) {
      return false;
    }

    if ((plugin.wasPreviousActivity(Wintertodt.WOODCUTTING)
            || plugin.wasPreviousActivity(Activity.EATING))
        && Players.getLocal().distanceTo(plugin.getNearestSide().getPositionNearRoots()) <= 3
        && !Inventory.isFull()
        && !plugin.shouldStartFletching()) {
      return false;
    }

    return plugin.isCurrentActivity(Activity.IDLE)
        && !plugin.shouldStartFeeding()
        && Inventory.contains(ItemID.BRUMA_ROOT);
  }

  @Override
  public void execute() {
    if (config.fletchNearBrazier()) {
      WorldPoint nearBrazier = plugin.getNearestSide().getPositionNearBrazier();
      if (!Players.getLocal().getWorldLocation().equals(nearBrazier)) {
        Movement.walk(nearBrazier);
      }
    }

    final Item knife = Inventory.getFirst(ItemID.KNIFE);
    final Item root = Inventory.getFirst(ItemID.BRUMA_ROOT);
    if (knife == null || root == null) {
      return;
    }

    Time.sleepTick();
    knife.useOn(root);

    Time.sleepTicksUntil(() -> plugin.isCurrentActivity(Wintertodt.FLETCHING), 5);
  }
}
