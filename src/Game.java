import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Game{

    public Player player;
    public JFrame frame = new JFrame();
    public JLayeredPane pane = new JLayeredPane();
    public static Tile.TileGraphics[][] tileGraphics = new Tile.TileGraphics[31][15];
    public GradientNoise.Perlin1D worldHeight = new GradientNoise.Perlin1D(4, -2277269595561219194L, false);

    Timer mainGameLoop;

    public static AudioManager musicManager = new AudioManager();
    public static AudioManager sfxManager = new AudioManager();

    Game() throws UnsupportedAudioFileException, IOException {
        musicManager.playSound("audio/initializer.wav");
        sfxManager.playSound("audio/initializer.wav");
        frame.setTitle("BOX 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(0, 0);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setLayout(null);

        frame.getContentPane().add(pane);
        pane.setLocation(0, 0);
        pane.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        pane.setLayout(null);
        pane.setVisible(true);

        frame.setVisible(true);

        generateTiles();

        player = new Player(frame, pane, 0, 0);
        //frame.addKeyListener(player);

        //MouseScrollInput mouseScrollInput = new MouseScrollInput(frame);

        //Do this so that the JLayeredPanes show up properly, instead of being invisible
        drawTiles();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        drawTiles();
        frame.setExtendedState(JFrame.NORMAL);
        System.out.println(" ");
        System.out.print("Seed: " + worldHeight.seed);
        System.out.print("L");
        System.out.println(" ");

        mainGameLoop = new Timer();
        mainGameLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                /*--------------------------------MAIN GAMEPLAY LOOP----------------------------------*/
                player.calculateNewPosition();
                player.updatePlayerPosition(player.x, player.y);

                drawTiles();
                /*-----------------------------END OF MAIN GAMEPLAY LOOP------------------------------*/
            }
        }, 0, GameInfo.UPDATE_RATE_IN_MILLIS);
    }

    public void generateTiles() {
        for (int i = 0; i != tileGraphics.length; i ++){
            for (int j = 0; j != tileGraphics[i].length; j ++){
                tileGraphics[i][j] = new Tile.TileGraphics(this.pane, i*64, (j)*64, 64, 64, null);
                tileGraphics[i][j].hasCollision = false;
            }
        }
        System.out.println("--- Y VALUES ---");
        for (int i = 0; i < tileGraphics.length; i++) {
            int y = (int) Math.ceil(worldHeight.CosInterpolation((double) i/10) * 8);
            tileGraphics[i][y].hasCollision = true;
            tileGraphics[i][y].regenerateTile(this.pane, i*64, (y)*64, 64, 64, "textures/tiles/dirt.png");
            if(i < 10){
                System.out.print("0");
            }
            System.out.println(i + ": " + y + " -- " + (worldHeight.CosInterpolation((double) i/10) * 8));
        }

        for (int i = 0; i != tileGraphics.length; i ++){
            boolean tileFound = false;
            for (int j = 0; j != tileGraphics[i].length; j ++) {
                if(tileFound){
                    tileGraphics[i][j].hasCollision = true;
                    if(Math.random()>0.9){
                        if(Math.random() >= 0.5){
                            tileGraphics[i][j].regenerateTile(this.pane, i*64, (j)*64, 64, 64, "textures/tiles/coal_ore.png");
                        }else{
                            tileGraphics[i][j].regenerateTile(this.pane, i*64, (j)*64, 64, 64, "textures/tiles/iron_ore.png");
                        }
                    }else{
                        tileGraphics[i][j].regenerateTile(this.pane, i*64, (j)*64, 64, 64, "textures/tiles/stone.png");
                    }
                }else if(tileGraphics[i][j].hasCollision){
                    tileFound = true;
                }
            }
        }
    }

    public void drawTiles(){
        for (Tile.TileGraphics[] tileGraphicsRow : tileGraphics) {
            for (Tile.TileGraphics tileGraphics : tileGraphicsRow) {
                tileGraphics.updateTilePosition(tileGraphics.x, tileGraphics.y);
            }
        }
    }
}