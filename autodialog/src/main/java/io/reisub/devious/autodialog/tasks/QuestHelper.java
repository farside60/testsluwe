package io.reisub.devious.autodialog.tasks;

import net.unethicalite.api.widgets.Dialog;

public class QuestHelper extends DialogTask {
  private int index;
  
  @Override
  public String getStatus() {
    return "Selecting correct quest response";
  }

  @Override
  public boolean validate() {
    if (!config.questHelper()) {
      return false;
    }

    index = getIndexStartsWith("[");

    return index != -1;
  }

  @Override
  public void execute() {
    Dialog.chooseOption(index);
  }
}
