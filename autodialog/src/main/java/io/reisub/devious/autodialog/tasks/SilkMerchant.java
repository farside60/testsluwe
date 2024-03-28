package io.reisub.devious.autodialog.tasks;

import net.runelite.api.DialogOption;
import net.unethicalite.api.widgets.Dialog;

public class SilkMerchant extends DialogTask {
  @Override
  public String getStatus() {
    return "Selling silk";
  }

  @Override
  public boolean validate() {
    return config.silkMerchant()
        && Dialog.canContinue()
        && Dialog.getText().startsWith("Hello. I have some fine silk");
  }

  @Override
  public void execute() {
    Dialog.invokeDialog(
        DialogOption.PLAYER_CONTINUE,
        DialogOption.NPC_CONTINUE,
        DialogOption.CHAT_OPTION_THREE,
        DialogOption.PLAYER_CONTINUE,
        DialogOption.NPC_CONTINUE,
        DialogOption.CHAT_OPTION_TWO,
        DialogOption.PLAYER_CONTINUE,
        DialogOption.NPC_CONTINUE);
  }
}
