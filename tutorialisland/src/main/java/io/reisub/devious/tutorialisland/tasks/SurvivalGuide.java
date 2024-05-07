package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.widgets.WidgetInfo;

public class SurvivalGuide extends Task {
  @Override
  public String getStatus() {
    return "Completing Survival Guide";
  }

  @Override
  public boolean validate() {
    return TutorialIsland.isProgressBetween(20, 90);
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(20)) {
      TutorialIsland.talkTo("Survival Expert");
    }

    if (TutorialIsland.isProgress(30)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_INVENTORY_TAB);
    }

    if (TutorialIsland.isProgress(40)) {
      TutorialIsland.interactNpc("Fishing spot", "Net");
    }

    if (TutorialIsland.isProgress(50)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_STATS_TAB);
    }

    if (TutorialIsland.isProgress(60)) {
      TutorialIsland.talkTo("Survival Expert");
    }

    if (TutorialIsland.isProgress(70)) {
      TutorialIsland.interactObject("Tree", "Chop down");
    }

    if (TutorialIsland.isProgress(80)) {
      TutorialIsland.useItemOnItem("Logs", "Tinderbox");
    }

    if (TutorialIsland.isProgress(90)) {
      TutorialIsland.useItemOnObject("Raw shrimps", "Fire");
    }
  }
}
