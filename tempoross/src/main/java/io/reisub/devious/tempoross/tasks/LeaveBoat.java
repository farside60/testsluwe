package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.interaction.Interaction;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;

public class LeaveBoat extends Task {
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Leaving boat";
  }

  @Override
  public boolean validate() {
    return plugin.isOnBoat() && plugin.getPlayersReady() > 1;
  }

  @Override
  public void execute() {
    final TileObject ladder = TileObjects.getNearest(ObjectID.ROPE_LADDER_41305);

    new Interaction(ladder, "Quick-climb").interact();

    Time.sleepTicksUntil(() -> plugin.isInDesert(), 20);
  }
}
