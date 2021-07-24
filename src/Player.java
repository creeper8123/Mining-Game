import org.w3c.dom.css.RGBColor;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Player{
    public JLabel texture;
    public Rectangle hitbox;
    public final int playerWidth = 32;
    public final int playerHeight = 48;

    public final int textureXOffset = 0;
    public final int textureYOffset = 0;

    public double x;
    public double y;

    public double xSpeed = 0.0;
    public double ySpeed = 0.0;
    public double xSpeedTarget = 0.0;
    public final double xSpeedMax = 2.0;
    public final double xSpeedDeadZone = 0.1;
    public final double xSpeedAcceleration = 0.02;

    public final double airborneManeuverabilityMultiplier = (double) 1/3;

    public final double gravity = 0.05;
    public final double terminalVelocity = 10;
    public double jumpForce = 5.0;

    public boolean onGround = false;

    Player(JFrame frame, double initialX, double initialY){
        this.x = initialX;
        this.y = initialY;
        this.texture = new JLabel();
        this.texture.setBounds(0, 0, this.playerWidth, this.playerHeight);
        this.texture.setBackground(new Color(0, 0, 255, 255));
        this.texture.setOpaque(true);
        this.hitbox = new Rectangle();
        this.hitbox.setBounds((int) this.x, (int) this.y, this.playerWidth, this.playerHeight);
        frame.getContentPane().add(this.texture);
    }

    public void calculateNewPosition(boolean jump, boolean moveLeft, boolean moveRight, boolean crouch){
        if((moveLeft && !moveRight) || (!moveLeft && moveRight)){
            if(moveLeft){
                this.xSpeedTarget = -this.xSpeedMax;
            }else{
                this.xSpeedTarget = this.xSpeedMax;
            }
        }else if(!moveLeft){
            if(this.onGround){
                this.xSpeedTarget = 0;
            }else{
                this.xSpeedTarget = this.xSpeed;
            }
        }

        if((Math.abs(this.xSpeed - this.xSpeedTarget) < this.xSpeedDeadZone) && (!moveLeft || !moveRight)){
            this.xSpeed = this.xSpeedTarget;
        }else if(this.xSpeed > this.xSpeedTarget){
            if(this.onGround){
                this.xSpeed -= this.xSpeedAcceleration;
            }else{
                this.xSpeed -= (this.xSpeedAcceleration * this.airborneManeuverabilityMultiplier);
            }
        }else if(this.xSpeed < this.xSpeedTarget){
            if(this.onGround){
                this.xSpeed += this.xSpeedAcceleration;
            }else{
                this.xSpeed += (this.xSpeedAcceleration * this.airborneManeuverabilityMultiplier);
            }
        }

        //Add Gravity Here
        if(!onGround){
            if(ySpeed < terminalVelocity){
                ySpeed += gravity;
            }else if(ySpeed > terminalVelocity){
                ySpeed = terminalVelocity;
            }
        }


        //Horizontal Collision
        hitbox.x += xSpeed;
        for (Tile[] tileRow :Game.tiles) {
            for (Tile tile:tileRow) {
                if(this.hitbox.intersects(tile.hitbox)) {
                    hitbox.x -= xSpeed;
                    while (!tile.hitbox.intersects(this.hitbox)) {
                        this.hitbox.x += Math.signum(this.xSpeed);
                    }
                    this.hitbox.x -= Math.signum(this.xSpeed);
                    this.xSpeed = 0;
                    this.x = hitbox.x;
                }
            }
        }




        //Vertical Collision
        hitbox.y += ySpeed;
        for (Tile[] tileRow :Game.tiles) {
            for (Tile tile:tileRow) {
                if(this.hitbox.intersects(tile.hitbox)) {
                    hitbox.y -= ySpeed;
                    while (!tile.hitbox.intersects(this.hitbox)) {
                        this.hitbox.y += Math.signum(this.ySpeed);
                    }
                    this.hitbox.y -= Math.signum(this.ySpeed);
                    if(Math.abs(ySpeed) < terminalVelocity && ySpeed > 0){
                        try {
                            Game.sfxManager.playSound("audio/sfx/land.wav");
                        } catch (IOException | UnsupportedAudioFileException e) {
                            e.printStackTrace();
                        }
                    }else if(ySpeed > 0){
                        try {
                            Game.sfxManager.playSound("audio/sfx/land_hard.wav");
                        } catch (IOException | UnsupportedAudioFileException e) {
                            e.printStackTrace();
                        }
                    }
                    this.ySpeed = 0;
                    this.y = hitbox.y;
                }
            }
        }

        hitbox.y++;
        for (Tile[] tileRow :Game.tiles) {
            boolean broken = false;
            for (Tile tile:tileRow) {
                if (tile.hitbox.intersects(this.hitbox)) {
                    onGround = true;
                    broken = true;
                    break;
                } else {
                    onGround = false;
                }
            }
            if(broken){
                break;
            }
        }
        hitbox.y--;

        if(jump && onGround){
            ySpeed = -jumpForce;
            try {
                Game.sfxManager.playSound("audio/sfx/jump.wav");
            } catch (IOException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }


        this.y += this.ySpeed;
        this.x += this.xSpeed;
    }

    public void calculateNewPositionFly(boolean jump, boolean moveLeft, boolean moveRight, boolean crouch){

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
        for (Tile[] tileRow :Game.tiles) {
            for (Tile tile:tileRow) {
                if(this.hitbox.intersects(tile.hitbox)) {
                    hitbox.x -= xSpeed;
                    while (!tile.hitbox.intersects(this.hitbox)) {
                        this.hitbox.x += Math.signum(this.xSpeed);
                    }
                    this.hitbox.x -= Math.signum(this.xSpeed);
                    this.xSpeed = 0;
                    this.x = hitbox.x;
                }
            }
        }

        hitbox.y += ySpeed;
        for (Tile[] tileRow :Game.tiles) {
            for (Tile tile:tileRow) {
                if (this.hitbox.intersects(tile.hitbox)) {
                    hitbox.y -= ySpeed;
                    while (!tile.hitbox.intersects(this.hitbox)) {
                        this.hitbox.y += Math.signum(this.ySpeed);
                    }
                    this.hitbox.y -= Math.signum(this.ySpeed);
                    this.ySpeed = 0;
                    this.y = hitbox.y;
                }
            }
        }

        this.y += this.ySpeed;
        this.x += this.xSpeed;
    }

    public void updatePlayerPosition(double x, double y){
        this.hitbox.setLocation((int) x, (int) y);
        this.texture.setLocation(((int) x)+this.textureXOffset, ((int) y)+this.textureYOffset);
    }
}
