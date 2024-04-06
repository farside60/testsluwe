package io.reisub.devious.utils.api.interaction.checks;

import net.unethicalite.api.exception.InteractionException;

public class CheckFailedException extends InteractionException {
  public CheckFailedException(String message, int timeout) {
    super(String.format("Check failed: %s after %d ticks", message, timeout));
  }

  public CheckFailedException(String message) {
    super(String.format("Check failed: %s", message));
  }
}
