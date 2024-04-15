package io.reisub.devious.utils.api;

import java.util.List;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
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

  /**
   * Drops all items while making sure not to go over the maximum amount of actions per tick (10).
   * Sleeps on item 9 and 19 to allow for an extra action right before and right after dropping.
   *
   * @param items list of items that should be dropped
   */
  public static void dropAll(List<Item> items) {
    for (int i = 0; i < items.size(); i++) {
      if (i == 9 || i == 19) {
        Time.sleepTick();
      }

      items.get(i).drop();
    }
  }
}
