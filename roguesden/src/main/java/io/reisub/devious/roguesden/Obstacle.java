package io.reisub.devious.roguesden;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum Obstacle {
  CONTORTION_BARS(new WorldPoint(3056, 4992, 1), ObstacleObject.CONTORTION_BARS, 0, false),
  PENDULUM_ONE(new WorldPoint(3048, 4997, 1), null, 2, false),
  SPIKES_ONE(new WorldPoint(3039, 4999, 1), null, 0, true),
  GRILL_ONE(new WorldPoint(3029, 5003, 1), ObstacleObject.GRILL_ONE, 1, false),
  SPIKES_TWO(new WorldPoint(3023, 5001, 1), null, 0, true),
  SPIKES_THREE(new WorldPoint(3011, 5005, 1), null, 0, true),
  LEDGE_ONE(new WorldPoint(3004, 5003, 1), ObstacleObject.LEDGE_ONE, 2, false),
  WALK_ONE(new WorldPoint(2988, 5004, 1), null, 0, false),
  BLADE_ONE(new WorldPoint(2969, 5016, 1), null, 3, false),
  PENDULUM_TWO(new WorldPoint(2967, 5016, 1), null, 2, false),
  LEDGE_TWO(new WorldPoint(2958, 5028, 1), ObstacleObject.LEDGE_TWO, 1, false),
  WALK_TWO(new WorldPoint(2958, 5035, 1), null, 0, false),
  SPIKES_FOUR(new WorldPoint(2962, 5050, 1), null, 0, true),
  PASSAGEWAY_ONE(new WorldPoint(2963, 5056, 1), ObstacleObject.PASSAGEWAY_ONE, 0, false),
  BLADE_TWO(new WorldPoint(2957, 5072, 1), null, 3, false),
  WALK_THREE(new WorldPoint(2957, 5076, 1), null, 0, false),
  PASSAGEWAY_TWO(new WorldPoint(2955, 5090, 1), ObstacleObject.PASSAGEWAY_TWO, 0, false),
  WALK_FOUR(new WorldPoint(2955, 5098, 1), null, 0, false),
  PASSAGEWAY_THREE(new WorldPoint(2963, 5105, 1), ObstacleObject.PASSAGEWAY_THREE, 0, false),
  GRILL_TWO(new WorldPoint(2972, 5094, 1), ObstacleObject.GRILL_TWO, 1, false),
  WALK_FIVE(new WorldPoint(2972, 5093, 1), null, 0, false),
  LEDGE_THREE(new WorldPoint(2977, 5087, 1), ObstacleObject.LEDGE_THREE, 2, false),
  SEARCH(new WorldPoint(2991, 5087, 1), ObstacleObject.WALL, 1, false),
  WALK_SIX(new WorldPoint(2993, 5088, 1), null, 0, true),
  BLADE_THREE(new WorldPoint(2997, 5088, 1), null, 0, true),
  TILE(new WorldPoint(3006, 5088, 1), null, 0, false),
  GRILL_THREE(new WorldPoint(3024, 5082, 1), ObstacleObject.GRILL_THREE, 1, false),
  GRILL_FOUR(new WorldPoint(3031, 5079, 1), ObstacleObject.GRILL_FOUR, 1, false),
  GRILL_FIVE(new WorldPoint(3032, 5077, 1), ObstacleObject.GRILL_FIVE, 1, false),
  GRILL_SIX(new WorldPoint(3037, 5076, 1), ObstacleObject.GRILL_SIX, 1, false),
  GRILL_SEVEN(new WorldPoint(3040, 5079, 1), ObstacleObject.GRILL_SEVEN, 1, false),
  GRILL_EIGHT(new WorldPoint(3043, 5076, 1), ObstacleObject.GRILL_EIGHT, 1, false),
  GRILL_NINE(new WorldPoint(3044, 5068, 1), ObstacleObject.GRILL_NINE, 1, false),
  GRILL_TEN(new WorldPoint(3041, 5069, 1), ObstacleObject.GRILL_TEN, 1, false),
  GRILL_ELEVEN(new WorldPoint(3039, 5070, 1), ObstacleObject.GRILL_ELEVEN, 1, false),
  WALK_SEVEN(new WorldPoint(3038, 5068, 1), null, 0, false),
  WALL_RUN(new WorldPoint(3028, 5034, 1), null, 0, true),
  GRILL_TWELVE(new WorldPoint(3024, 5034, 1), ObstacleObject.GRILL_TWELVE, 1, false),
  GRILL_THIRTEEN(new WorldPoint(3014, 5033, 1), ObstacleObject.GRILL_THIRTEEN, 1, true),
  PENDULUM_THREE(new WorldPoint(3009, 5033, 1), null, 2, true),
  WALK_EIGHT(new WorldPoint(3000, 5034, 1), null, 0, false),
  SPIKES_FIVE(new WorldPoint(2992, 5045, 1), null, 0, true),
  WALK_NINE(new WorldPoint(2992, 5053, 1), null, 0, false),
  SPINNING_BLADES(new WorldPoint(2992, 5067, 1), null, 0, true),
  POWDER(new WorldPoint(2992, 5075, 1), null, 0, false),
  POWDER_DISTRACT(new WorldPoint(3009, 5063, 1), null, 0, true),
  PENDULUM_FOUR(new WorldPoint(3028, 5056, 1), null, 3, false),
  PENDULUM_FIVE(new WorldPoint(3028, 5051, 1), null, 3, false),
  CRACK_SAFE(new WorldPoint(3028, 5047, 1), null, 0, false);

  private final WorldPoint start;
  private final ObstacleObject obstacleObject;
  private final int waitTicks;
  private final boolean run;

  @Override
  public String toString() {
    return super.toString().substring(0, 1).toUpperCase()
        + super.toString().substring(1).toLowerCase().replaceAll("_", " ");
  }

  @Getter
  @RequiredArgsConstructor
  public enum ObstacleObject {
    CONTORTION_BARS(new WorldPoint(3049, 4997, 1), "Enter"),
    GRILL_ONE(new WorldPoint(3024, 5001, 1), "Open"),
    LEDGE_ONE(new WorldPoint(2993, 5004, 1), "Climb"),
    LEDGE_TWO(new WorldPoint(2958, 5031, 1), "Climb"),
    PASSAGEWAY_ONE(new WorldPoint(2957, 5069, 1), "Enter"),
    PASSAGEWAY_TWO(new WorldPoint(2955, 5095, 1), "Enter"),
    PASSAGEWAY_THREE(new WorldPoint(2972, 5097, 1), "Enter"),
    GRILL_TWO(new WorldPoint(2972, 5094, 1), "Open"),
    LEDGE_THREE(new WorldPoint(2983, 5087, 1), "Climb"),
    WALL(new WorldPoint(2993, 5087, 1), "Search"),
    GRILL_THREE(new WorldPoint(3030, 5079, 1), "Open"),
    GRILL_FOUR(new WorldPoint(3032, 5078, 1), "Open"),
    GRILL_FIVE(new WorldPoint(3036, 5076, 1), "Open"),
    GRILL_SIX(new WorldPoint(3039, 5079, 1), "Open"),
    GRILL_SEVEN(new WorldPoint(3042, 5076, 1), "Open"),
    GRILL_EIGHT(new WorldPoint(3044, 5069, 1), "Open"),
    GRILL_NINE(new WorldPoint(3041, 5068, 1), "Open"),
    GRILL_TEN(new WorldPoint(3040, 5070, 1), "Open"),
    GRILL_ELEVEN(new WorldPoint(3038, 5069, 1), "Open"),
    GRILL_TWELVE(new WorldPoint(3015, 5033, 1), "Open"),
    GRILL_THIRTEEN(new WorldPoint(3010, 5033, 1), "Open");

    private final WorldPoint location;
    private final String action;
  }
}
