package io.reisub.devious.motherlodemine.tasks;

import io.reisub.devious.motherlodemine.Config;
import io.reisub.devious.motherlodemine.MotherlodeMine;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class GoUp extends Task {

  @Inject
  private MotherlodeMine plugin;
  @Inject
  private Config config;

  @Override
  public String getStatus() {
    return "Going up";
  }

  @Override
  public boolean validate() {
    return config.upstairs()
        && plugin.isCurrentActivity(Activity.IDLE)
        && !Inventory.contains(ItemID.PAYDIRT)
        && !plugin.isUpstairs();
  }

  @Override
  public void execute() {
    final TileObject ladder = TileObjects.getNearest(NullObjectID.NULL_19044);

    if (ladder == null) {
      return;
    }

    ladder.interact("Climb");
    Time.sleepTicksUntil(() -> plugin.isUpstairs(), 20);
    Time.sleepTick();
  }
}
