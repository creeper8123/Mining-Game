import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Game{

    public static Player player;
    public static JFrame frame = new JFrame();
    public static JLayeredPane pane = new JLayeredPane();
    public static Tile.TileGraphics[][] tileGraphics = new Tile.TileGraphics[(Toolkit.getDefaultToolkit().getScreenSize().width / Tile.TILE_WIDTH) + 5][(Toolkit.getDefaultToolkit().getScreenSize().height / Tile.TILE_HEIGHT) + 5];
    public static Tile.TileType[][] tileTypes = new Tile.TileType[512][20];
    public GradientNoise.Perlin1D worldHeight = new GradientNoise.Perlin1D((int) Math.ceil(2 + (tileTypes.length/10)), 1L, false);

    Timer mainGameLoop;

    public static AudioManager musicManager = new AudioManager();
    public static AudioManager sfxManager = new AudioManager();

    Game() throws UnsupportedAudioFileException, IOException {
        musicManager.playSound("audio/initializer.wav");
        sfxManager.playSound("audio/initializer.wav");
        frame.setTitle("Voxel Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(0, 0);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setLayout(null);

        frame.getContentPane().add(pane);
        pane.setLocation(0, 0);
        pane.setSize(tileGraphics.length * Tile.TILE_WIDTH, tileGraphics[0].length * Tile.TILE_HEIGHT);
        pane.setLayout(null);
        pane.setVisible(true);

        frame.setVisible(true);

        generateTiles();

        player = new Player(frame, pane, (tileGraphics.length/2) * 64, 0);
        frame.setExtendedState(JFrame.NORMAL);
        System.out.println(" ");
        System.out.print("Seed: " + worldHeight.seed);
        System.out.println("L");

        musicManager.playSound("audio/music/overworld.wav", -1);

        mainGameLoop = new Timer();
        mainGameLoop.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                /*--------------------------------MAIN GAMEPLAY LOOP----------------------------------*/
                player.calculateNewPosition();

                if(GameInfo.LevelScrolling.scrollLevelX < (tileTypes.length - tileGraphics.length)-1){
                    if(player.x > (tileGraphics.length/2+GameInfo.LevelScrolling.SCROLL_AMOUNT) * 64){
                        player.x -= (64 * GameInfo.LevelScrolling.SCROLL_AMOUNT);
                        GameInfo.LevelScrolling.ScrollLevelRight(tileTypes, tileGraphics);
                    }
                }

                if(GameInfo.LevelScrolling.scrollLevelX > 0){
                    if(player.x < (tileGraphics.length/2-GameInfo.LevelScrolling.SCROLL_AMOUNT) * 64){
                        player.x += (64 * GameInfo.LevelScrolling.SCROLL_AMOUNT);
                        GameInfo.LevelScrolling.ScrollLevelLeft(tileTypes, tileGraphics);
                    }
                }

                if(GameInfo.LevelScrolling.scrollLevelY < (tileTypes[0].length - tileGraphics[0].length)-1 && false){
                    if(player.y < 0){//(tileGraphics[0].length/2-GameInfo.LevelScrolling.SCROLL_AMOUNT) * 64){
                        player.y += (64 * GameInfo.LevelScrolling.SCROLL_AMOUNT);
                        GameInfo.LevelScrolling.ScrollLevelUp(tileTypes, tileGraphics);
                    }
                }

                player.updatePlayerPosition(player.x, player.y);
                /*-----------------------------END OF MAIN GAMEPLAY LOOP------------------------------*/
            }
        }, 0, GameInfo.UPDATE_RATE_IN_MILLIS);
    }

    private void generateTiles() {

        for (int i = 0; i != tileGraphics.length; i ++){
            for (int j = 0; j != tileGraphics[i].length; j ++){
                tileGraphics[i][j] = new Tile.TileGraphics(pane, i*Tile.TILE_WIDTH, (j)*Tile.TILE_HEIGHT, Tile.TILE_WIDTH, Tile.TILE_HEIGHT, null);
            }
        }

        for (int i = 0; i != tileTypes.length; i ++) {
            for (int j = 0; j != tileTypes[i].length; j++) {
                tileTypes[i][j] = new Tile.TileType(Tile.TileType.TILE_NULL, i, j);
            }
        }

        for (int i = 0; i < tileTypes.length; i++) {
            int y = (int) Math.ceil(worldHeight.cosInterpolation((double) i/10) * 8);
            tileTypes[i][y] = new Tile.TileType(Tile.TileType.TILE_GRASS, i, y);
        }

        for (int i = 0; i != tileTypes.length; i ++){
            boolean tileFound = false;
            for (int j = 0; j != tileTypes[i].length; j ++) {
                if(tileFound){
                    if(Math.random()>0.9){
                        if(Math.random() >= 0.5){
                            tileTypes[i][j] = new Tile.TileType(Tile.TileType.TILE_COAL_ORE, i, j);
                        }else{
                            tileTypes[i][j] = new Tile.TileType(Tile.TileType.TILE_IRON_ORE, i, j);
                        }
                    }else{
                        tileTypes[i][j] = new Tile.TileType(Tile.TileType.TILE_STONE, i, j);
                    }
                }else if(tileTypes[i][j].hasCollision){
                    tileFound = true;
                }
            }
        }

        for (int i = 0; i != tileGraphics.length; i ++){
            for (int j = 0; j != tileGraphics[i].length; j ++){
                tileGraphics[i][j].regenerateTile(tileTypes[i][j]);
            }
        }
    }
}