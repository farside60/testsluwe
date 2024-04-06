package io.reisub.devious.utils.api.interaction.checks;

import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.api.Activity;
import java.util.stream.Collectors;
import net.unethicalite.api.commons.Time;

public class CurrentActivityCheck extends ActivityCheck {
  public CurrentActivityCheck(int timeout, TickScript script, Activity... activities) {
    this(timeout, false, script, activities);
  }

  public CurrentActivityCheck(
      int timeout, boolean ignoreFailure, TickScript script, Activity... activities) {
    super(timeout, ignoreFailure, script, activities);
  }

  @Override
  public void check() throws CheckFailedException {
    if (getActivities() == null || getActivities().isEmpty()) {
      return;
    }

    if (!Time.sleepTicksUntil(
        () -> {
          for (Activity activity : getActivities()) {
            if (getScript().isCurrentActivity(activity)) {
              return true;
            }
          }

          return false;
        },
        getTimeout())) {
      final String activitiesString =
          getActivities().stream().map(Activity::getName).collect(Collectors.joining(","));

      throw new CheckFailedException(
          String.format("Timed out waiting for activity to change to '%s'", activitiesString),
          getTimeout());
    }
  }
}
