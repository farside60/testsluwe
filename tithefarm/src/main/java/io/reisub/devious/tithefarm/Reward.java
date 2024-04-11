package io.reisub.devious.tithefarm;

import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;

@Getter
@RequiredArgsConstructor
public enum Reward {
  GRICOLLERS_CAN(200, () -> Widgets.get(236, 9)),
  AUTO_WEED(50, () -> Widgets.get(236, 15)),
  FARMERS_TORSO(150, () -> Widgets.get(236, 26)),
  FARMERS_LEGS(125, () -> Widgets.get(236, 30)),
  FARMERS_HAT(75, () -> Widgets.get(236, 34)),
  FARMERS_BOOTS(50, () -> Widgets.get(236, 9)),
  HERB_SACK(250, () -> Widgets.get(236, 14)),
  SEED_BOX(250, () -> Widgets.get(236, 10)),
  HERB_BOX(30, () -> Widgets.get(236, 7, 4)),
  GRAPE_SEED(2, () -> Widgets.get(236, 7, 2));

  private final int cost;
  private final Supplier<Widget> widget;

  public static boolean shouldLeaveFarmToBuyAnything(Config config) {
    for (Reward reward : Reward.values()) {
      if (reward.shouldLeaveFarmToBuy(config)) {
        return true;
      }
    }

    return false;
  }

  public static boolean shouldBuyAnything(Config config) {
    for (Reward reward : Reward.values()) {
      if (reward.shouldBuy(config)) {
        return true;
      }
    }

    return false;
  }

  public boolean shouldLeaveFarmToBuy(Config config) {
    switch (this) {
      case HERB_BOX:
      case GRAPE_SEED:
        return Vars.getBit(Varbits.TITHE_FARM_POINTS) >= 900;
      default:
        return shouldBuy(config);
    }
  }

  public boolean shouldBuy(Config config) {
    if (!config.buyRewards()) {
      return false;
    }

    if (cost > Vars.getBit(Varbits.TITHE_FARM_POINTS)) {
      return false;
    }

    switch (this) {
      case GRICOLLERS_CAN:
        return config.buyGricollersCan() && !Inventory.contains(ItemID.GRICOLLERS_CAN);
      case AUTO_WEED:
        return config.buyAutoWeed() && Vars.getBit(Varbits.AUTOWEED) == 0;
      case FARMERS_TORSO:
        return config.buyFarmersTorso()
            && !Inventory.contains(ItemID.FARMERS_JACKET, ItemID.FARMERS_SHIRT);
      case FARMERS_LEGS:
        return config.buyFarmersLegs()
            && !Inventory.contains(
                ItemID.FARMERS_BORO_TROUSERS, ItemID.FARMERS_BORO_TROUSERS_13641);
      case FARMERS_HAT:
        return config.buyFarmersHat()
            && !Inventory.contains(ItemID.FARMERS_STRAWHAT, ItemID.FARMERS_STRAWHAT_13647);
      case FARMERS_BOOTS:
        return config.buyFarmersBoots()
            && !Inventory.contains(ItemID.FARMERS_BOOTS, ItemID.FARMERS_BOOTS_13645);
      case HERB_SACK:
        return config.buyHerbSack()
            && !Inventory.contains(ItemID.HERB_SACK)
            && Skills.getLevel(Skill.HERBLORE) >= 58;
      case SEED_BOX:
        return config.buySeedBox() && !Inventory.contains(ItemID.SEED_BOX);
      case HERB_BOX:
        return config.buyHerbBox();
      case GRAPE_SEED:
        return config.buyGrapeSeed();
      default:
        return false;
    }
  }
}
