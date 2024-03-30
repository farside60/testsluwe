package io.reisub.devious.tempoross;

import io.reisub.devious.utils.api.Stats;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.unethicalite.api.game.Skills;

public class TemporossOverlay extends OverlayPanel {
  private final Tempoross plugin;
  private final Config config;

  @Inject
  private TemporossOverlay(Tempoross plugin, Config config) {
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
        .add(TitleComponent.builder().text("Sluwe Tempoross").color(Color.WHITE).build());

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
                .left("Wins: " + plugin.getGamesWon())
                .leftColor(Color.GREEN)
                .right("Losses: " + plugin.getGamesLost())
                .rightColor(Color.RED)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left("Average round time: " + plugin.getAverageRoundTime())
                .leftColor(Color.YELLOW)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left(
                    "Permits earned: "
                        + plugin.getPermitsEarned()
                        + " ("
                        + Stats.getExperiencePerHour(
                            plugin.getPermitsEarned(), plugin.getStartTime())
                        + "/h)")
                .leftColor(Color.ORANGE)
                .build());

    int fishingExperience = plugin.getExperienceGained(Skill.FISHING);
    int cookingExperience = plugin.getExperienceGained(Skill.COOKING);
    int constructionExperience = plugin.getExperienceGained(Skill.CONSTRUCTION);

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left(
                    "Fishing: "
                        + Stats.getFormattedExperience(fishingExperience)
                        + " ("
                        + Stats.getExperiencePerHourInThousands(
                            fishingExperience, plugin.getStartTime())
                        + "k/h)")
                .leftColor(Color.YELLOW)
                .right(
                    Skills.getLevel(Skill.FISHING)
                        + " ("
                        + plugin.getLevelsGained(Skill.FISHING)
                        + ")")
                .rightColor(Color.YELLOW)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left(
                    "Cooking: "
                        + Stats.getFormattedExperience(cookingExperience)
                        + " ("
                        + Stats.getExperiencePerHourInThousands(
                            cookingExperience, plugin.getStartTime())
                        + "k/h)")
                .leftColor(Color.ORANGE)
                .right(
                    Skills.getLevel(Skill.COOKING)
                        + " ("
                        + plugin.getLevelsGained(Skill.COOKING)
                        + ")")
                .rightColor(Color.ORANGE)
                .build());

    panelComponent
        .getChildren()
        .add(
            LineComponent.builder()
                .left(
                    "Construction: "
                        + Stats.getFormattedExperience(constructionExperience)
                        + " ("
                        + Stats.getExperiencePerHourInThousands(
                            constructionExperience, plugin.getStartTime())
                        + "k/h)")
                .leftColor(Color.YELLOW)
                .right(
                    Skills.getLevel(Skill.CONSTRUCTION)
                        + " ("
                        + plugin.getLevelsGained(Skill.CONSTRUCTION)
                        + ")")
                .rightColor(Color.YELLOW)
                .build());

    return super.render(graphics);
  }
}
