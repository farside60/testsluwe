package io.reisub.devious.wintertodt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum Side {
  EAST(new WorldPoint(1638, 3996, 0), new WorldPoint(1638, 3988, 0)),
  WEST(new WorldPoint(1622, 3996, 0), new WorldPoint(1622, 3988, 0));

  private final WorldPoint positionNearBrazier;
  private final WorldPoint positionNearRoots;
}
