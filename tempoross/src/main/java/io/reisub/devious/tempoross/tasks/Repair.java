package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.interaction.Interaction;
import io.reisub.devious.utils.api.interaction.checks.CurrentActivityCheck;
import io.reisub.devious.utils.api.interaction.checks.IdleActivityCheck;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;

public class Repair extends Task {
  TileObject brokenObject;
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Repairing";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInTemporossArea()
        || !plugin.isWaveIncoming()
        || plugin.isCurrentActivity(Tempoross.REPAIRING)) {
      return false;
    }

    TileObject tetherObject =
        TileObjects.getNearest(
            NullObjectID.NULL_41352,
            NullObjectID.NULL_41353,
            NullObjectID.NULL_41354,
            NullObjectID.NULL_41355);
    brokenObject =
        TileObjects.getNearest(
            ObjectID.DAMAGED_MAST_40996,
            ObjectID.DAMAGED_MAST_40997,
            ObjectID.DAMAGED_TOTEM_POLE,
            ObjectID.DAMAGED_TOTEM_POLE_41011);

    if (brokenObject == null) {
      return false;
    }

    if (tetherObject == null) {
      return true;
    }

    return Players.getLocal().distanceTo(tetherObject)
        > Players.getLocal().distanceTo(brokenObject);
  }

  @Override
  public void execute() {
    new Interaction(
            brokenObject,
            new IdleActivityCheck(3, plugin),
            new CurrentActivityCheck(25, plugin, Tempoross.REPAIRING))
        .interact();
    brokenObject.interact(0);
  }
}
