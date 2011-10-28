/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.view;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageFrame{
    /**
     * 
     */
    private static final long serialVersionUID = -5488520407354198375L;
    BufferedImage image = null;  
    Dimension size = new Dimension();
    String description;


    public ImageFrame(BufferedImage image, String description, int width, int height) {
        this.image = image;
        size.setSize(image.getWidth(), image.getHeight());
        this.description = description;
        
        
    }
    
    public void displayImage() {
        
        JLabel label = new JLabel(new ImageIcon(image));
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(label);
        f.setTitle(description);
        f.setSize(size);
        f.pack();
        f.setLocation(200,200);
        f.setVisible(true);
        
    }
    
    
}