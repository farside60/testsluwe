package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;

public class PrayerGuide extends Task {
  @Override
  public String getStatus() {
    return "Completing Prayer Guide";
  }

  @Override
  public boolean validate() {
    return TutorialIsland.isProgressBetween(550, 610);
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(550)) {
      SluweMovement.walkTo(new WorldPoint(3126, 3107, 0));
      TutorialIsland.talkTo("Brother Brace");
    }

    if (TutorialIsland.isProgress(560)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_PRAYER_TAB);
    }

    if (TutorialIsland.isProgress(570)) {
      TutorialIsland.talkTo("Brother Brace");
    }

    if (TutorialIsland.isProgress(580)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_FRIEND_TAB);
    }

    if (TutorialIsland.isProgress(600)) {
      TutorialIsland.talkTo("Brother Brace");
    }

    if (TutorialIsland.isProgress(610)) {
      TutorialIsland.open("Door");
    }
  }
}
