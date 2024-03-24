package io.reisub.devious.agility;

import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
public class ObstacleArea extends WorldArea {
  private final int id;

  public ObstacleArea(WorldPoint sw, WorldPoint ne, int id) {
    super(sw, ne);

    this.id = id;
  }
}
