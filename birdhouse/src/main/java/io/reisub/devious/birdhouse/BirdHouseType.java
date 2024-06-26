package io.reisub.devious.birdhouse;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@RequiredArgsConstructor
@Getter
public enum BirdHouseType {
  NORMAL("Bird House", ItemID.BIRD_HOUSE),
  OAK("Oak Bird House", ItemID.OAK_BIRD_HOUSE),
  WILLOW("Willow Bird House", ItemID.WILLOW_BIRD_HOUSE),
  TEAK("Teak Bird House", ItemID.TEAK_BIRD_HOUSE),
  MAPLE("Maple Bird House", ItemID.MAPLE_BIRD_HOUSE),
  MAHOGANY("Mahogany Bird House", ItemID.MAHOGANY_BIRD_HOUSE),
  YEW("Yew Bird House", ItemID.YEW_BIRD_HOUSE),
  MAGIC("Magic Bird House", ItemID.MAGIC_BIRD_HOUSE),
  REDWOOD("Redwood Bird House", ItemID.REDWOOD_BIRD_HOUSE);

  private final String name;
  private final int itemId;

  @Nullable
  public static BirdHouseType fromVarpValue(int varp) {
    int index = (varp - 1) / 3;

    if (varp <= 0 || index >= values().length) {
      return null;
    }

    return values()[index];
  }
}
