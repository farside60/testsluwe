package io.reisub.devious.utils.api.interaction.checks;

import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.api.Activity;

public class IdleActivityCheck extends CurrentActivityCheck {
  public IdleActivityCheck(int timeout, TickScript script) {
    this(timeout, false, script);
  }

  public IdleActivityCheck(int timeout, boolean ignoreFailure, TickScript script) {
    super(timeout, ignoreFailure, script, Activity.IDLE);
  }
}
