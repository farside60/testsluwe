package io.reisub.devious.mining;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum Location {
  QUARRY_SANDSTONE(
      new WorldPoint(3165, 2914, 0),
      8,
      null,
      ImmutableSet.of(ObjectID.SANDSTONE_ROCKS),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(3166, 2913, 0), new WorldPoint(3166, 2914, 0)));
          add(new RockPosition(new WorldPoint(3164, 2915, 0), new WorldPoint(3165, 2915, 0)));
          add(new RockPosition(new WorldPoint(3164, 2914, 0), new WorldPoint(3165, 2914, 0)));
          add(new RockPosition(new WorldPoint(3167, 2913, 0), new WorldPoint(3166, 2914, 0)));
        }
      },
      true),
  QUARRY_GRANITE(
      new WorldPoint(3167, 2908, 0),
      8,
      null,
      ImmutableSet.of(ObjectID.GRANITE_ROCKS),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(3165, 2908, 0), new WorldPoint(3166, 2908, 0)));
          add(new RockPosition(new WorldPoint(3165, 2909, 0), new WorldPoint(3166, 2909, 0)));
          add(new RockPosition(new WorldPoint(3165, 2910, 0), new WorldPoint(3166, 2910, 0)));
          add(new RockPosition(new WorldPoint(3167, 2911, 0), new WorldPoint(3167, 2910, 0)));
        }
      },
      true),
  VOLCANIC_ASH(
      new WorldPoint(3788, 3772, 0),
      20,
      null,
      ImmutableSet.of(ObjectID.ASH_PILE),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(3794, 3773, 0), new WorldPoint(3794, 3772, 0)));
          add(new RockPosition(new WorldPoint(3789, 3769, 0), new WorldPoint(3789, 3770, 0)));
          add(new RockPosition(new WorldPoint(3781, 3774, 0), new WorldPoint(3782, 3774, 0)));
        }
      },
      false),
  SOFT_CLAY(
      new WorldPoint(3294, 12451, 0),
      20,
      new WorldPoint(3295, 6059, 0),
      ImmutableSet.of(ObjectID.SOFT_CLAY_ROCKS_36210),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(3293, 12451, 0), new WorldPoint(3294, 12451, 0)));
          add(new RockPosition(new WorldPoint(3294, 12450, 0), new WorldPoint(3294, 12451, 0)));
        }
      },
      false),
  TE_SALT(
      new WorldPoint(2845, 10334, 0),
      15,
      null,
      ImmutableSet.of(ObjectID.TE_SALT_ROCKS),
      null,
      false),
  EFH_SALT(
      new WorldPoint(2836, 10334, 0),
      15,
      null,
      ImmutableSet.of(ObjectID.EFH_SALT_ROCKS),
      null,
      false),
  URT_SALT(
      new WorldPoint(2833, 10340, 0),
      15,
      null,
      ImmutableSet.of(ObjectID.URT_SALT_ROCKS),
      null,
      false),
  BASALT(
      new WorldPoint(2841, 10339, 0),
      20,
      new WorldPoint(2871, 3936, 0),
      ImmutableSet.of(ObjectID.BASALT_ROCKS),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(2841, 10338, 0), new WorldPoint(2841, 10337, 0)));
          add(new RockPosition(new WorldPoint(2838, 10335, 0), new WorldPoint(2838, 10336, 0)));
        }
      },
      false),
  GEM_ROCK(
      new WorldPoint(2825, 2997, 0),
      20,
      new WorldPoint(2852, 2955, 0),
      ImmutableSet.of(ObjectID.GEM_ROCKS_11381, ObjectID.GEM_ROCKS),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(2820, 2998, 0), new WorldPoint(2820, 2999, 0)));
          add(new RockPosition(new WorldPoint(2821, 2998, 0), new WorldPoint(2821, 2999, 0)));
          add(new RockPosition(new WorldPoint(2821, 3000, 0), new WorldPoint(2821, 2999, 0)));
          add(new RockPosition(new WorldPoint(2823, 2999, 0), new WorldPoint(2822, 2999, 0)));
          add(new RockPosition(new WorldPoint(2823, 3002, 0), new WorldPoint(2823, 3001, 0)));
          add(new RockPosition(new WorldPoint(2825, 3001, 0), new WorldPoint(2824, 3001, 0)));
          add(new RockPosition(new WorldPoint(2825, 3003, 0), new WorldPoint(2824, 3003, 0)));
        }
      },
      false),
  AMETHYST_EAST(
      new WorldPoint(3027, 9707, 0),
      25,
      null,
      ImmutableSet.of(ObjectID.AMETHYST_CRYSTALS, ObjectID.AMETHYST_CRYSTALS_11389),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(3029, 9704, 0), new WorldPoint(3028, 9704, 0)));
          add(new RockPosition(new WorldPoint(3029, 9703, 0), new WorldPoint(3028, 9703, 0)));
          add(new RockPosition(new WorldPoint(3029, 9702, 0), new WorldPoint(3028, 9702, 0)));
          add(new RockPosition(new WorldPoint(3029, 9701, 0), new WorldPoint(3028, 9701, 0)));
          add(new RockPosition(new WorldPoint(3026, 9702, 0), new WorldPoint(3027, 9702, 0)));
          add(new RockPosition(new WorldPoint(3026, 9703, 0), new WorldPoint(3027, 9703, 0)));
          add(new RockPosition(new WorldPoint(3026, 9704, 0), new WorldPoint(3027, 9704, 0)));
        }
      },
      false),
  BARRONITE(
      new WorldPoint(2936, 5807, 0),
      30,
      null,
      ImmutableSet.of(ObjectID.BARRONITE_ROCKS, ObjectID.BARRONITE_ROCKS_41548),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(2936, 5806, 0), new WorldPoint(2936, 5807, 0)));
          add(new RockPosition(new WorldPoint(2937, 5806, 0), new WorldPoint(2937, 5807, 0)));
          add(new RockPosition(new WorldPoint(2941, 5809, 0), new WorldPoint(2940, 5809, 0)));
          add(new RockPosition(new WorldPoint(2941, 5810, 0), new WorldPoint(2940, 5810, 0)));
        }
      },
      false),
  MONASTERY_IRON(
      new WorldPoint(2605, 3234, 0),
      5,
      new WorldPoint(2654, 3283, 0),
      ImmutableSet.of(
          ObjectID.IRON_ROCKS,
          ObjectID.IRON_ROCKS_11365,
          ObjectID.IRON_ROCKS_36203,
          ObjectID.IRON_ROCKS_42833),
      new ArrayDeque<>() {
        {
          add(new RockPosition(new WorldPoint(2605, 3233, 0), new WorldPoint(2936, 5807, 0)));
          add(new RockPosition(new WorldPoint(2605, 3235, 0), new WorldPoint(2937, 5807, 0)));
        }
      },
      false);

  private final WorldPoint miningAreaPoint;
  private final int miningAreaDistance;
  private final WorldPoint bankPoint;
  private final Set<Integer> rockIds;
  private final ArrayDeque<RockPosition> rockPositions;
  private final boolean threeTick;
}
