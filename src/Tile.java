import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Tile{
    public static final int TILE_WIDTH = 64;
    public static final int TILE_HEIGHT = 64;
    public static final double BACKGROUND_DARKNESS_MODIFIER = 0.5;

    public static class TileGraphics implements MouseListener {
        int x;
        int y;

        int textureXOffset = 0;
        int textureYOffset = 0;

        int tileWidth;
        int tileHeight;

        Rectangle hitbox;
        public JLabel texture;
        boolean hasCollision;
        TileType tileType;

        Image currentImage;

        public TileGraphics(JLayeredPane pane, TileType tileType){
            this.tileType = tileType;

            this.x = this.tileType.x * TILE_WIDTH;
            this.y = this.tileType.y * TILE_HEIGHT;

            this.tileWidth = TILE_WIDTH;
            this.tileHeight = TILE_HEIGHT;
            this.hasCollision = this.tileType.hasCollision;

            this.hitbox = new Rectangle(this.x, this.y, tileWidth, tileHeight);

            this.texture = new JLabel();
            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
            if(this.tileType.image != null){
                this.texture.setIcon(new ImageIcon(this.tileType.image));
            }
            this.updateTexture();

            this.texture.setOpaque(true);

            pane.add(this.texture);
            pane.setLayer(this.texture, GameInfo.TILE_LAYER);

            assert texture != null;
            texture.addMouseListener(this);
        }

        public TileGraphics(JLayeredPane pane, int x, int y, int tileWidth, int tileHeight, String textureIcon){
            this.tileType = tileType;

            this.x = x;
            this.y = y;

            this.tileWidth = this.tileWidth;
            this.tileHeight = this.tileHeight;
            this.hasCollision = true;

            this.hitbox = new Rectangle(this.x, this.y, tileWidth, tileHeight);

            this.texture = new JLabel();
            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
            if(textureIcon == null){
                this.texture.setIcon(null);
            }else{
                this.texture.setIcon(new ImageIcon(GameInfo.ImageProcessing.getImage(textureIcon, true)));
            }

            this.texture.setBackground(Color.CYAN);

            this.updateTexture();

            this.texture.setOpaque(true);

            pane.add(this.texture);
            pane.setLayer(this.texture, GameInfo.TILE_LAYER);

            assert texture != null;
            texture.addMouseListener(this);
        }

        public void regenerateTile(JLayeredPane pane, int x, int y, int tileWidth, int tileHeight, String textureIcon){
            this.tileType = tileType;

            this.x = x;
            this.y = y;

            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.hasCollision = true;

            this.hitbox.setBounds(this.x, this.y, tileWidth, tileHeight);

            if(textureIcon == null){
                this.texture.setIcon(null);
            }else{
                this.texture.setIcon(new ImageIcon(GameInfo.ImageProcessing.getImage(textureIcon, true)));
            }
            this.updateTexture();

            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
        }

        public void regenerateTile(TileType tileType){
            this.tileType = tileType;

            this.x = tileType.x * TILE_WIDTH;
            this.y = tileType.y * TILE_HEIGHT;

            this.tileWidth = TILE_WIDTH;
            this.tileHeight = TILE_HEIGHT;
            this.hasCollision = tileType.hasCollision;

            this.hitbox.setBounds(this.x, this.y, tileWidth, tileHeight);
            if(this.tileType.image != null){
                this.texture.setIcon(new ImageIcon(this.tileType.image));
            }
            this.updateTexture();

            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);

            this.texture.setBounds(this.x, this.y, TILE_WIDTH, TILE_HEIGHT);
        }

        public void updateTilePosition(int x, int y){
            this.x = x;
            this.y = y;
            this.hitbox.setLocation(x, y);
            this.texture.setLocation(x +this.textureXOffset, y + this.textureYOffset);
        }

        public void updateTexture(Image image){
            this.texture.setIcon(new ImageIcon(image));
            this.texture.revalidate();
            this.texture.repaint();
        }

        public void updateTexture(){
            this.texture.revalidate();
            this.texture.repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(tileType.canBeBroken){
                if(Math.sqrt(Math.pow((tileType.x*TILE_WIDTH)-(Game.player.x + (64 * GameInfo.LevelScrolling.scrollLevelX)), 2)+Math.pow((tileType.y*TILE_HEIGHT)-Game.player.y + ( + (64 * GameInfo.LevelScrolling.scrollLevelY)), 2)) < 200){
                    if(e.getButton() == 1){
                        if(!tileType.isBroken){
                            this.updateTexture(tileType.imageBackground);
                            tileType.isBroken = true;
                            Game.tileTypes[tileType.x][tileType.y].isBroken = true;
                            this.hasCollision = false;
                            Game.tileTypes[tileType.x][tileType.y].hasCollision = false;
                        }
                    }else if(e.getButton() == 3){
                        if(tileType.isBroken){
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
        public static final int TILE_NULL = 0;
        public static final int TILE_GRASS = 1;
        public static final int TILE_DIRT = 2;
        public static final int TILE_STONE = 3;
        public static final int TILE_COAL_ORE = 4;
        public static final int TILE_IRON_ORE = 5;

        public int tileId;

        public Image image;
        public ArrayList<Image> imageRandom = new ArrayList<>();
        public Image[] imageCycle;
        public Image imageBackground;
        public ArrayList<Image> imageRandomBackground = new ArrayList<>();
        public Image[] imageCycleBackground;

        public int cycleDelay;

        public int x;
        public int y;

        public boolean hasCollision;
        public boolean isBroken;
        public boolean canBeBroken;

        public TileType(int tileId, int x, int y){
            this.x = x;
            this.y = y;
            this.tileId = tileId;

            if(tileId == TILE_NULL){
                image = null;
                hasCollision = false;
                canBeBroken = false;
            }else if(tileId == TILE_GRASS){
                image = GameInfo.ImageProcessing.getImage("textures/tiles/grass.png", false);
                imageBackground = GameInfo.ImageProcessing.changeBrightness(image, BACKGROUND_DARKNESS_MODIFIER);
                hasCollision = true;
                canBeBroken = true;
            }else if(tileId == TILE_DIRT){
                image = GameInfo.ImageProcessing.getImage("textures/tiles/dirt.png", true);
                imageBackground = GameInfo.ImageProcessing.changeBrightness(image, BACKGROUND_DARKNESS_MODIFIER);
                hasCollision = true;
                canBeBroken = true;
            }else if(tileId == TILE_STONE){
                image = GameInfo.ImageProcessing.getImage("textures/tiles/stone.png", true);
                imageBackground = GameInfo.ImageProcessing.changeBrightness(image, BACKGROUND_DARKNESS_MODIFIER);
                hasCollision = true;
                canBeBroken = true;
            }else if(tileId == TILE_COAL_ORE){
                image = GameInfo.ImageProcessing.getImage("textures/tiles/coal_ore.png", true);
                imageBackground = GameInfo.ImageProcessing.changeBrightness(image, BACKGROUND_DARKNESS_MODIFIER);
                hasCollision = true;
                canBeBroken = true;
            }else if(tileId == TILE_IRON_ORE){
                image = GameInfo.ImageProcessing.getImage("textures/tiles/iron_ore.png", true);
                imageBackground = GameInfo.ImageProcessing.changeBrightness(image, BACKGROUND_DARKNESS_MODIFIER);
                hasCollision = true;
                canBeBroken = true;
            }
        }
    }
}

