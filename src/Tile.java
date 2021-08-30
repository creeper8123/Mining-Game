import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Tile{

    //finals about tile dimensions
    public static final int TILE_WIDTH = 64;
    public static final int TILE_HEIGHT = 64;

    public static class TileGraphics implements MouseListener {
        public static final double BACKGROUND_DARKNESS_MODIFIER = 0.5; //How much darker the tile will be if it's a background tile

        //Tile position
        int x;
        int y;

        //Separation from the tile texture and the hitbox
        int textureXOffset = 0;
        int textureYOffset = 0;

        //The tile dimensions
        int tileWidth;
        int tileHeight;

        //Create the main assets of the tile graphic
        Rectangle hitbox;
        public JLabel texture;
        boolean hasCollision;
        TileType tileType;

        //Define a new tile with a known tileType
        public TileGraphics(JLayeredPane pane, TileType tileType){
            this.tileType = tileType;

            //Sets the graphic's location to the location defined in the tile
            this.x = this.tileType.x * TILE_WIDTH;
            this.y = this.tileType.y * TILE_HEIGHT;

            //Setting the tile properties to the proper dimensions
            this.tileWidth = TILE_WIDTH;
            this.tileHeight = TILE_HEIGHT;

            this.hasCollision = this.tileType.hasCollision; //Should this tile be considered for collisions?

            this.hitbox = new Rectangle(this.x, this.y, tileWidth, tileHeight); //Define the hotbox's bounds

            //Defines the tile's texture
            this.texture = new JLabel();
            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
            if(this.tileType.image != null){
                this.texture.setIcon(new ImageIcon(this.tileType.image));
            }
            this.texture.setBackground(Color.CYAN);
            this.updateTexture();
            this.texture.setOpaque(true);

            //Adds the texture to the pane, and adds a MouseListener to the object
            pane.add(this.texture);
            pane.setLayer(this.texture, GameInfo.TILE_LAYER);
            assert texture != null;
            texture.addMouseListener(this);
        }

        public TileGraphics(JLayeredPane pane, int x, int y, int tileWidth, int tileHeight, String textureIcon){

            //Sets the graphic's location
            this.x = x;
            this.y = y;

            //Setting the tile properties to the proper dimensions
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;

            this.hasCollision = true; //Should this tile be considered for collisions?

            this.hitbox = new Rectangle(this.x, this.y, tileWidth, tileHeight); //Define the hotbox's bounds

            //Defines the tile's texture
            this.texture = new JLabel();
            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
            if(textureIcon == null){
                this.texture.setIcon(null);
            }else{
                this.texture.setIcon(new ImageIcon(GameInfo.ImageProcessing.getImage(textureIcon, true, false)));
            }
            this.texture.setBackground(Color.CYAN);
            this.updateTexture();
            this.texture.setOpaque(true);

            //Adds the texture to the pane, and adds a MouseListener to the object
            pane.add(this.texture);
            pane.setLayer(this.texture, GameInfo.TILE_LAYER);
            assert texture != null;
            texture.addMouseListener(this);
        }

        //Replaces the current tile info without creating a new TileGraphics
        public void regenerateTile(JLayeredPane pane, int x, int y, int tileWidth, int tileHeight, String textureIcon){

            //Sets the graphic's location
            this.x = x;
            this.y = y;

            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;

            this.hasCollision = true; //Should this tile be considered for collisions?

            this.hitbox.setBounds(this.x, this.y, tileWidth, tileHeight);

            //Defines the tile's texture
            if(textureIcon == null){
                this.texture.setIcon(null);
            }else{
                this.texture.setIcon(new ImageIcon(GameInfo.ImageProcessing.getImage(textureIcon, true, false)));
            }
            this.updateTexture();

            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
        }

        //Replaces the current tileType without creating a new TileGraphics
        public void regenerateTile(TileType tileType){
            this.tileType = tileType;

            //Sets the graphic's location to the location defined in the tile
            this.x = tileType.x * TILE_WIDTH;
            this.y = tileType.y * TILE_HEIGHT;

            this.tileWidth = TILE_WIDTH;
            this.tileHeight = TILE_HEIGHT;

            this.hasCollision = tileType.hasCollision; //Should this tile be considered for collisions?

            //Defines the tile's texture
            this.hitbox.setBounds(this.x, this.y, tileWidth, tileHeight);
            if(this.tileType.image != null){
                this.texture.setIcon(new ImageIcon(this.tileType.image));
            }
            this.updateTexture();

            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
            this.texture.setBounds(this.x, this.y, TILE_WIDTH, TILE_HEIGHT);
        }

        //Change the location of the texture and the hitbox
        public void updateTilePosition(int x, int y){
            this.x = x;
            this.y = y;
            this.hitbox.setLocation(x, y);
            this.texture.setLocation(x +this.textureXOffset, y + this.textureYOffset);
        }

        //Changes the tile's texture, then refreshes the texture of the tile
        public void updateTexture(Image image){
            this.texture.setIcon(new ImageIcon(image));
            this.texture.revalidate();
            this.texture.repaint();
        }

        //Refreshes the texture of the tile
        public void updateTexture(){
            this.texture.revalidate();
            this.texture.repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println(tileType.tileId);
            if(tileType.canBeBroken){
                if(Math.sqrt(Math.pow((tileType.x*TILE_WIDTH)-(Game.player.x + (64 * GameInfo.LevelScrolling.scrollLevelX)), 2)+Math.pow((tileType.y*TILE_HEIGHT)-Game.player.y + ( + (64 * GameInfo.LevelScrolling.scrollLevelY)), 2)) < 200){ //If the player is within 200 pixels
                    if(e.getButton() == 1){ //Left click
                        if(!tileType.isBroken){
                            //Replace the tile
                            this.updateTexture(tileType.imageBackground);
                            tileType.isBroken = true;
                            Game.tileTypes[tileType.x][tileType.y].isBroken = true;
                            this.hasCollision = false;
                            Game.tileTypes[tileType.x][tileType.y].hasCollision = false;
                        }
                    }else if(e.getButton() == 3){ //Right click
                        if(tileType.isBroken){
                            if(!this.hitbox.intersects(Game.player.hitbox)){
                                //Break the tile
                                this.updateTexture(tileType.image);
                                tileType.isBroken = false;
                                Game.tileTypes[tileType.x][tileType.y].isBroken = false;
                                this.hasCollision = true;
                                Game.tileTypes[tileType.x][tileType.y].hasCollision = true;
                            }
                        }
                    }
                }
            }
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
    }

    public static class TileType{
        //List of Tile IDs
        public static final int TILE_NULL = 0;
        public static final int TILE_GRASS = 1;
        public static final int TILE_DIRT = 2;
        public static final int TILE_STONE = 3;
        public static final int TILE_COAL_ORE = 4;
        public static final int TILE_IRON_ORE = 5;

        public int tileId;

        //List of texture options
        public Image image; //The image texture
        public ArrayList<String> imageRandom = new ArrayList<>(); //Picks a random image upon being called, add string addresses
        public Image[] imageCycle; //List of images to cycle between

        //Darkened versions of the above textures
        public Image imageBackground;
        public ArrayList<Image> imageRandomBackground = new ArrayList<>();
        public Image[] imageCycleBackground;

        public int cycleDelay; //Delay (in milliseconds) between images in imageCycle

        //Tile coordinates (used in 1st TileGraphics definition)
        public int x;
        public int y;

        //Properties defining the tile
        public boolean hasCollision;
        public boolean isBroken;
        public boolean canBeBroken;

        public TileType(int tileId, int x, int y){
            this.x = x;
            this.y = y;
            this.tileId = tileId;

            //Defining the tile attributes based on the ID number
            switch (tileId){
                case TILE_NULL -> {
                    image = null;
                    imageBackground = null;
                    hasCollision = false;
                    canBeBroken = false;
                }
                case TILE_GRASS -> {
                    image = GameInfo.ImageProcessing.getImage("textures/tiles/fullTiles/grass.png", false, false);
                    imageBackground = GameInfo.ImageProcessing.changeBrightness(image, TileGraphics.BACKGROUND_DARKNESS_MODIFIER);
                    hasCollision = true;
                    canBeBroken = true;
                }
                case TILE_DIRT -> {
                    for (int i = 0; i < /*Objects.requireNonNull(new File("resources/" + "textures/tiles/dirt").listFiles()).length*/ 5; i++) {
                        imageRandom.add("textures/tiles/fullTiles/dirt/dirt" + i + ".png");
                    }
                    image = GameInfo.ImageProcessing.getImage(imageRandom.get((int) Math.floor(Game.WORLD_RANDOM.nextDouble() * imageRandom.size())), true, false);
                    imageBackground = GameInfo.ImageProcessing.changeBrightness(image, TileGraphics.BACKGROUND_DARKNESS_MODIFIER);
                    hasCollision = true;
                    canBeBroken = true;
                }
                case TILE_STONE -> {
                    for (int i = 0; i < /*Objects.requireNonNull(new File("resources/" + "textures/tiles/stone").listFiles()).length*/ 5; i++) {
                        imageRandom.add("textures/tiles/fullTiles/stone/stone" + i + ".png");
                    }
                    image = GameInfo.ImageProcessing.getImage(imageRandom.get((int) Math.floor(Game.WORLD_RANDOM.nextDouble() * imageRandom.size())), true, false);
                    imageBackground = GameInfo.ImageProcessing.changeBrightness(image, TileGraphics.BACKGROUND_DARKNESS_MODIFIER);
                    hasCollision = true;
                    canBeBroken = true;
                }
                case TILE_COAL_ORE -> {
                    for (int i = 0; i < 5; i++) {
                        imageRandom.add("textures/tiles/fullTiles/stone/stone" + i + ".png");
                    }
                    String baseImage = imageRandom.get((int) Math.floor(Game.WORLD_RANDOM.nextDouble() * imageRandom.size()));
                    imageRandom.clear();

                    for (int i = 0; i < 5; i++) {
                        imageRandom.add("textures/tiles/tileOverlays/coalOre/coalOre" + i + ".png");
                    }
                    String topImage = imageRandom.get((int) Math.floor(Game.WORLD_RANDOM.nextDouble() * imageRandom.size()));
                    image = GameInfo.ImageProcessing.resizeImage(4, GameInfo.ImageProcessing.overlayImage(GameInfo.ImageProcessing.getImage(baseImage, true, true), GameInfo.ImageProcessing.getImage(topImage, true, true))); //GameInfo.ImageProcessing.getImage(baseImage, true, false);
                    imageBackground = GameInfo.ImageProcessing.changeBrightness(image, TileGraphics.BACKGROUND_DARKNESS_MODIFIER);
                    hasCollision = true;
                    canBeBroken = true;
                }
                case TILE_IRON_ORE -> {
                    image = GameInfo.ImageProcessing.getImage("textures/tiles/tileOverlays/iron_ore.png", true, false);
                    imageBackground = GameInfo.ImageProcessing.changeBrightness(image, TileGraphics.BACKGROUND_DARKNESS_MODIFIER);
                    hasCollision = true;
                    canBeBroken = true;
                }
                default -> {
                    image = GameInfo.ImageProcessing.getImage("textures/missingTexture.png", false, false);
                    imageBackground = GameInfo.ImageProcessing.getImage("textures/missingTexture.png", false, false);
                    hasCollision = false;
                    canBeBroken = false;
                }
            }
        }
    }
}

