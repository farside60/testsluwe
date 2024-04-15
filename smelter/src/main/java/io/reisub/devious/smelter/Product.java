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
  BRONZE(1, ImmutableMap.of(ItemID.COPPER_ORE, 14, ItemID.TIN_ORE, 14)),
  IRON(3, ImmutableMap.of(ItemID.IRON_ORE, 28)),
  SILVER(4, ImmutableMap.of(ItemID.SILVER_ORE, 28)),
  STEEL(5, ImmutableMap.of(ItemID.IRON_ORE, 9, ItemID.COAL, 18)),
  GOLD(6, ImmutableMap.of(ItemID.GOLD_ORE, 28)),
  MITHRIL(7, ImmutableMap.of(ItemID.MITHRIL_ORE, 5, ItemID.COAL, 20)),
  ADAMANTITE(8, ImmutableMap.of(ItemID.ADAMANTITE_ORE, 4, ItemID.COAL, 24)),
  RUNITE(9, ImmutableMap.of(ItemID.RUNITE_ORE, 3, ItemID.COAL, 24)),
  RUBY_BRACELET(54, ImmutableMap.of(ItemID.GOLD_BAR, 13, ItemID.RUBY, 13), ItemID.BRACELET_MOULD);

  private final int productionIndex;
  private final Map<Integer, Integer> materials;
  private final int mouldId;

  Product(int productionIndex, Map<Integer, Integer> materials) {
    this(productionIndex, materials, -1);
  }

  public boolean hasMaterials() {
    if (mouldId != -1 && !Inventory.contains(mouldId)) {
      return false;
    }

    for (int id : materials.keySet()) {
      if (!Inventory.contains(id)) {
        return false;
      }
    }

    return true;
  }
}
