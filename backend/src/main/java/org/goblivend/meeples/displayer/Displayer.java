package org.goblivend.meeples.displayer;

import lombok.Getter;
import org.goblivend.meeples.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.String.format;

public class Displayer {
    @Getter
    private Mode mode = Mode.GAME;
    private Game game;
    @Getter
    private Integer playerId;
    private Supplier<Optional<Integer>> selectedMeepleSupplier;
    private Runnable selectedMeepleUpdater;
    private JFrame frame;
    private GameDisplayer gameDisplayer;
    private JPanel gameModePanel;
    private SideDisplayer sideDisplayer;
    private Consumer<String> logUpdater;

    public Displayer(Game game, Integer playerId) {
        this.game = game;
        this.playerId = playerId;
    }

    public void updateMode() {
        this.mode = Mode.values()[(mode.mode + 1) % (Mode.values().length)];
    }

    public void create() {
        createFrame();
        createSizeUpdateListener();
        createGameDisplayer();
        createGameModePanel();
        createSideDisplayer();

        createMouseListener();
        createKeyInputMap();

        frame.getContentPane().add(this.gameModePanel, BorderLayout.PAGE_START);
        frame.getContentPane().add(this.gameDisplayer, BorderLayout.LINE_START);
        frame.getContentPane().add(this.sideDisplayer, BorderLayout.CENTER);

        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(true);

        // TODO: Set minimum frame size dynamically from the sum of all subcomponents' minimum size
        frame.setMinimumSize(new Dimension(400, 250));

        updateSize();
    }

    public void setGame(Game game) {
        this.game = game;
        this.gameDisplayer.setGame(game);
        this.sideDisplayer.setGame(game);
    }

    private void createFrame() {
        this.frame = new JFrame();
        frame.setTitle("Mutant Meeples");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
    }

    private void createGameModePanel() {
        this.gameModePanel = new JPanel();
        var button = new JButton();
        button.setText(format("Mode: %s", mode.name()));
        button.addActionListener((ActionEvent e) -> {
            updateMode();
            button.setText(format("Mode: %s", mode.name()));
            frame.repaint();
        });
        this.gameModePanel.add(button);
    }

    private void createGameDisplayer() {
        this.gameDisplayer = new GameDisplayer(game, this::updateLog, playerId);
        this.selectedMeepleSupplier = this.gameDisplayer::getSelectedMeeple;
        this.selectedMeepleUpdater = this.gameDisplayer::updateSelectedMeeple;
    }

    private void createSideDisplayer() {
        this.sideDisplayer = new SideDisplayer(
                this.game,
                this.selectedMeepleSupplier,
                this.selectedMeepleUpdater,
                playerId,
                this.frame::repaint
        );
        logUpdater = this.sideDisplayer::setLogValue;
    }

    private void updateSize() {
        int minSize = Math.min(frame.getWidth(), frame.getHeight());

        gameDisplayer.setPreferredSize(new Dimension(minSize, minSize));
//        gameDisplayer.updateSize();
        frame.repaint();
    }

    private void createSizeUpdateListener() {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();
            }
        });

        frame.addWindowStateListener(e -> updateSize());
    }

    private void createKeyInputMap() {
        InputMap inputMap = this.frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = this.frame.getRootPane().getActionMap();


        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"),
                "unselect");
        actionMap.put("unselect", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Unselect meeple");
                gameDisplayer.unSelectMeeple();
                frame.repaint();
            }
        });
        System.out.println(Arrays.toString(inputMap.allKeys()));
        System.out.println(Arrays.toString(actionMap.allKeys()));
    }

    private void createMouseListener() {
        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.printf("Mouse Typed: %s\n", e);
                if (getMode() == Mode.EDIT)
                    gameDisplayer.updateWall(e);
                else if (getMode() == Mode.GAME) {
                    gameDisplayer.recordMouseEvent(e);
                }
                frame.repaint();

            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private void updateLog(String log) {
        this.logUpdater.accept(log);
    }

    public enum Mode {
        EDIT(0), GAME(1);

        public final int mode;

        Mode(int mode) {
            this.mode = mode;
        }
    }
}
