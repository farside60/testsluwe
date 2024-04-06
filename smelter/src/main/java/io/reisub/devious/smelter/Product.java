package io.reisub.devious.smelter;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.unethicalite.api.items.Inventory;

@Getter
@RequiredArgsConstructor
public enum Product {
  MOLTEN_GLASS(1, ImmutableMap.of(ItemID.SODA_ASH, 14, ItemID.BUCKET_OF_SAND, 14)),
  BRONZE(1, ImmutableMap.of(ItemID.COPPER_ORE, 1, ItemID.TIN_ORE, 1)),
  IRON(3, ImmutableMap.of(ItemID.IRON_ORE, 1)),
  SILVER(4, ImmutableMap.of(ItemID.SILVER_ORE, 1)),
  STEEL(5, ImmutableMap.of(ItemID.IRON_ORE, 1, ItemID.COAL, 2)),
  GOLD(6, ImmutableMap.of(ItemID.GOLD_ORE, 1)),
  MITHRIL(7, ImmutableMap.of(ItemID.MITHRIL_ORE, 1, ItemID.COAL, 4)),
  ADAMANTITE(8, ImmutableMap.of(ItemID.ADAMANTITE_ORE, 1, ItemID.COAL, 6)),
  RUNITE(9, ImmutableMap.of(ItemID.RUNITE_ORE, 1, ItemID.COAL, 8));

  private final int productionIndex;
  private final Map<Integer, Integer> materials;

  public boolean hasMaterials() {
    for (int id : materials.keySet()) {
      if (!Inventory.contains(id)) {
        return false;
      }
    }

    return true;
  }
}
