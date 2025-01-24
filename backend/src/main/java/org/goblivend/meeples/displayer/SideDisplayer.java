package org.goblivend.meeples.displayer;

import org.goblivend.meeples.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

class SideDisplayer extends JPanel {
    private Game game;
    private final Supplier<Optional<Integer>> selectedMeepleSupplier;
    private final Integer selectedPlayer;
    private final Runnable selectedMeepleUpdater;
    private final Runnable frameRepainter;
    private final JButton selectedMeepleButton;
    private final JButton selectedPlayerButton;
    private final JButton resetTurnButton;
    private final JLabel logLabel;
    private final List<Integer> playerIds;
    private final List<PlayerDisplayer> playerDisplayers; // TODO Finish player displayers

    public SideDisplayer(Game game,
                         Supplier<Optional<Integer>> selectedMeepleSupplier,
                         Runnable selectedMeepleUpdater,
                         Integer selectedPlayer,
                         Runnable frameRepainter) {
        this.game = game;
        this.selectedMeepleSupplier = selectedMeepleSupplier;
        this.selectedPlayer = selectedPlayer;
        this.selectedMeepleUpdater = selectedMeepleUpdater;
        this.frameRepainter = frameRepainter;
        this.playerIds = game.getPlayerIds();

        this.selectedMeepleButton = new JButton();
        this.selectedMeepleButton.addActionListener((ActionEvent) -> {
            this.selectedMeepleUpdater.run();
            this.frameRepainter.run();
        });

        this.selectedPlayerButton = new JButton(String.format("Player: %d", playerIds.get(selectedPlayer)));

        this.resetTurnButton = new JButton("Reset Turn");
        this.resetTurnButton.addActionListener((ActionEvent) -> {
            this.game.resetCurrentMove(selectedPlayer);
            this.frameRepainter.run();
            this.setLogValue(null);
        });

        this.logLabel = new JLabel();
        this.logLabel.setFont(this.logLabel.getFont().deriveFont(20f));
        this.logLabel.setForeground(Color.RED);
        this.logLabel.setText("ABCDEFG");



        this.playerDisplayers = game.getPlayerIds()
                .stream()
                .map(p -> new PlayerDisplayer(game, p))
                .toList();


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(selectedMeepleButton);
        add(selectedPlayerButton);
        add(resetTurnButton);
        this.playerDisplayers.forEach(this::add);

        add(Box.createVerticalGlue());
        add(logLabel);
        revalidate();
        repaint();
    }

    public void setGame(Game game) {
        this.game = game;
        this.playerDisplayers.forEach(p -> p.setGame(game));
    }

    public void setLogValue(String log) {
        logLabel.setText(log);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (selectedMeepleSupplier != null) {
            selectedMeepleButton.setText(String.format("Meeple: %d", selectedMeepleSupplier.get().orElse(null)));
        }

        System.out.println("Repainting buttons");
        super.paintComponent(g);
    }


}
