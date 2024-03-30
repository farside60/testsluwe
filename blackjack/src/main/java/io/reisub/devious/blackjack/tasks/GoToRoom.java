package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;

public class GoToRoom extends Task {
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Going to room";
  }

  @Override
  public boolean validate() {
    return config.target().getRoom().getOutsideLocation().distanceTo(Players.getLocal()) > 8;
  }

  @Override
  public void execute() {
    final WorldPoint destination = config.target().getRoom().getOutsideLocation();

    SluweMovement.walkTo(destination);
    Time.sleepTicksUntil(() -> Players.getLocal().distanceTo(destination) <= 8, 20);

    // if there's a lurable NPC inside the room we should end up in the room because:
    // if there are wrong NPCs inside, the LureOut task will lure them out which expects us to be in
    // the room
    // if we only have the correct one we will start blackjacking which expects us to be in the room
    // if none are inside, the LureIn task will lure one in which expects us to be outside the room
    final NPC lurableNpc =
        NPCs.getNearest(n -> n.hasAction("Lure") && config.target().getRoom().contains(n));

    if (lurableNpc != null) {
      config.target().getRoom().passDoor(config.target().getRoom().getInsideLocation());
    }
  }
}
