import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Tile{
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


        /*TODO: add mouse click event to every tileGraphics
        Remove tileGraphics when left-clicked
        place tileGraphics when right-clicked
        */


        public TileGraphics(JLayeredPane pane, int x, int y, int tileWidth, int tileHeight, String textureIcon){
            this.x = x;
            this.y = y;

            pane.addMouseListener(this);

            //TODO: Find out why mouse listener is activating on every tileGraphics at once

            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.hasCollision = true;

            this.hitbox = new Rectangle(this.x, this.y, tileWidth, tileHeight);

            this.texture = new JLabel();
            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
            if(textureIcon == null){
                this.texture.setIcon(null);
            }else{
                this.texture.setIcon(new ImageIcon(GameInfo.ImageProcessing.getImage(textureIcon, true)));
            }
            this.texture.setOpaque(true);

            pane.add(this.texture);
            pane.setLayer(this.texture, GameInfo.TILE_LAYER);
        }

        public void regenerateTile(JLayeredPane pane, int x, int y, int tileWidth, int tileHeight, String textureIcon){
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

            this.texture = new JLabel();
            this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
            this.texture.setBackground(Color.GREEN);
            this.texture.setOpaque(true);
        }

        public void updateTilePosition(int x, int y){
            this.x = x;
            this.y = y;
            this.hitbox.setLocation(this.x, this.y);
            this.texture.setLocation(this.x +this.textureXOffset, this.y + this.textureYOffset);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            //this.hasCollision = false;
            //this.texture.setIcon(null);
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
}

