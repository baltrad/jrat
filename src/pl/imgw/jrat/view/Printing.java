/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.view;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import pl.imgw.jrat.data.hdf5.ArrayData;
import pl.imgw.jrat.data.hdf5.OdimH5Dataset;
import pl.imgw.jrat.data.hdf5.OdimH5File;
import pl.imgw.jrat.util.LogsHandler;
import pl.imgw.jrat.util.MessageLogger;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class Printing {

    /**
     * Print scan selected in command line argument in new frame
     * 
     * @param odim
     */
    public static void printScan(OdimH5File odim, String dsName, boolean verbose) {
        
        try {

            OdimH5Dataset dataset = odim.getDataset(dsName);

            if (dataset == null) {
                MessageLogger.showMessage(dsName + " does not exist ", true);
                return;
            }
            
            ArrayData data = null;
            if(dataset.getData().length == 1)
                data = dataset.getData()[0].getArray();
            else if (dataset.getData().length>1){
                String[] groups = dsName.split("/");
                if (groups.length < 2) {
                    System.out
                            .println("More then 1 data in dataset. Specify the number e.g. "
                                    + groups[0] + "/data1");
                }
                for(int i =0; i < groups.length; i++)
                    if(dataset.getData()[i].getDataName().contains(groups[1]))
                        data = dataset.getData()[i].getArray();
            }
            
//            MessageLogger.showMessage("data ready to print", verbose);

            PictureFromArray pic = new PictureFromArray(data.getData(),
                    ColorScales.getGray256Scale());
            
            try {

                String folder = "/home/vrolok/Pulpit/";

                File file = new File(folder + "printed.png");
                ImageIO.write(pic.getImg(), "png", file);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
//            ImageFrame frame = new ImageFrame(pic.getImg(), dataset.getProduct(),
//                    data.getSizeX(), data.getSizeY());

            String mes = "";
            mes += dataset.getDatasetname() + " printed:\n";
//            mes += "Elevation\t" + dataset.getElangle() +"\n";
            mes += "Scan start\t" + dataset.getFullStartDate() +"\n";
            mes += "Scan end\t" + dataset.getFullEndDate() +"\n";

            MessageLogger.showMessage(mes, verbose);
//            frame.displayImage();
        } catch (Exception e) {
            MessageLogger.showMessage("Couldn't print a map", true);
            LogsHandler.saveProgramLogs("DataProcessorController",
                    e.getMessage());
        }
    }
    
}
