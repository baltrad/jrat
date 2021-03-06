/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.wz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pl.imgw.jrat.data.ArrayData;
import pl.imgw.jrat.data.DataContainer;
import pl.imgw.jrat.data.UnsignedByteArray;
import pl.imgw.jrat.data.WZDataContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.IntArrayParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.data.rainbow.XMLDataContainer;
import pl.imgw.jrat.tools.out.ClipboardHandler;
import pl.imgw.jrat.tools.out.ColorScales;
import pl.imgw.jrat.tools.out.ImageBuilder;
import pl.imgw.jrat.tools.out.MapColor;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class DisplayPanel extends Container implements ActionListener, MouseListener {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1048505364299562402L;
    private JComboBox layers;
    private JLabel picture;
    private JTextField filename;
    private JTextField productValue;
    private JPanel down;
    private File fg = null;
    private DataContainer data = null;
    private JFrame frame = null;

    private ArrayData loadedArray;
    private BufferedImage img = null;
    private double nodata = 0;
    private double undetected = 0;
    
    private JButton open;
    private final JFileChooser fc = new JFileChooser();
    private File openedfile;
    
    private HashMap<String, BufferedImage> pics = new HashMap<String, BufferedImage>();
    
    public DisplayPanel() {
        
//        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setLayout(new BorderLayout());
        down = new JPanel();
        down.setLayout(new BoxLayout(down, BoxLayout.LINE_AXIS));
        
        
        picture = new JLabel();
        picture.setPreferredSize(new Dimension(760, 800));
        picture.addMouseListener(this);

        layers = new JComboBox();
        layers.addActionListener(this);
        layers.addItem("No layers");
        
        open = new JButton("Open");
        filename = new JTextField("no file loaded");
        filename.setEditable(false);

        productValue = new JTextField();
        productValue.setEditable(false);
        
        setValue(0, 0, 0.0);
        
        open.addActionListener(this);
        
        down.add(open);
        down.add(Box.createRigidArea(new Dimension(10, 0)));
        down.add(filename);
        down.add(Box.createRigidArea(new Dimension(10, 0)));
        down.add(productValue);
        down.add(Box.createRigidArea(new Dimension(10, 0)));
        down.add(layers);
        down.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
//        add(new JLabel("taki"));
        add(picture, BorderLayout.NORTH);
//        add(Box.createRigidArea(new Dimension(0, 5)));
//        add(new JSeparator(SwingConstants.HORIZONTAL));
//        add(Box.createRigidArea(new Dimension(0, 5)));
        add(down, BorderLayout.SOUTH);
        
    }
    
    
    /**
     * @param i
     * @param j
     * @param d
     * @return
     */
    private void setValue(int x, int y, double v) {
        String text = "x=" + x + " y=" + y + " value";
        if(v == nodata)
            text += "=nodata";
        else if (v == undetected)
            text += "<threshold";
        else
            text += "="+v;
        productValue.setText(text);
    }


    public void setData(DataContainer data, String name) {

        this.data = data;
        
        layers.removeAllItems();
        for (String s : data.getArrayList().keySet())
            layers.addItem(s);
        
        ArrayData first = data.getArray(data.getArrayList().keySet().iterator().next());
               
        picture.setPreferredSize(new Dimension(first.getSizeX(), first
                .getSizeY()));
        frame.pack();
        filename.setText(name);
        
    }
    
    private void updatePic() {
        if(img != null) {
            picture.setOpaque(true);
            picture.setBackground(Color.black);
            picture.setIcon(new ImageIcon(img));
            picture.revalidate();
//            revalidate();
            frame.pack();
        }
        
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(layers)) {
            img = pics.get(layers.getSelectedItem());
            if (img == null) {
                img = loadImage((String) layers.getSelectedItem());
            }
            updatePic();
        } else if (e.getSource().equals(open)) {

            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                openedfile = fc.getSelectedFile();
                filename.setText("loading...");
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                ParserManager pm = new ParserManager();
                pm.setParser(new DefaultParser());
                if (pm.initialize(openedfile)) {
                    setData(pm.getProduct(), openedfile.getName());
                } else {
                    filename.setText("cannot display the file");
                }
                frame.setCursor(Cursor.getDefaultCursor());
            }

        }

    }

    /**
     * @param selectedItem
     * @return
     */
    private BufferedImage loadImage(String selectedItem) {
        if(data == null) {
            return null;
        }
        if(selectedItem == null)
            return null;
        String par = selectedItem.substring(selectedItem.indexOf(":") + 1, selectedItem.length());
        
        loadedArray = data.getArray(selectedItem);
        Set<MapColor> scale = ColorScales.getRedScale(1, 3);
        if (data instanceof WZDataContainer) {
            nodata = ((WZDataContainer) data).getNodata()
                    * ((UnsignedByteArray) loadedArray).getGain()
                    + ((UnsignedByteArray) loadedArray).getOffset();
            undetected = ((WZDataContainer) data).getBelowth()
                    * ((UnsignedByteArray) loadedArray).getGain()
                    + ((UnsignedByteArray) loadedArray).getOffset();

            double min = ((UnsignedByteArray) loadedArray).getOffset() + 2
                    * ((UnsignedByteArray) loadedArray).getGain();
            double step = 5 * ((UnsignedByteArray) loadedArray).getGain();
            scale = ColorScales.getRedScale(min, step);
            fg = new File("overlay", "wz_fg.png");
        } else if (data instanceof XMLDataContainer) {
            scale = ColorScales.getRBScale();
            if(par.contains("flag")) {
                ((UnsignedByteArray) loadedArray).setGain(0);
                scale = ColorScales.getGrayScale(0, 1);
            }
            fg = null;
        } else {
            fg = null;
        }
        
        if(par.matches("WZ")) {
            scale = ColorScales.getWZScale();
            nodata = -1;
            undetected = -1;
        }
//        System.out.println("parametr wczytany: " + par);
        
        /*
        else if (par.matches("Z")) {
            ;
        } else if (par.matches("V")) {
            ;
        } else if (par.matches("Tb")) {
            ;
        } else if (par.matches("ShV")) {
            ;
        } else if (par.matches("ShH")) {
            ;
        }
        */
        
        return new ImageBuilder()
        // .setDarker(true)
        .setData(loadedArray)
        .setNoDataValue(nodata)
        .setNoDetectedValue(undetected)
        .setForeground(fg)
        .setTransparency(255)
        .setScale(scale)
        .hasCaption(true)
        .create();
    }

    /**
     * @param frame
     */
    public void setFrame(JFrame frame) {
        this.frame = frame;
        
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(picture)) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (loadedArray != null) {
                    int x = e.getPoint().x;
                    int y = e.getPoint().y;
                    double v = loadedArray.getPoint(x, y);
                    setValue(x, y, v);
                }
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (img != null) {
                    
                    ClipboardHandler.setClipboard(img);
                    JOptionPane.showMessageDialog(this, "Image copied to clipboard", "Success",
                            JOptionPane.DEFAULT_OPTION);
                }
            }
        }

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    
}
