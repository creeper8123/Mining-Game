import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class Player implements KeyListener{

    public JLabel texture; //The player's texture
    public Rectangle hitbox; //The player's hitbox

    public boolean jump = false; //Is the "Up" key pressed?
    public boolean moveLeft = false; //Is the "Left" key pressed?
    public boolean crouch = false; //Is the "Down" key pressed?
    public boolean moveRight = false; //Is the "Right" key pressed?

    public final int playerWidth = 32; //How wide the player is
    public final int playerHeight = 48; //How tall the player is

    public final int textureXOffset = 0; //X offset on the texture based on the hitbox's position
    public final int textureYOffset = 0; //Y offset on the texture based on the hitbox's position

    public double x; //Current X position
    public double y; //Current Y position

    public double xSpeed = 0.0; //Current speed on the X-axis
    public double ySpeed = 0.0; //Current speed on the Y-axis

    public double xSpeedTarget = 0.0; //The speed the player is trying to reach
    public final double xSpeedMax = 2.0; //The target speed moving left or right under normal conditions
    public final double xSpeedDeadZone = 0.1; //How close the current speed and the target speed need to be before the current speed is overwritten to the target speed

    public final double xSpeedAcceleration = 0.02; //How much the player's speed changes in every update

    public final double airborneManeuverabilityMultiplier = 0.25; //How much acceleration is changed while not on the ground

    public final double gravity = 0.05; //How much the player's downward speed is changes when not on the ground
    public final double terminalVelocity = 10; //Maximum downward speed under normal conditions
    public double jumpForce = 5.0; //Upward speed added to the player when the player is on the ground

    public boolean onGround = false; //Is the player currently touching the ground

    //TODO: Make all sprite values controllable at same rate at different framerates as defined in GameInfo.UPDATE_RATE_IN_MILLIS

    //Defined all the player variables and adds them to the main frame
    Player(JFrame frame, JLayeredPane pane, double initialX, double initialY){
        //Adds a keyboard input listener to the main frame, and calls functions here accordingly
        frame.addKeyListener(this);

        //Sets the current position as defined in the arguments
        this.x = initialX;
        this.y = initialY;

        //Creates the texture object, and sets the texture to an image
        this.texture = new JLabel();
        this.texture.setBounds(0, 0,this.playerWidth, this.playerHeight);
        this.texture.setIcon(new ImageIcon("resources/" + "textures/missingTexture.png"));
        this.texture.setOpaque(true);

        //Creates the hitbox object
        this.hitbox = new Rectangle();
        this.hitbox.setBounds((int) this.x, (int) this.y, this.playerWidth, this.playerHeight);

        //Adds the texture to the pane, and sets the layer to the sprite layer
        pane.add(this.texture);
        pane.setLayer(this.texture, GameInfo.SPRITE_LAYER);
    }

    //Takes the inputs and gets the player's position on the next frame
    public void calculateNewPosition(){

        if(moveLeft ^ moveRight){ //If only left or right but not both
            if(moveLeft){
                this.xSpeedTarget = -this.xSpeedMax; //If left, set target speed to -max
            }else{
                this.xSpeedTarget = this.xSpeedMax; //Else, set target speed to max
            }
        }else{ //If neither key is pressed
            if(this.onGround){
                this.xSpeedTarget = 0; //If on ground, kill all speed
            }else{
                this.xSpeedTarget = this.xSpeed; //If airborne, keep the same speed.
            }
        }

        if((Math.abs(this.xSpeed - this.xSpeedTarget) < this.xSpeedDeadZone)){
            this.xSpeed = this.xSpeedTarget; //If the player is very close to their target speed, snap them to said speed
        }else if(this.xSpeed > this.xSpeedTarget){
            if(this.onGround){
                this.xSpeed -= this.xSpeedAcceleration; //If their on the ground and their target speed is lower, accelerate in the neg. direction
            }else{
                this.xSpeed -= (this.xSpeedAcceleration * this.airborneManeuverabilityMultiplier); //If their not on the ground and their target speed is lower, accelerate in the neg. direction very slowly
            }
        }else if(this.xSpeed < this.xSpeedTarget){
            if(this.onGround){
                this.xSpeed += this.xSpeedAcceleration; //If their on the ground and their target speed is lower, accelerate in the pos. direction
            }else{
                this.xSpeed += (this.xSpeedAcceleration * this.airborneManeuverabilityMultiplier); //If their not on the ground and their target speed is lower, accelerate in the pos. direction very slowly
            }
        }

        //Adds gravity to the player
        if(!onGround){
            if(ySpeed < terminalVelocity){
                ySpeed += gravity; //If not on ground and slower than terminal velocity, accelerate downwards
            }else if(ySpeed > terminalVelocity){
                ySpeed = terminalVelocity;//If not on ground and faster than terminal velocity, set downward speed to terminal velocity
            }//If not on ground and moving at terminal velocity, do nothing
        }


        //Checks if the player is about to run into an object on the X-axis
        if(xSpeed != 0){
            hitbox.x += xSpeed; //Adds the xSpeed to the current location
            for (Tile.TileGraphics[] tileGraphicsRow :Game.tileGraphics) {
                for (Tile.TileGraphics tileGraphics : tileGraphicsRow) {
                    if(this.hitbox.intersects(tileGraphics.hitbox) && tileGraphics.hasCollision) { //Checks if the hitbox collides with any other active hitboxes
                        //If a collision is detected...
                        hitbox.x -= xSpeed; //Resets the X pos of the hitbox
                        while (!tileGraphics.hitbox.intersects(this.hitbox)) {
                            this.hitbox.x += Math.signum(this.xSpeed); //Increments the hitbox toward the object it collided with
                        }
                        this.hitbox.x -= Math.signum(this.xSpeed); //Moves the player 1 pixel away from a collision
                        this.xSpeed = 0; //Cancels out all X speed
                        this.x = hitbox.x;
                    }
                }
            }
        }

        //Checks if the player is about to run into an object on the Y-axis
        if(ySpeed != 0){
            hitbox.y += ySpeed;//Adds the ySpeed to the current location
            for (Tile.TileGraphics[] tileGraphicsRow :Game.tileGraphics) {
                for (Tile.TileGraphics tileGraphics : tileGraphicsRow) {
                    if(this.hitbox.intersects(tileGraphics.hitbox) && tileGraphics.hasCollision) {//Checks if the hitbox collides with any other active hitboxes
                        //If a collision is detected...
                        hitbox.y -= ySpeed; //Resets the Y pos of the hitbox
                        while (!tileGraphics.hitbox.intersects(this.hitbox)) {
                            this.hitbox.y += Math.signum(this.ySpeed); //Increments the hitbox toward the object it collided with
                        }
                        this.hitbox.y -= Math.signum(this.ySpeed); //Moves the player 1 pixel away from a collision
                        if(Math.abs(ySpeed) < terminalVelocity * 0.75 && Math.abs(ySpeed) > 0){ //If the player falls less than 9 tiles
                            try { //Play the appropriate landing sound
                                Game.sfxManager.playSound("audio/sfx/land.wav");
                            } catch (IOException | UnsupportedAudioFileException e) {
                                e.printStackTrace();
                            }
                        }else if(Math.abs(ySpeed) > terminalVelocity * 0.75 && Math.abs(ySpeed) > 0){ //If the player falls more than 9 tiles
                            try {
                                Game.sfxManager.playSound("audio/sfx/land_hard.wav");
                            } catch (IOException | UnsupportedAudioFileException e) {
                                e.printStackTrace();
                            }
                        }
                        this.ySpeed = 0; //Cancels out all Y speed
                        this.y = hitbox.y;
                    }
                }
            }
        }

        //Checks if the player is on the ground
        hitbox.y++; //Increment the hitbox 1 pixel down
        for (Tile.TileGraphics[] tileGraphicsRow :Game.tileGraphics) {
            boolean broken = false;
            for (Tile.TileGraphics tileGraphics : tileGraphicsRow) {
                if (tileGraphics.hitbox.intersects(this.hitbox) && tileGraphics.hasCollision) { //Check all tiles until a collision is found
                    onGround = true;
                    broken = true;
                    break;
                } else {
                    onGround = false;
                }
            }
            if(broken){
                break; //Cancel the loop
            }
        }
        hitbox.y--; //Increment the hitbox 1 pixel up

        //Makes the player jump
        if(jump && onGround){
            ySpeed = -jumpForce; //Jump if the player is on the ground and the jump key is pressed.
            try {
                Game.sfxManager.playSound("audio/sfx/jump.wav");
            } catch (IOException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }

        //Add the newly calculated speeds to the current position
        this.y += this.ySpeed;
        this.x += this.xSpeed;
    }

    public void calculateNewPositionFly(){

        if((!moveLeft && moveRight) || (moveLeft && !moveRight)){
            if(moveLeft){
                this.xSpeed = -this.xSpeedMax;
            }else{
                this.xSpeed = this.xSpeedMax;
            }
        }else{
            this.xSpeed = 0;
        }

        if((!jump && crouch) || (jump && !crouch)){
            if(jump){
                this.ySpeed = -this.xSpeedMax;
            }else{
                this.ySpeed = this.xSpeedMax;
            }
        }else{
            this.ySpeed = 0;
        }


        hitbox.x += xSpeed;
        for (Tile.TileGraphics[] tileGraphicsRow :Game.tileGraphics) {
            for (Tile.TileGraphics tileGraphics : tileGraphicsRow) {
                if(this.hitbox.intersects(tileGraphics.hitbox) && tileGraphics.hasCollision) {
                    hitbox.x -= xSpeed;
                    while (!tileGraphics.hitbox.intersects(this.hitbox)) {
                        this.hitbox.x += Math.signum(this.xSpeed);
                    }
                    this.hitbox.x -= Math.signum(this.xSpeed);
                    this.xSpeed = 0;
                    this.x = hitbox.x;
                }
            }
        }

        hitbox.y += ySpeed;
        for (Tile.TileGraphics[] tileGraphicsRow :Game.tileGraphics) {
            for (Tile.TileGraphics tileGraphics : tileGraphicsRow) {
                if (this.hitbox.intersects(tileGraphics.hitbox) && tileGraphics.hasCollision) {
                    hitbox.y -= ySpeed;
                    while (!tileGraphics.hitbox.intersects(this.hitbox)) {
                        this.hitbox.y += Math.signum(this.ySpeed);
                    }
                    this.hitbox.y -= Math.signum(this.ySpeed);
                    this.ySpeed = 0;
                    this.y = hitbox.y;
                }
            }
        }

        //Adds the speeds to the current positions
        this.y += this.ySpeed;
        this.x += this.xSpeed;
    }

    public void updatePlayerPosition(double x, double y){
        this.x = x;
        this.y = y;

        this.hitbox.setLocation((int) x, (int) y);
        this.texture.setLocation(((int) x)+this.textureXOffset, ((int) y)+this.textureYOffset);
        Game.pane.setLocation(- (int) (x) + ((Game.frame.getWidth() - this.playerWidth)/2) - this.playerWidth/2, - (int) (y) + ((Game.frame.getHeight() - this.playerHeight)/2) - this.playerHeight/2);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w', ' ' -> jump = true;
            case 'a' -> moveLeft = true;
            case 's' -> crouch = true;
            case 'd' -> moveRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w', ' ' -> jump = false;
            case 'a' -> moveLeft = false;
            case 's' -> crouch = false;
            case 'd' -> moveRight = false;
        }
    }
}