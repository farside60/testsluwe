package io.reisub.devious.utils.api.interaction.checks;


import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;

public class StartMovingCheck extends Check {
  public StartMovingCheck() {
    super(3, true);
  }

  public StartMovingCheck(int timeout, boolean ignoreFailure) {
    super(timeout, ignoreFailure);
  }

  @Override
  public void check() throws CheckFailedException {
    if (!Time.sleepTicksUntil(() -> Players.getLocal().isMoving(), getTimeout())) {
      throw new CheckFailedException("Player did not start moving", getTimeout());
    }
  }
}
