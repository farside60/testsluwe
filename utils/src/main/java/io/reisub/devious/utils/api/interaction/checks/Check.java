package io.reisub.devious.utils.api.interaction.checks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Check {
  @Getter private final int timeout;
  @Getter private final boolean ignoreFailure;

  public abstract void check() throws CheckFailedException;
}
