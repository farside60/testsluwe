package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.movement.Movement;

public class QuestGuide extends Task {
  @Override
  public String getStatus() {
    return "Completing Quest Guide";
  }

  @Override
  public boolean validate() {
    return TutorialIsland.isProgressBetween(200, 250);
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(200)) {
      toggleRun();
    }

    if (TutorialIsland.isProgress(210)) {
      final WorldPoint destination = new WorldPoint(3075, 3123, 0);
      if (Players.getLocal().distanceTo(destination) > 5) {
        SluweMovement.walkTo(destination, 2);
      }
      TutorialIsland.open("Door");
    }

    if (TutorialIsland.isProgress(220)) {
      TutorialIsland.talkTo("Quest Guide");
    }

    if (TutorialIsland.isProgress(230)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_QUESTS_TAB);
    }

    if (TutorialIsland.isProgress(240)) {
      TutorialIsland.talkTo("Quest Guide");
    }

    if (TutorialIsland.isProgress(250)) {
      TutorialIsland.interactObject("Ladder", "Climb-down");
    }
  }

  private void toggleRun() {
    if (Movement.isRunEnabled()) {
      Movement.toggleRun();
      Movement.toggleRun();
    } else {
      Movement.toggleRun();
    }

    TutorialIsland.sleepUntilProgressUpdate(2);
  }
}
