package io.reisub.devious.utils.api.interaction;

import io.reisub.devious.utils.api.interaction.checks.Check;
import io.reisub.devious.utils.api.interaction.checks.CheckFailedException;
import java.util.Arrays;
import java.util.List;
import net.unethicalite.api.Interactable;
import net.unethicalite.api.exception.InteractionException;

public class Interaction {
  private final Interactable interactable;
  private final int actionIndex;
  private final String action;
  private final List<Check> checkList;

  public Interaction(Interactable interactable, Check... checks) {
    this(interactable, 0, checks);
  }

  public Interaction(Interactable interactable, String action, Check... checks) {
    this.interactable = interactable;
    this.actionIndex = interactable == null ? -1 : interactable.getActionIndex(action);
    this.action = action;
    this.checkList = Arrays.asList(checks);
  }

  public Interaction(Interactable interactable, int actionIndex, Check... checks) {
    this.interactable = interactable;
    this.actionIndex = actionIndex;
    this.action = null;
    this.checkList = Arrays.asList(checks);
  }

  public void interact() throws InteractionException {
    String errorMessage = null;

    if (interactable == null) {
      errorMessage = "Interactable is null";
    } else if (actionIndex == -1) {
      errorMessage = String.format("Action '%s' not found on interactable", action);
    } else if (actionIndex >= interactable.getActions().length) {
      errorMessage = String.format("Action index '%d' is out of bounds", actionIndex);
    }

    if (errorMessage != null) {
      errorMessage =
          String.format(
              "%s\ninteractable: %s, actionIndex: %d, action: %s, checkList: %s",
              errorMessage, interactable, actionIndex, action, checkList);
      throw new InteractionException(errorMessage);
    }

    interactable.interact(actionIndex);

    if (checkList == null) {
      return;
    }

    for (Check check : checkList) {
      try {
        check.check();
      } catch (CheckFailedException checkFailedException) {
        if (!check.isIgnoreFailure()) {
          throw checkFailedException;
        }
      }
    }
  }
}
