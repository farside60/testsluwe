package io.reisub.devious.fishingtrawler.tasks;

import io.reisub.devious.fishingtrawler.FishingTrawler;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.widgets.Widgets;

public class GetRewards extends Task {
  @Inject private FishingTrawler plugin;

  @Override
  public String getStatus() {
    return "Getting rewards";
  }

  @Override
  public boolean validate() {
    return Utils.isInRegion(FishingTrawler.PORT_REGION) && !plugin.isTakenReward();
  }

  @Override
  public void execute() {
    final TileObject rewardNet = TileObjects.getNearest(ObjectID.TRAWLER_NET_2483);
    if (rewardNet == null) {
      return;
    }

    rewardNet.interact("Inspect");

    Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.fromId(24051731)), 30);

    final Widget bankWidget = Widgets.fromId(24051731);
    if (bankWidget == null) {
      return;
    }

    bankWidget.interact("Bank-all");
    plugin.setTakenReward(true);
  }
}
