package io.reisub.devious.motherlodemine.tasks;

import io.reisub.devious.motherlodemine.MotherlodeMine;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;

public class FixWheel extends Task {
  @Inject private MotherlodeMine plugin;

  @Override
  public String getStatus() {
    return "Fixing wheel";
  }

  @Override
  public boolean validate() {
    return plugin.isCurrentActivity(Activity.IDLE)
        && !plugin.isUpstairs()
        && plugin.wasPreviousActivity(Activity.DEPOSITING)
        && TileObjects.getAll(ObjectID.BROKEN_STRUT).size() == 2;
  }

  @Override
  public void execute() {
    TileObject strut = TileObjects.getNearest(ObjectID.BROKEN_STRUT);
    if (strut == null) {
      return;
    }

    plugin.setActivity(MotherlodeMine.REPAIRING);

    strut.interact("Hammer");
    Time.sleepTicksUntil(() -> plugin.isCurrentActivity(Activity.IDLE), 30);
  }
}
