package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.coords.WorldPoint;

public class CookingGuide extends Task {
  @Override
  public String getStatus() {
    return "Completing Cooking Guide";
  }

  @Override
  public boolean validate() {
    return TutorialIsland.isProgressBetween(120, 170);
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(120)) {
      TutorialIsland.open("Gate");
    }

    if (TutorialIsland.isProgress(130)) {
      TutorialIsland.open("Door", new WorldPoint(3079, 3084, 0));
    }

    if (TutorialIsland.isProgress(140)) {
      TutorialIsland.talkTo("Master Chef");
    }

    if (TutorialIsland.isProgress(150)) {
      TutorialIsland.useItemOnItem("Pot of flour", "Bucket of water");
    }

    if (TutorialIsland.isProgress(160)) {
      TutorialIsland.useItemOnObject("Bread dough", "Range");
    }

    if (TutorialIsland.isProgress(170)) {
      TutorialIsland.open("Door", new WorldPoint(3072, 3090, 0));
    }
  }
}
