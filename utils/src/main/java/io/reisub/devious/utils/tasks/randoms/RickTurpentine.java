package io.reisub.devious.utils.tasks.randoms;

import io.reisub.devious.utils.Config;
import javax.inject.Inject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;

public class RickTurpentine extends RandomTask {
  @Inject private Config config;

  @Override
  public boolean isEnabled() {
    return config.enableRickTurpentine();
  }

  @Override
  public boolean shouldDismiss() {
    return config.dismissRickTurpentine();
  }

  @Override
  public boolean validate() {
    return validate("Rick Turpentine");
  }

  @Override
  public void execute() {
    if (shouldDismiss()) {
      dismiss(getRandomNpc());
      return;
    }

    final int freeSlots = Inventory.getFreeSlots();

    getRandomNpc().interact("Talk-to");

    Time.sleepTicksUntil(() -> Inventory.getFreeSlots() < freeSlots, 5);
    setLast();
  }
}
