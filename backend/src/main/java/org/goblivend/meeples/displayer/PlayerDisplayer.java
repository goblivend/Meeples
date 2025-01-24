package org.goblivend.meeples.displayer;

import org.goblivend.meeples.game.Game;
import org.goblivend.meeples.game.MeepleType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

class PlayerDisplayer extends JPanel {
    private Game game;
    private final Integer playerId;
    //    private final JLabel playerLabel;
    private final PlayerTeamDisplayer playerTeamDisplayer;

    public PlayerDisplayer(Game game, Integer playerId) {
        this.game = game;
        this.playerId = playerId;
//        this.playerLabel = new JLabel("Am A player");
        this.playerTeamDisplayer = new PlayerTeamDisplayer();

        this.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.setLayout(new BorderLayout());

//        this.add(this.playerLabel);
        this.add(this.playerTeamDisplayer);


        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        createSizeUpdateListener();
//        this.playerLabel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(200, 200);
    }

    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    private void createSizeUpdateListener() {
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();
            }
        });
    }

    public void updateSize() {
        this.playerTeamDisplayer.setSize(this.getSize());
    }

    class PlayerTeamDisplayer extends JPanel {
        private static final Integer DEFAULT_TEAM_ICON_SIZE = 64;
        private static final Integer DEFAULT_TEAM_ICON_DISTANCE = 5;

        private Integer teamIconSize = DEFAULT_TEAM_ICON_SIZE;
        private Integer teamIconDistance = DEFAULT_TEAM_ICON_DISTANCE;

        public PlayerTeamDisplayer() {
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintCompleteTeam(g);
            paintUsedMemberCheckMark(g);
            paintPlayingMembers(g);
        }

        private void paintCompleteTeam(Graphics g) {
            var members = MeepleType.values();
            for (int i = 0; i < members.length; i++) {
                var x = i * (teamIconSize + teamIconDistance);
                var y = 0;
                g.setColor(members[i].color);
                g.fillOval(x, y, teamIconSize, teamIconSize);
            }
        }

        private void paintUsedMemberCheckMark(Graphics g) {
            var members = MeepleType.values();
            var player = game.getPlayer(playerId);

            for (int i = 0; i < members.length; i++) {
                if (!player.isInSpecialTeam(members[i]))
                    continue;

                var x = i * (teamIconSize + teamIconDistance);
                var y = 0;

                g.setColor(new Color(0, 171, 14));

                Polygon checkMark = getCheckMark(x, y);

                g.fillPolygon(checkMark);
            }
        }

        private void paintPlayingMembers(Graphics g) {
            var members = MeepleType.values();
            var player = game.getPlayer(playerId);

            for (int i = 0; i < members.length; i++) {
                if (!player.isPlaying(members[i]))
                    continue;

                var x = i * (teamIconSize + teamIconDistance);
                var y = teamIconSize;

                g.setColor(new Color(0, 0, 0));
                g.fillRect(x, y, teamIconSize, teamIconDistance);
            }
        }

        // TODO: Display on a three-line grid the meeples played and the number of moves
        // Also display whether the special move was used (overline white ?)

        // TODO: Move this to a utility class
        private Polygon getCheckMark(int x, int y) {
            Polygon checkMark = new Polygon();
            checkMark.addPoint(teamIconSize * 1 / 8, teamIconSize * 4 / 8);
            checkMark.addPoint(teamIconSize * 2 / 8, teamIconSize * 3 / 8);
            checkMark.addPoint(teamIconSize * 3 / 8, teamIconSize * 4 / 8);
            checkMark.addPoint(teamIconSize * 6 / 8, teamIconSize * 1 / 8);
            checkMark.addPoint(teamIconSize * 7 / 8, teamIconSize * 2 / 8);
            checkMark.addPoint(teamIconSize * 3 / 8, teamIconSize * 7 / 8);

            checkMark.translate(x, y);
            return checkMark;
        }
    }
}
