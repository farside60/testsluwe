package io.reisub.devious.herblore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;

@AllArgsConstructor
@Getter
public enum Secondary {
  ALL(-1, -1),
  UNICORN_DUST(ItemID.UNICORN_HORN, ItemID.UNICORN_HORN_DUST),
  CHOCOLATE_DUST(ItemID.CHOCOLATE_BAR, ItemID.CHOCOLATE_DUST),
  KEBBIT_TEETH_DUST(ItemID.KEBBIT_TEETH, ItemID.KEBBIT_TEETH_DUST),
  CRUSHED_NEST(ItemID.BIRD_NEST_5075, ItemID.CRUSHED_NEST),
  GOAT_HORN_DUST(ItemID.DESERT_GOAT_HORN, ItemID.GOAT_HORN_DUST),
  DRAGON_SCALE_DUST(ItemID.BLUE_DRAGON_SCALE, ItemID.DRAGON_SCALE_DUST),
  LAVA_SCALE_SHARD(ItemID.LAVA_SCALE, ItemID.LAVA_SCALE_SHARD),
  CRUSHED_SUPERIOR_DRAGON_BONES(ItemID.SUPERIOR_DRAGON_BONES, ItemID.CRUSHED_SUPERIOR_DRAGON_BONES),
  CRYSTAL_DUST(ItemID.CRYSTAL_SHARD, ItemID.CRYSTAL_DUST),
  NIHIL_DUST(ItemID.NIHIL_SHARD, ItemID.NIHIL_DUST);

  private final int originalId;
  private final int groundId;
}
