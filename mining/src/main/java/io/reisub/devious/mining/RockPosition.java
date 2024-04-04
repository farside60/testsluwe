package io.reisub.devious.mining;

import lombok.Value;
import net.runelite.api.coords.WorldPoint;

@Value
public class RockPosition {
  WorldPoint rock;
  WorldPoint interactFrom;
}
