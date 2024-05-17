package io.reisub.devious.utils.api;

import net.runelite.api.coords.WorldPoint;

public class SluweWorldPoint {
  /**
   * Parse a string to a WorldPoint object. The string may contain spaces but must use the format
   * 'x,y,plane'. The plane may be omitted. If this is the case, it will default the plane to '0'.
   * The string '0,0,0' will return null.
   *
   * @param worldPointString the string containing the world point coordinates in the 'x,y,plane'
   *     format
   * @return WorldPoint or null if the string failed to parse or is '0,0,0'
   */
  public static WorldPoint parseString(String worldPointString) {
    worldPointString = worldPointString.replaceAll(" ", "");

    if (worldPointString.equals("0,0,0")) {
      return null;
    }

    String[] split = worldPointString.split(",");

    try {
      switch (split.length) {
        case 2:
          return new WorldPoint(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0);
        case 3:
          return new WorldPoint(
              Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        default:
          return null;
      }
    } catch (NumberFormatException numberFormatException) {
      return null;
    }
  }
}
