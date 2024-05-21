package io.reisub.devious.utils.tasks.randoms;

import io.reisub.devious.utils.Config;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.widgets.Dialog;

public class FreakyForester extends RandomTask {
  @Inject private Config config;

  @Override
  public boolean isEnabled() {
    return config.enableFreakyForester();
  }

  @Override
  public boolean shouldDismiss() {
    return config.dismissFreakyForester();
  }

  @Override
  public boolean validate() {
    return validate("Freaky Forester");
  }

  @Override
  public void execute() {
    if (shouldDismiss()) {
      dismiss(getRandomNpc());
      return;
    }

    final NPC randomNpc = getRandomNpc();

    if (randomNpc != null && randomNpc.getInteracting().equals(Players.getLocal())) {
      enterRandomEvent();
    }
  }

  private void enterRandomEvent() {
    final WorldPoint current = Players.getLocal().getWorldLocation();

    getRandomNpc().interact("Talk-to");

    Time.sleepTicksUntil(Dialog::isViewingOptions, 10);

    Dialog.chooseOption(1); // that has three tails

    Time.sleepTicksUntil(() -> !Players.getLocal().getWorldLocation().equals(current), 10);
    Time.sleepTick();
  }
}
