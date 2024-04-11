package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.tithefarm.Config;
import io.reisub.devious.tithefarm.Reward;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Widgets;

public class BuyRewards extends Task {
  @Inject private TitheFarm plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Buying rewards";
  }

  @Override
  public boolean validate() {
    return !TitheFarm.isInTitheFarm() && Reward.shouldBuyAnything(config);
  }

  @Override
  public void execute() {
    final NPC gricoller = NPCs.getNearest(NpcID.FARMER_GRICOLLER);
    if (gricoller == null) {
      return;
    }

    gricoller.interact("Rewards");
    if (!Time.sleepTicksUntil(
        () -> {
          Widget shop = Widgets.get(236, 0);
          return shop != null && shop.isVisible();
        },
        20)) {
      return;
    }

    for (Reward reward : Reward.values()) {
      String buyAction;

      switch (reward) {
        case HERB_BOX:
          buyAction = "Buy-50";
          break;
        case GRAPE_SEED:
          buyAction = "Buy-x";
          break;
        default:
          buyAction = "Buy";
          break;
      }

      final int currentPoints = Vars.getBit(Varbits.TITHE_FARM_POINTS);

      if (reward.shouldBuy(config)) {
        final Widget itemWidget = reward.getWidget().get();
        if (itemWidget.hasAction(buyAction)) {
          itemWidget.interact(buyAction);

          if (reward == Reward.GRAPE_SEED) {
            Time.sleepTicksUntil(Dialog::isEnterInputOpen, 5);
            Dialog.enterAmount(500);
          }

          Time.sleepTicksUntil(() -> Vars.getBit(Varbits.TITHE_FARM_POINTS) != currentPoints, 5);

          if (Dialog.isOpen()) {
            Dialog.close();
          }
        }
      }

      final Item herbBox = Inventory.getFirst(ItemID.HERB_BOX);
      if (herbBox == null) {
        return;
      }

      herbBox.interact("Bank-all");
      Time.sleepTicksUntil(() -> !Inventory.contains(ItemID.HERB_BOX), 100);
    }
  }
}
