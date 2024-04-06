package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.interaction.Interaction;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.widgets.Dialog;

public class EnterBoat extends Task {
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Entering boat";
  }

  @Override
  public boolean validate() {
    return plugin.isInDesert() && (plugin.getPlayersReady() == 0 || Dialog.canContinueNPC());
  }

  @Override
  public void execute() {
    if (plugin.isReachedMaxStormIntensity()) {
      plugin.setReachedMaxStormIntensity(false);
      plugin.setGamesLost(plugin.getGamesLost() + 1);
    }

    final TileObject ladder = TileObjects.getNearest(ObjectID.ROPE_LADDER_41305);
    new Interaction(ladder).interact();

    Time.sleepTicks(Rand.nextInt(3, 5));

    if (Dialog.isOpen()) {
      Dialog.close();
    }

    Time.sleepTicksUntil(() -> plugin.isOnBoat() || plugin.getPlayersReady() >= 1, 20);

    if (!plugin.isOnBoat() && plugin.getPlayersReady() >= 1) {
      Movement.walk(new WorldPoint(3137, 2840, 0));
      Time.sleepTick();
    }
  }
}
