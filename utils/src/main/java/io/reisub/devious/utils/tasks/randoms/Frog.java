package io.reisub.devious.utils.tasks.randoms;

import io.reisub.devious.utils.Config;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Dialog;

public class Frog extends RandomTask {
  @Inject private Config config;

  @Override
  public boolean isEnabled() {
    return config.enableFrog();
  }

  @Override
  public boolean shouldDismiss() {
    return config.dismissFrog();
  }

  @Override
  public boolean validate() {
    return validate("Frog");
  }

  @Override
  public void execute() {
    if (shouldDismiss()) {
      dismiss(getRandomNpc());
      return;
    }

    final NPC royalFrog = NPCs.getNearest(n -> n.getId() == 5431);
    royalFrog.interact("Talk-to");

    if (!Time.sleepTicksUntil(Dialog::isViewingOptions, 30)) {
      return;
    }

    Dialog.chooseOption(1);

    Time.sleepTicksUntil(() -> Inventory.contains("Frog token"), 20);
    setLast();
  }
}
