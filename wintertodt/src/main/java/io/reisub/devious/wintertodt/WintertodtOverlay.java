package io.reisub.devious.wintertodt;

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

public class WintertodtOverlay extends OverlayPanel {
  private Wintertodt plugin;
  private Config config;

  @Inject
  private WintertodtOverlay(Wintertodt plugin, Config config) {
    this.plugin = plugin;
    this.config = config;

    setPosition(OverlayPosition.BOTTOM_LEFT);
    setLayer(OverlayLayer.ABOVE_SCENE);
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (plugin.isRunning() && config.enableOverlay()) {
      panelComponent.setPreferredSize(new Dimension(280, 0));
      panelComponent
          .getChildren()
          .add(TitleComponent.builder().text("Sluwe Wintertodt").color(Color.WHITE).build());

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
                  .left("Wins: " + plugin.getWonGames())
                  .leftColor(Color.GREEN)
                  .right("Losses: " + plugin.getLostGames())
                  .rightColor(Color.RED)
                  .build());

      int firemakingExperience = plugin.getExperienceGained(Skill.FIREMAKING);
      int woodcuttingExperience = plugin.getExperienceGained(Skill.WOODCUTTING);
      int fletchingExperience = plugin.getExperienceGained(Skill.FLETCHING);
      int constructionExperience = plugin.getExperienceGained(Skill.CONSTRUCTION);

      panelComponent
          .getChildren()
          .add(
              LineComponent.builder()
                  .left(
                      "Firemaking: "
                          + Stats.getFormattedExperience(firemakingExperience)
                          + " ("
                          + Stats.getExperiencePerHourInThousands(
                              firemakingExperience, plugin.getStartTime())
                          + "k/h)")
                  .leftColor(Color.YELLOW)
                  .right(
                      Skills.getLevel(Skill.FIREMAKING)
                          + " ("
                          + plugin.getLevelsGained(Skill.FIREMAKING)
                          + ")")
                  .rightColor(Color.YELLOW)
                  .build());

      panelComponent
          .getChildren()
          .add(
              LineComponent.builder()
                  .left(
                      "Woodcutting: "
                          + Stats.getFormattedExperience(woodcuttingExperience)
                          + " ("
                          + Stats.getExperiencePerHourInThousands(
                              woodcuttingExperience, plugin.getStartTime())
                          + "k/h)")
                  .leftColor(Color.ORANGE)
                  .right(
                      Skills.getLevel(Skill.WOODCUTTING)
                          + " ("
                          + plugin.getLevelsGained(Skill.WOODCUTTING)
                          + ")")
                  .rightColor(Color.ORANGE)
                  .build());

      panelComponent
          .getChildren()
          .add(
              LineComponent.builder()
                  .left(
                      "Fletching: "
                          + Stats.getFormattedExperience(fletchingExperience)
                          + " ("
                          + Stats.getExperiencePerHourInThousands(
                              fletchingExperience, plugin.getStartTime())
                          + "k/h)")
                  .leftColor(Color.YELLOW)
                  .right(
                      Skills.getLevel(Skill.FLETCHING)
                          + " ("
                          + plugin.getLevelsGained(Skill.FLETCHING)
                          + ")")
                  .rightColor(Color.YELLOW)
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
                  .leftColor(Color.ORANGE)
                  .right(
                      Skills.getLevel(Skill.CONSTRUCTION)
                          + " ("
                          + plugin.getLevelsGained(Skill.CONSTRUCTION)
                          + ")")
                  .rightColor(Color.ORANGE)
                  .build());
    }

    return super.render(graphics);
  }
}
