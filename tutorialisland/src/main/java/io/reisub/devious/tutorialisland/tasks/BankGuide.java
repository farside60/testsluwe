package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.items.Bank;

public class BankGuide extends Task {
  @Override
  public String getStatus() {
    return "Completing Bank Guide";
  }

  @Override
  public boolean validate() {
    return TutorialIsland.isProgressBetween(500, 540);
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(500)) {
      TutorialIsland.interactObject("Ladder", "Climb-up");
    }

    if (TutorialIsland.isProgress(510)) {
      TutorialIsland.interactObject("Bank booth", "Use");
    }

    if (TutorialIsland.isProgress(520)) {
      if (Bank.isOpen()) {
        Bank.close();
      }

      TutorialIsland.interactObject("Poll booth", "Use");
    }

    if (TutorialIsland.isProgress(525)) {
      TutorialIsland.open("Door", new WorldPoint(3125, 3124, 0));
    }

    if (TutorialIsland.isProgress(530)) {
      TutorialIsland.talkTo("Account Guide");
    }

    if (TutorialIsland.isProgress(531)) {
      TutorialIsland.interactWidget(164, 39);
    }

    if (TutorialIsland.isProgress(532)) {
      TutorialIsland.talkTo("Account Guide");
    }

    if (TutorialIsland.isProgress(540)) {
      TutorialIsland.open("Door", new WorldPoint(3130, 3124, 0));
    }
  }
}
