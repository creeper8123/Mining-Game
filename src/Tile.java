import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
    int x;
    int y;

    int textureXOffset = 0;
    int textureYOffset = 0;

    int tileWidth;
    int tileHeight;

    Rectangle hitbox;

    public JLabel texture;

    boolean hasCollision;


    public Tile(JFrame frame, int x, int y, int tileWidth, int tileHeight, String textureIcon){
        this.x = x;
        this.y = y;

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.hasCollision = true;

        this.hitbox = new Rectangle(this.x, this.y, tileWidth, tileHeight);

        this.texture = new JLabel();
        this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
        this.texture.setIcon(GameInfo.getImage(textureIcon));
        this.texture.setOpaque(true);

        frame.getContentPane().add(this.texture);
    }

    public void regenerateTile(JFrame frame, int x, int y, int tileWidth, int tileHeight){
        this.x = x;
        this.y = y;

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.hasCollision = true;

        this.hitbox = new Rectangle(this.x, this.y, tileWidth, tileHeight);

        this.texture = new JLabel();
        this.texture.setBounds(this.x, this.y, this.tileWidth, this.tileHeight);
        this.texture.setBackground(Color.GREEN);
        this.texture.setOpaque(true);
    }

    public void updateTilePosition(int x, int y){
        this.x = x;
        this.y = y;
        this.hitbox.setLocation(this.x, this.y);
        this.texture.setLocation(this.x +this.textureXOffset, this.y +this.textureYOffset);
    }
}
