package io.reisub.devious.blackjack.tasks;

import com.google.common.collect.ImmutableList;
import io.reisub.devious.blackjack.Blackjack;
import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.tasks.Task;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.coords.Area;
import net.unethicalite.api.coords.RectangularArea;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.movement.pathfinder.Walker;

public class LureIn extends Task {
  @Inject private Blackjack plugin;
  @Inject private Config config;

  private final List<Area> ignoreAreas =
      ImmutableList.of(
          new RectangularArea(3363, 2990, 3366, 2995), new RectangularArea(3356, 3000, 3359, 3004));

  @Override
  public String getStatus() {
    return "Luring target into room";
  }

  @Override
  public boolean validate() {
    if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
      return false;
    }

    if (Players.getLocal().distanceTo(config.target().getRoom().getOutsideLocation()) > 8) {
      return false;
    }

    NPC npcInRoom =
        NPCs.getNearest(
            n -> n.getId() == config.target().getId() && config.target().getRoom().contains(n));

    return npcInRoom == null;
  }

  @Override
  public void execute() {
    if (config.target().getRoom().contains(Players.getLocal())
        && config.target().getRoom().getDoor().hasAction("Open")) {
      config.target().getRoom().openDoor();
    }

    final NPC target =
        NPCs.getNearest(
            n -> {
              if (config.target().getId() != n.getId()) {
                return false;
              }

              if (!Reachable.isInteractable(n)) {
                return false;
              }

              for (Area ignoreArea : ignoreAreas) {
                if (ignoreArea.contains(n)) {
                  return false;
                }
              }

              return true;
            });
    if (target == null || !plugin.lure(target)) {
      return;
    }

    // simplistic way of walking in straight lines to avoid lure targets getting stuck
    // lots of room for improvement
    final WorldPoint destination = config.target().getRoom().getOutsideLocationTwo();

    List<WorldPoint> path = Walker.buildPath(destination);
    if (path.isEmpty()) {
      return;
    }

    WorldPoint lastInLine = null;

    for (WorldPoint point : path) {
      if (lastInLine == null) {
        lastInLine = point;
        continue;
      }

      if (point.getX() != lastInLine.getX() && point.getY() != lastInLine.getY()) {
        final WorldPoint finalLastInLine = lastInLine;
        Movement.walk(finalLastInLine);
        Time.sleepTicksUntil(
            () ->
                Players.getLocal().getWorldLocation().equals(finalLastInLine) && !target.isMoving(),
            15);
      }

      lastInLine = point;
    }

    if (Players.getLocal().distanceTo(target) > 1) {
      // Try recovering from Rana blocking our target
      final NPC rana = NPCs.getNearest(NpcID.RANA_THE_DYER);
      if (rana != null && rana.distanceTo(Players.getLocal()) <= 1) {
        Movement.walk(Players.getLocal().getWorldLocation().dx(1).dy(1));
        Time.sleepTicks(2);
      }
    }

    config
        .target()
        .getRoom()
        .passDoor(
            config.target().getRoom().getInsideLocationTwo(),
            true,
            () -> target.getWorldLocation().equals(config.target().getRoom().getInsideLocation()),
            10);

    if (!Movement.isRunEnabled()) {
      Movement.toggleRun();
    }
  }
}
