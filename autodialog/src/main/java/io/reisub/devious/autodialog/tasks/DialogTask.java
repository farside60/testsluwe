package io.reisub.devious.autodialog.tasks;

import io.reisub.devious.autodialog.Config;
import io.reisub.devious.utils.tasks.Task;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.widgets.Dialog;

public abstract class DialogTask extends Task {
  @Inject protected Config config;

  protected int getIndexStartsWith(String startsWith) {
    List<Widget> options = Dialog.getOptions();
    if (options.isEmpty()) {
      return -1;
    }

    for (Widget option : options) {
      if (option.getText().startsWith(startsWith)) {
        return option.getIndex();
      }
    }

    return -1;
  }

  protected int getIndex(String string) {
    List<Widget> options = Dialog.getOptions();
    if (options.isEmpty()) {
      return -1;
    }

    for (Widget option : options) {
      if (option.getText().equals(string)) {
        return option.getIndex();
      }
    }

    return -1;
  }
}
