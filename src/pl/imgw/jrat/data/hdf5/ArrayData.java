/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import pl.imgw.jrat.trec.MV;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ArrayData {

    protected int[][] data = null;
    protected int sizeX = 0;
    protected int sizeY = 0;
    

    public ArrayData(int sizeX, int sizeY) {

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.data = new int[sizeX][sizeY];
    }

    /**
     * 
     * @param x
     * @param y
     * @param value
     * @return
     */
    public boolean setPoint(int x, int y, int value) {

        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }

        data[x][y] = value;

        return true;
    }
    /**
     * 
     * @param x
     * @param y
     * @param mv
     * @param value
     * @return
     */

    public boolean setPoint(int x, int y, MV mv, int value) {
        
        if (mv == null) {
            return false;
        }
        
        if (x + mv.getX() < 0 || y + mv.getY() < 0 || x + mv.getX() >= sizeX
                || y + mv.getY() >= sizeY) {
            return false;
        }
        
        data[x + mv.getX()][y + mv.getY()] = value;
        
        return true;
    }

    
    /**
     * 
     * @param x
     * @param y
     * @param mv
     * @param t
     * @param value
     * @return
     */
    public boolean setPoint(int x, int y, MV mv, double t, int value) {

        if (mv == null) {
            return false;
        }
        
        x = (int) ((x + t * mv.getX()));
        y = (int) ((y + t * mv.getY()));
        
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return false;
        }

        data[x][y] = value;

        return true;
    }
    
    
    /* 
     * Do test�w
     */
    public static void main(String[] args) {
        
        ArrayData dc = new ArrayData(10, 10);
        dc.setPoint(0, 0, new MV(4, 5), 0.3, 5);

        for (int y = 0; y < dc.sizeY; y++) {
            for (int x = 0; x < dc.sizeX; x++) {
                System.out.print(dc.getPoint(x, y) + " ");
            }
            System.out.print("\n");
        }
        
    }

    /**
     * 
     * @param x
     * @param y
     * @return
     */
    public int getPoint(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            // System.out.println(index ++);
            return 0;
        }
        return data[x][y];
    }

    /**
     * 
     * @param x
     * @param y
     * @param mv
     * @return
     */
    public int getPoint(int x, int y, MV mv) {

        if (mv == null) {
            return data[x][y];
        }

        if (x + mv.getX() < 0 || y + mv.getY() < 0 || x + mv.getX() >= sizeX
                || y + mv.getY() >= sizeY) {
            return 0;
        }
        return data[x + mv.getX()][y + mv.getY()];

    }

    /**
     * 
     * @param x
     * @param y
     * @param mv
     * @param t
     * @return
     */
    public int getPoint(int x, int y, MV mv, double t) {

        if (mv == null) {
            return data[x][y];
        }

        x = (int) ((x + t * mv.getX()));
        y = (int) ((y + t * mv.getY()));

        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {
            return 0;
        }
        return data[x][y];

    }

    /**
     * @return the sizeX
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * @return the sizeY
     */
    public int getSizeY() {
        return sizeY;
    }

    /**
     * @return the data
     */
    public int[][] getData() {
        return data;
    }

    /**
     * 
     * @param x
     * @param y
     * @return
     */
    public boolean incrementPoint(int x, int y) {

        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) {

            return false;
        }

        data[x][y]++;

        return true;

    }

    /**
     * 
     * @param x
     * @param y
     * @return
     */
    public boolean checkPoint(int x, int y) {
        return (x < 0 || y < 0 || x >= sizeX || y >= sizeY);
           
    }
    
}
