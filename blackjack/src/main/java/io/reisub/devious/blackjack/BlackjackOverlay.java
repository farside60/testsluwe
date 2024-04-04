package io.reisub.devious.blackjack;

import io.reisub.devious.utils.api.Stats;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import javax.inject.Inject;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.unethicalite.api.game.Skills;

public class BlackjackOverlay extends OverlayPanel {
  private final Blackjack plugin;
  private final Config config;

  @Inject
  private BlackjackOverlay(Blackjack plugin, Config config) {
    this.plugin = plugin;
    this.config = config;

    setPosition(OverlayPosition.BOTTOM_LEFT);
    setLayer(OverlayLayer.ABOVE_SCENE);
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (!plugin.isRunning() || !config.enableOverlay()) {
      return super.render(graphics);
    }

    panelComponent.setPreferredSize(new Dimension(280, 0));
    panelComponent
        .getChildren()
        .add(TitleComponent.builder().text("Sluwe Blackjack").color(Color.WHITE).build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Running: " + plugin.getTimeRunning())
                .leftColor(Color.YELLOW)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Knocked out: " + plugin.getSuccessfulKnockOuts())
                .leftColor(Color.GREEN)
                .right("Glanced: " + plugin.getFailedKnockOuts())
                .rightColor(Color.RED)
                .build());

    DecimalFormat df = new DecimalFormat("#.00");

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left(
                    "Knock out rate: " + df.format(plugin.getKnockoutRate()) + "%")
                .leftColor(Color.YELLOW)
                .build());

    int thievingExperience = plugin.getExperienceGained(Skill.THIEVING);

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left(
                    "Thieving: "
                        + Stats.getFormattedExperience(thievingExperience)
                        + " ("
                        + Stats.getExperiencePerHourInThousands(
                            thievingExperience, plugin.getStartTime())
                        + "k/h)")
                .leftColor(Color.ORANGE)
                .right(
                    Skills.getLevel(Skill.THIEVING)
                        + " ("
                        + plugin.getLevelsGained(Skill.THIEVING)
                        + ")")
                .rightColor(Color.ORANGE)
                .build());

    return super.render(graphics);
  }
}
