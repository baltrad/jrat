/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.trec;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MVCollection {

    private List<MV> allVectors = new ArrayList<MV>();

    public MVCollection() {

    }

    /**
     * Creates collection of motion vectors covers every point in a square from
     * its center
     * 
     * @param range
     *            half-side length of the square
     */
    public MVCollection(int range) {
        for (int x = -range + 1; x < range; x++)
            for (int y = -range + 1; y < range; y++)
                allVectors.add(new MV(x, y));
    }

    /**
     * @return the allVectors
     */
    public List<MV> getAllVectors() {
        return allVectors;
    }

    public static void main(String[] args) {

        MVCollection mvc = new MVCollection(14);
        List<MV> a = mvc.getAllVectors();
        ListIterator<MV> itr = a.listIterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }

    }

}
