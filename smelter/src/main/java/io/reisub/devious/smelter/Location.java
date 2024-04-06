package io.reisub.devious.smelter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum Location {
  EDGEVILLE(ObjectID.FURNACE_16469, new WorldPoint(3109, 3499, 0), new WorldPoint(3098, 3494, 0));

  private final int furnaceId;
  private final WorldPoint furnaceLocation;
  private final WorldPoint bankLocation;
}
