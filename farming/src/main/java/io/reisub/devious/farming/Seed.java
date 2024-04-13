package io.reisub.devious.farming;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.unethicalite.api.game.Skills;

@RequiredArgsConstructor
public enum Seed {
  GUAM(ItemID.GUAM_SEED, 9),
  MARRENTILL(ItemID.MARRENTILL_SEED, 14),
  TARROMIN(ItemID.TARROMIN_SEED, 19),
  HARRALANDER(ItemID.HARRALANDER_SEED, 26),
  RANARR(ItemID.RANARR_SEED, 32),
  TOADFLAX(ItemID.TOADFLAX_SEED, 38),
  IRIT(ItemID.IRIT_SEED, 44),
  AVANTOE(ItemID.AVANTOE_SEED, 50),
  KWUARM(ItemID.KWUARM_SEED, 56),
  SNAPDRAGON(ItemID.SNAPDRAGON_SEED, 62),
  CADANTINE(ItemID.CADANTINE_SEED, 67),
  LANTADYME(ItemID.LANTADYME_SEED, 73),
  DWARF_WEED(ItemID.DWARF_WEED_SEED, 79),
  TORSTOL(ItemID.TORSTOL_SEED, 85);

  @Getter private final int id;
  private final int requiredLevel;

  public boolean canPlant() {
    return Skills.getLevel(Skill.FARMING) >= requiredLevel;
  }

  public static Seed getById(int id) {
    for (Seed seed : Seed.values()) {
      if (seed.getId() == id) {
        return seed;
      }
    }

    return null;
  }
}
