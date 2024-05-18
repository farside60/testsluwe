package io.reisub.devious.utils.tasks.randoms;

import io.reisub.devious.utils.Config;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;

public class Reward extends RandomTask {
  @Inject private Config config;
  private final int rewardBoxGroupId = 240;

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean shouldDismiss() {
    return false;
  }

  @Override
  public String getStatus() {
    return "Handling random event reward";
  }

  @Override
  public boolean validate() {
    return Inventory.contains("Book of knowledge");
  }

  @Override
  public void execute() {
    final Item reward = Inventory.getFirst("Book of knowledge", "Lamp");
    if (reward == null) {
      return;
    }

    reward.interact(0);

    Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(rewardBoxGroupId, 0)), 10);

    config.rewardSkill().selectRewardWidget();
    Time.sleepTick();

    final Widget confirmWidget = Widgets.get(rewardBoxGroupId, 26);
    if (!Widgets.isVisible(confirmWidget)) {
      return;
    }

    confirmWidget.interact("Confirm");

    Time.sleepTicksUntil(() -> !Widgets.isVisible(Widgets.get(rewardBoxGroupId, 0)), 10);
  }
}
