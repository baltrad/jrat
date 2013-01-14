/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.calid;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.arrays.IntDataArray;
import pl.imgw.jrat.projection.ProjectionUtility;

/**
 * 
 * This class is responsible for creating 2D array, representing spatial
 * redistribution of compared overlapping points. 
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Results2DManager {

    public static final short NO_DATA = 9999;

    Point source1;
    Point source2;
    String name1;
    String name2;
    ArrayData data;

    public Results2DManager(ArrayList<PairedPoints> array, Pair pair) {

        Point2D.Double p = new Point2D.Double(pair.getVol1().getLon(), pair
                .getVol1().getLat());
        p = ProjectionUtility.projectToCartesian(p, new String[] {
                "+proj=merc", "+ellps=sphere" });

        double xmax = p.x, xmin = p.x, ymax = p.y, ymin = p.y;
        p = new Point2D.Double(pair.getVol2().getLon(), pair.getVol2().getLat());
        p = ProjectionUtility.projectToCartesian(p, new String[] {
                "+proj=merc", "+ellps=sphere" });

        if (p.x > xmax) {
            xmax = p.x;
        }
        if (p.y > ymax)
            ymax = p.y;
        if (p.x < xmin)
            xmin = p.x;
        if (p.y < ymin)
            ymin = p.y;

        Iterator<PairedPoints> itr = array.iterator();
        while (itr.hasNext()) {
            PairedPoints pp = itr.next();
            p = ProjectionUtility.projectToCartesian(pp.getCoord1(),
                    new String[] { "+proj=merc", "+ellps=sphere" });
            // System.out.println(p.x / 1000 + " " + p.y / 1000);
            if (p.x > xmax) {
                xmax = p.x;
            }
            if (p.y > ymax)
                ymax = p.y;
            if (p.x < xmin)
                xmin = p.x;
            if (p.y < ymin)
                ymin = p.y;
        }

        int xsize = (int) ((xmax - xmin) / 1000) +1;
        int ysize = (int) ((ymax - ymin) / 1000) +1;

//        System.out.println("xsize, ysize=" + xsize + ", " + ysize);

        int[][] pic = new int[xsize][ysize];

        for (int[] row : pic) {
            Arrays.fill(row, NO_DATA);
        }

        itr = array.iterator();

        int x, y;

        while (itr.hasNext()) {
            PairedPoints pp = itr.next();
            p = ProjectionUtility.projectToCartesian(pp.getCoord1(),
                    new String[] { "+proj=merc", "+ellps=sphere" });
            x = (int) ((p.x - xmin) / 1000);
            y = ysize - (int) ((p.y - ymin) / 1000) - 1;
            pic[x][y] = (pp.getDifference() != null) ? (short) Math.round(pp
                    .getDifference()) : NO_DATA;
        }

        p = new Point2D.Double(pair.getVol1().getLon(), pair.getVol1().getLat());
        p = ProjectionUtility.projectToCartesian(p, new String[] {
                "+proj=merc", "+ellps=sphere" });
        x = (int) ((p.x - xmin) / 1000);
        y = ysize - (int) ((p.y - ymin) / 1000) - 1;
        source1 = new Point(x, y);
        name1 = pair.getSource1();

        p = new Point2D.Double(pair.getVol2().getLon(), pair.getVol2().getLat());
        p = ProjectionUtility.projectToCartesian(p, new String[] {
                "+proj=merc", "+ellps=sphere" });
        x = (int) ((p.x - xmin) / 1000);
        y = ysize - (int) ((p.y - ymin) / 1000);
        source2 = new Point(x, y);
        name2 = pair.getSource2();

        data = new IntDataArray(pic);

    }

    /**
     * @return the source1
     */
    public Point getSource1() {
        return source1;
    }

    /**
     * @return the source2
     */
    public Point getSource2() {
        return source2;
    }

    /**
     * @return the name1
     */
    public String getName1() {
        return name1;
    }

    /**
     * @return the name2
     */
    public String getName2() {
        return name2;
    }

    /**
     * @return the data
     */
    public ArrayData getData() {
        return data;
    }

}
