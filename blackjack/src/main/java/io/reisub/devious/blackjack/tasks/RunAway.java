package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Config;
import io.reisub.devious.blackjack.Room;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;

public class RunAway extends Task {
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Running away";
  }

  @Override
  public boolean validate() {
    return Combat.getCurrentHealth() <= 12
        && (NPCs.getNearest(config.target().getId()).isInteracting()
            && NPCs.getNearest(config.target().getId())
                .getInteracting()
                .equals(Players.getLocal()));
  }

  @Override
  public void execute() {
    if (config.target().getRoom() == Room.NORTH) {
      TileObjects.getNearest(ObjectID.LADDER_6261).interact(0);
      Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().getPlane() == 1, 5);
    } else {
      SluweMovement.walkTo(config.target().getRoom().getLeashLocation());
      Time.sleepTicks(3);
    }
  }
}
