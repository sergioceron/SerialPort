/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package serialport;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 *
 * @author sergio
 */
public class Panel extends JPanel {

    private int height = 10;

    public void setHeight1(int height) {
        this.height = height;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);
        g.setPaintMode();
        g.fill3DRect(0, 300-height, 400, (int) (height), true);
    }
}
