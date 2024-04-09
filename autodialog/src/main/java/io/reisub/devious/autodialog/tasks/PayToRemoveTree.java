package io.reisub.devious.autodialog.tasks;

import net.unethicalite.api.widgets.Dialog;

public class PayToRemoveTree extends DialogTask {
  @Override
  public String getStatus() {
    return "Paying to remove tree";
  }

  @Override
  public boolean validate() {
    return config.payToRemoveTree()
        && Dialog.canContinue()
        && Dialog.getText().equals("Pay 200 Coins to have your tree chopped down?");
  }

  @Override
  public void execute() {
    Dialog.chooseOption(1);
  }
}
