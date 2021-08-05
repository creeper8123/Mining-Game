package UserInputs.MouseInputs;

import javax.swing.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseScrollInput implements MouseWheelListener {

    public MouseScrollInput(JFrame frame){
        frame.addMouseWheelListener(this);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
