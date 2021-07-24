package UserInputs;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput implements KeyListener {

    public boolean upPressed;
    public boolean leftPressed;
    public boolean downPressed;
    public boolean rightPressed;

    public KeyInput(JFrame frame){
        frame.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w', ' ' -> upPressed = true;
            case 'a' -> leftPressed = true;
            case 's' -> downPressed = true;
            case 'd' -> rightPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w', ' ' -> upPressed = false;
            case 'a' -> leftPressed = false;
            case 's' -> downPressed = false;
            case 'd' -> rightPressed = false;
        }
    }
}
