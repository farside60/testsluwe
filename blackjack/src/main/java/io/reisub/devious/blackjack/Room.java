package io.reisub.devious.blackjack;

import java.util.function.BooleanSupplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Locatable;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.coords.Area;
import net.unethicalite.api.coords.RectangularArea;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.movement.Movement;

@Getter
@RequiredArgsConstructor
public enum Room {
  NORTH(
      new RectangularArea(3363, 3000, 3365, 3003),
      new WorldPoint(3364, 3000, 0),
      new WorldPoint(3364, 3001, 0),
      new WorldPoint(3364, 2999, 0),
      new WorldPoint(3364, 2998, 0),
      new WorldPoint(3361, 2986, 0)),
  // TODO
  SOUTH(
      new RectangularArea(0, 0, 0, 0),
      new WorldPoint(0, 0, 0),
      new WorldPoint(0, 0, 0),
      new WorldPoint(0, 0, 0),
      new WorldPoint(0, 0, 0),
      new WorldPoint(0, 0, 0));

  private final Area area;

  /** Inside tile next to the door. */
  private final WorldPoint insideLocation;

  /** Inside tile with 1 tile between this tile and the door. Used for luring. */
  private final WorldPoint insideLocationTwo;

  /** Outside tile next to the door. */
  private final WorldPoint outsideLocation;

  /** Outside tile with 1 tile between this tile and the door. Used for luring. */
  private final WorldPoint outsideLocationTwo;

  /**
   * Tile to run towards in order to get rid of a leash.
   */
  private final WorldPoint leashLocation;

  public TileObject getDoor() {
    TileObject door = TileObjects.getFirstAt(outsideLocation, o -> o.hasAction("Open", "Close"));
    if (door != null) {
      return door;
    }

    return TileObjects.getFirstAt(insideLocation, o -> o.hasAction("Open", "Close"));
  }

  public boolean contains(Locatable locatable) {
    return area.contains(locatable);
  }

  public boolean passDoor(WorldPoint destination) {
    return passDoor(destination, true);
  }

  public boolean passDoor(WorldPoint destination, boolean closeDoor) {
    return passDoor(destination, closeDoor, null, 0);
  }

  public boolean passDoor(
      WorldPoint destination, boolean closeDoor, BooleanSupplier endCondition, int waitTicks) {
    if (!openDoor()) {
      return false;
    }

    Movement.walk(destination);
    Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().equals(destination), 10);

    if (endCondition != null) {
      Time.sleepTicksUntil(endCondition, waitTicks);
    }

    if (closeDoor) {
      if (!closeDoor()) {
        return false;
      }
      Time.sleepTick();
    }

    if (getArea().contains(destination)) {
      return getArea().contains(Players.getLocal());
    } else {
      return !getArea().contains(Players.getLocal());
    }
  }

  public boolean openDoor() {
    return interactDoor("Open");
  }

  public boolean closeDoor() {
    return interactDoor("Close");
  }

  public boolean interactDoor(String action) {
    final String reverseAction = action.equals("Open") ? "Close" : "Open";

    final TileObject door = getDoor();
    if (door == null) {
      return false;
    }

    if (door.hasAction(action)) {
      door.interact(action);
      Time.sleepTicksUntil(() -> getDoor().hasAction(reverseAction), 10);
    }

    return door.hasAction(reverseAction);
  }
}
