package io.reisub.devious.autowelcomescreen;

import io.reisub.devious.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.script.blocking_events.WelcomeScreenEvent;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;
import org.pf4j.Extension;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Auto Welcome Screen",
    description = "Saves a click!")
@Slf4j
public class AutoWelcomeScreen extends Plugin {
  @Subscribe
  private void onWidgetHiddenChanged(WidgetLoaded e) {
    int group = e.getGroupId();
    if (group == 378 || group == 413) {
      Widget playButton = WelcomeScreenEvent.getPlayButton();
      if (Widgets.isVisible(playButton)) {
        Static.getClient().invokeWidgetAction(1, playButton.getId(), -1, -1, "");
      }
    }
  }
}
