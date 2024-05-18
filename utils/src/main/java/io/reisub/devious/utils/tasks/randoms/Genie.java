package io.reisub.devious.utils.tasks.randoms;

import io.reisub.devious.utils.Config;
import javax.inject.Inject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;

public class Genie extends RandomTask {
  @Inject private Config config;

  @Override
  public boolean isEnabled() {
    return config.enableGenie();
  }

  @Override
  public boolean shouldDismiss() {
    return config.dismissGenie();
  }

  @Override
  public boolean validate() {
    return validate("Genie");
  }

  @Override
  public void execute() {
    if (shouldDismiss()) {
      dismiss(getRandomNpc());
      return;
    }

    getRandomNpc().interact("Talk-to");

    Time.sleepTicksUntil(() -> Inventory.contains("Lamp"), 20);
    setLast();
  }
}
