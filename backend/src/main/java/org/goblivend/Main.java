package org.goblivend;

import org.goblivend.meeples.displayer.Displayer;
import org.goblivend.meeples.game.Game;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        InputStream inputStream = Main.class
                .getClassLoader()
                .getResourceAsStream("map-game-1.yml");
        Map<String, Object> obj = yaml.load(inputStream);
        Game game = new Game(obj);
        var id1 = game.addPlayer("Player 1");
        var id2 = game.addPlayer("Player 2");
        Displayer displayer1 = new Displayer(game, id1);
        displayer1.create();


        Displayer displayer2 = new Displayer(game, id2);
        displayer2.create();
    }
}
