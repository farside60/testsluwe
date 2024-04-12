package io.reisub.devious.utils.api;

import net.runelite.api.ItemID;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;

public class SluweInventory {
  public static boolean hasHammer() {
    return hasAnyItemInventoryOrEquipped(ItemID.HAMMER, ItemID.IMCANDO_HAMMER);
  }

  public static boolean hasAnyItemInventoryOrEquipped(int... ids) {
    for (int id : ids) {
      if (hasItemInventoryOrEquipped(id)) {
        return true;
      }
    }

    return false;
  }

  public static boolean hasAnyItemInventoryOrEquipped(String... names) {
    for (String name : names) {
      if (hasItemInventoryOrEquipped(name)) {
        return true;
      }
    }

    return false;
  }

  public static boolean hasAllItemsInventoryOrEquipped(int... ids) {
    for (int id : ids) {
      if (!hasItemInventoryOrEquipped(id)) {
        return false;
      }
    }

    return true;
  }

  public static boolean hasAllItemsInventoryOrEquipped(String... names) {
    for (String name : names) {
      if (!hasItemInventoryOrEquipped(name)) {
        return false;
      }
    }

    return true;
  }

  public static boolean hasItemInventoryOrEquipped(int id) {
    return Inventory.contains(id) || Equipment.contains(id);
  }

  public static boolean hasItemInventoryOrEquipped(String name) {
    return Inventory.contains(name) || Equipment.contains(name);
  }
}
