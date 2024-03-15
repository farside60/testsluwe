package io.reisub.devious.stallstealer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum Stall {
  ARDOUGNE_CAKE(
      ObjectID.BAKERS_STALL_11730, new WorldPoint(2669, 3310, 0), new WorldPoint(2653, 3284, 0));

  private final int stallId;
  private final WorldPoint stealLocation;
  private final WorldPoint bankLocation;
}
