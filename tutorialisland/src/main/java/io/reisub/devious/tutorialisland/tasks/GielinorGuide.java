package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.NPC;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.widgets.Dialog;

public class GielinorGuide extends Task {
  @Override
  public String getStatus() {
    return "Completing Gielinor Guide";
  }

  @Override
  public boolean validate() {
    return TutorialIsland.getProgressVarp() <= 10;
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(2)) {
      talkToGuide();
    }

    if (TutorialIsland.isProgress(3)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_SETTINGS_TAB);
    }

    if (TutorialIsland.isProgress(7)) {
      TutorialIsland.talkTo("Gielinor Guide");
    }

    if (TutorialIsland.isProgress(10)) {
      TutorialIsland.open("Door");
    }
  }

  private void talkToGuide() {
    final NPC guide = NPCs.getNearest("Gielinor Guide");
    if (guide == null) {
      return;
    }

    guide.interact("Talk-to");
    Time.sleepTicksUntil(Dialog::isViewingOptions, 50);

    Dialog.chooseOption("I am an experienced player.");
    TutorialIsland.sleepUntilProgressUpdate(50);
  }
}
