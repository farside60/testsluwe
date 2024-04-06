package io.reisub.devious.utils.api.interaction.checks;

import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.api.Activity;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public abstract class ActivityCheck extends Check {
  private final TickScript script;
  private final List<Activity> activities;

  public ActivityCheck(
      int timeout, boolean ignoreFailure, TickScript script, Activity... activities) {
    super(timeout, ignoreFailure);
    this.script = script;
    this.activities = Arrays.asList(activities);
  }
}
