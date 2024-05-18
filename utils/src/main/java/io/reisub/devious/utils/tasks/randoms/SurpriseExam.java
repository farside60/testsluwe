package io.reisub.devious.utils.tasks.randoms;

import io.reisub.devious.utils.Config;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.widgets.Dialog;

public class SurpriseExam extends RandomTask {
  @Inject private Config config;

  @Override
  public boolean isEnabled() {
    return config.enableSurpriseExam();
  }

  @Override
  public boolean shouldDismiss() {
    return config.dismissSurpriseExam();
  }

  @Override
  public boolean validate() {
    return validate("Dunce");
  }

  @Override
  public void execute() {
    if (shouldDismiss()) {
      dismiss(getRandomNpc());
      return;
    }

    getRandomNpc().interact("Talk-to");

    if (!Time.sleepTicksUntil(Dialog::isViewingOptions, 30)) {
      return;
    }

    Dialog.chooseOption(1);

    Time.sleepTicksUntil(() -> NPCs.getNearest(NpcID.MR_MORDAUT) != null, 20);
  }
}
