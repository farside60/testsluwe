package io.reisub.devious.autodialog.tasks;

import io.reisub.devious.utils.Utils;
import net.runelite.api.DialogOption;
import net.unethicalite.api.widgets.Dialog;

public class SeersStew extends DialogTask {
  @Override
  public String getStatus() {
    return "Buying stew";
  }

  @Override
  public boolean validate() {
    return config.seersStew()
        && Dialog.canContinue()
        && Dialog.getText().equals("Good morning, what would you like?")
        && Utils.isInRegion(10806);
  }

  @Override
  public void execute() {
    Dialog.invokeDialog(
        DialogOption.NPC_CONTINUE,
        DialogOption.CHAT_OPTION_ONE,
        DialogOption.PLAYER_CONTINUE,
        DialogOption.NPC_CONTINUE,
        DialogOption.CHAT_OPTION_THREE,
        DialogOption.PLAYER_CONTINUE,
        DialogOption.NPC_CONTINUE);
  }
}
