package io.reisub.devious.utils.api.interaction.checks;

import net.runelite.api.Locatable;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;

public class DistanceCheck extends Check {
  private final Locatable locatable;
  private final int distance;
  private final boolean closer;

  public DistanceCheck(int timeout, Locatable locatable, int distance) {
    this(timeout, false, locatable, distance, true);
  }

  public DistanceCheck(int timeout, Locatable locatable, int distance, boolean closer) {
    this(timeout, false, locatable, distance, closer);
  }

  public DistanceCheck(
      int timeout, boolean ignoreFailure, Locatable locatable, int distance, boolean closer) {
    super(timeout, ignoreFailure);
    this.locatable = locatable;
    this.distance = distance;
    this.closer = closer;
  }

  @Override
  public void check() throws CheckFailedException {
    if (locatable == null) {
      return;
    }

    if (closer) {
      if (!Time.sleepTicksUntil(
          () -> Players.getLocal().distanceTo(locatable) < distance, getTimeout())) {
        throw new CheckFailedException(
            String.format("Timed out waiting for distance to be closer than %d tiles", distance),
            getTimeout());
      }
    } else {
      if (!Time.sleepTicksUntil(
          () -> Players.getLocal().distanceTo(locatable) > distance, getTimeout())) {
        throw new CheckFailedException(
            String.format("Timed out waiting for distance to be further than %d tiles", distance),
            getTimeout());
      }
    }
  }
}
