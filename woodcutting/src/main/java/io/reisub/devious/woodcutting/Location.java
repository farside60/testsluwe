package io.reisub.devious.woodcutting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum Location {
  YEW_WOODCUTTING_GUILD(
      new WorldPoint(1593, 3487, 0),
      20,
      new WorldPoint(1591, 3477, 0),
      ImmutableSet.of(
          ObjectID.YEW_TREE,
          ObjectID.YEW_TREE_5121,
          ObjectID.YEW_TREE_8503,
          ObjectID.YEW_TREE_8504,
          ObjectID.YEW_TREE_8505,
          ObjectID.YEW_TREE_8506,
          ObjectID.YEW_TREE_8507,
          ObjectID.YEW_TREE_8508,
          ObjectID.YEW_TREE_8509,
          ObjectID.YEW_TREE_8510,
          ObjectID.YEW_TREE_8511,
          ObjectID.YEW_TREE_8512,
          ObjectID.YEW_TREE_8513
      ),
      -1,
      -1,
      new ArrayDeque<>() {
        {
          add(new WorldPoint(1596, 3485, 0));
          add(new WorldPoint(1591, 3487, 0));
          add(new WorldPoint(1596, 3490, 0));
          add(new WorldPoint(1591, 3493, 0));
          add(new WorldPoint(1596, 3495, 0));
        }
      },
      null,
      false
  ),
  HARDWOOD_FOSSIL_ISLAND(
      new WorldPoint(3708, 3836, 0),
      10,
      new WorldPoint(3741, 3805, 0),
      ImmutableSet.of(
          NullObjectID.NULL_30480,
          NullObjectID.NULL_30481,
          NullObjectID.NULL_30482
      ),
      -1,
      -1,
      null,
      null,
      false
  ),
  TEAK_CASTLE_WARS(
      new WorldPoint(2334, 3048, 0),
      5,
      null,
      ImmutableSet.of(ObjectID.TEAK_TREE),
      0,
      0,
      null,
      ImmutableList.of(
              new WorldPoint(2335, 3047, 0),
              new WorldPoint(2335, 3049, 0),
              new WorldPoint(2334, 3048, 0),
              new WorldPoint(2335, 3046, 0),
              new WorldPoint(2333, 3050, 0)
      ),
      false
  ),
  ;

  private final WorldPoint woodcuttingAreaPoint;
  private final int woodcuttingAreaRadius;
  private final WorldPoint bankPoint;
  private final Set<Integer> treeIds;
  private final int xoffset;
  private final int yoffset;
  private final ArrayDeque<WorldPoint> treePositions;
  private final List<WorldPoint> burnLineStarts;
  private final boolean ordered;
}
