package io.reisub.devious.autopickup.tasks;

import io.reisub.devious.autopickup.AutoPickup;
import io.reisub.devious.autopickup.Config;
import io.reisub.devious.utils.tasks.Task;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.TileItem;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.game.Worlds;
import net.unethicalite.api.items.Inventory;

public class Burn extends Task {
  @Inject private AutoPickup plugin;
  @Inject private Config config;
  private TileItem nearestItem;

  @Override
  public String getStatus() {
    return "Burning logs";
  }

  @Override
  public boolean validate() {
    if (!config.burn()) {
      return false;
    }

    if (!Inventory.contains(ItemID.TINDERBOX)) {
      return false;
    }

    return Skills.getLevel(Skill.FIREMAKING) < config.firemakingLevel()
        && (nearestItem = plugin.getNearestItem()) != null
        && nearestItem.hasAction("Light")
        && !locationHasFire();
  }

  @Override
  public void execute() {
    nearestItem.interact("Light");

    Time.sleepTicksUntil(this::locationHasFire, 50);
    Time.sleepTick();
  }

  private boolean locationHasFire() {
    return TileObjects.getFirstAt(nearestItem.getWorldLocation(), "Fire") != null;
  }
}
