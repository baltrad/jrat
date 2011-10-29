/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import ncsa.hdf.object.Group;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public interface OdimH5File {
    
    /**
     * Set all attributes from root group
     * @param root
     * @return
     */
    public boolean initializeFromRoot(Group root);

    /**
     * Returns array data from the file of given path e.g. /dataset2/data1
     * 
     * @param path
     * @return
     */
    public OdimH5Dataset getDataset(String path);
    
    /**
     * Display all attributes as a tree
     */
    public void displayTree();
    
    /**
     * 
     * @param verbose
     */
    public void printGeneralInfo(boolean verbose);
}
