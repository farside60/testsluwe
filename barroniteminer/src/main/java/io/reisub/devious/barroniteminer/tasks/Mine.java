package io.reisub.devious.barroniteminer.tasks;

import io.reisub.devious.barroniteminer.BarroniteMiner;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;

public class Mine extends Task {
  @Inject private BarroniteMiner plugin;
  @Getter private WorldPoint currentVeinLocation;

  @Override
  public String getStatus() {
    return "Mining";
  }

  @Override
  public boolean validate() {
    return plugin.isCurrentActivity(Activity.IDLE);
  }

  @Override
  public void execute() {
    final TileObject vein =
        TileObjects.getNearest(ObjectID.BARRONITE_ROCKS, ObjectID.BARRONITE_ROCKS_41548);
    if (vein == null) {
      return;
    }

    vein.interact("Mine");
    Time.sleepTicksUntil(() -> Players.getLocal().getAnimation() != -1, 30);

    plugin.setActivity(BarroniteMiner.MINING);
    currentVeinLocation = vein.getWorldLocation();
  }
}
