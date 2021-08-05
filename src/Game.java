import UserInputs.MouseInputs.MouseButtonInput;
import UserInputs.MouseInputs.MouseScrollInput;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Game{

    public Player player;
    JFrame frame = new JFrame();
    public static Tile[][] tiles = new Tile[10][5];

    Timer mainGameLoop;

    public static AudioManager musicManager = new AudioManager();
    public static AudioManager sfxManager = new AudioManager();

    Game() throws UnsupportedAudioFileException, IOException {
        musicManager.playSound("audio/initializer.wav");
        sfxManager.playSound("audio/initializer.wav");
        frame.setTitle("B O X   2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(0, 0);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setLayout(null);
        frame.setVisible(true);

        generateTiles();

        player = new Player(frame, 0, 0);

        UserInputs.KeyInput keyInput = new UserInputs.KeyInput(frame);
        MouseButtonInput mouseButtonInput = new MouseButtonInput(frame);
        //MouseScrollInput mouseScrollInput = new MouseScrollInput(frame);

        //Do this so that the JLayeredPanes show up properly, instead of being invisible
        drawTiles();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        drawTiles();
        frame.setExtendedState(JFrame.NORMAL);

        mainGameLoop = new Timer();
        mainGameLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                /*--------------------------------MAIN GAMEPLAY LOOP----------------------------------*/
                player.calculateNewPosition(keyInput.upPressed, keyInput.leftPressed, keyInput.rightPressed, keyInput.downPressed);
                player.updatePlayerPosition(player.x, player.y);

                drawTiles();

                if(mouseButtonInput.mouseHeld){
                    player.x = 0.0;
                    player.xSpeed = 0.0;
                    player.y = 0.0;
                    player.ySpeed = 0.0;
                }
                /*-----------------------------END OF MAIN GAMEPLAY LOOP------------------------------*/
            }
        }, 0, 4);
    }

    public void generateTiles() {
        for (int i = 0; i != tiles.length; i ++){
            for (int j = 0;j != tiles[i].length; j ++){
                tiles[i][j] = new Tile(this.frame, i*64, (j+6)*64, 64, 64, "textures/missingTexture.png");
            }
        }
    }

    public void drawTiles(){
        for (Tile[] tileRow :tiles) {
            for (Tile tile:tileRow) {
                tile.updateTilePosition(tile.x, tile.y);
            }
        }
    }
}