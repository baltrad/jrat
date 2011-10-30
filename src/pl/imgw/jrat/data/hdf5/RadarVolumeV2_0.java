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
public class RadarVolumeV2_0 extends RadarVolume implements OdimH5File{

    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.hdf5.OdimH5File#initializaFromRoot(ncsa.hdf.object.Group)
     */
    @Override
    public boolean initializeFromRoot(Group root) {
        // TODO Auto-generated method stub
        return false;
    }


    /* (non-Javadoc)
     * @see pl.imgw.jrat.data.hdf5.OdimH5File#displayTree()
     */
    @Override
    public void displayTree() {
        // TODO Auto-generated method stub
        
    }

}
