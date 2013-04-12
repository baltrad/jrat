/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.data.arrays.ArrayData;
import pl.imgw.jrat.data.containers.DataContainer;
import pl.imgw.jrat.data.containers.ImageContainer;
import pl.imgw.jrat.data.containers.ScanContainer;
import pl.imgw.jrat.data.containers.VolumeContainer;
import pl.imgw.jrat.data.containers.WZDataContainer;
import pl.imgw.jrat.data.parsers.DefaultParser;
import pl.imgw.jrat.data.parsers.GlobalParser;
import pl.imgw.jrat.data.parsers.ParserManager;
import pl.imgw.jrat.tools.out.ArrayFilePrinter;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class PrintingImageProcessController {

    /**
     * 
     * @param files
     * @param output output folder
     * @param options
     */
    public static void printImage(List<File> files, File output,
            String[] options) {

        // LogHandler.getLogs().displayMsg("Printing image from file", WARNING);
        ImagesController ic = null;
        ParserManager pm = new ParserManager();

        pm.setParser(GlobalParser.getInstance().getParser());
        ic = new ImagesController(options);

        for (File file : files) {
            if (!pm.initialize(file))
                continue;
            DataContainer prod = pm.getProduct();
            
            if(prod == null)
                continue;
            
            printToFile(ic, output, prod);

        }

    }

    private static void printToFile(ImagesController ic, File output,
            DataContainer prod) {

        Map<String, ArrayData> data = new HashMap<String, ArrayData>();

//        System.out.println("1");
        
        
//            System.out.println("2");
            
        if (ic.getDatasetValue().isEmpty()) {
//            System.out.println("2");
            data.putAll(prod.getArrayList());
        } else {
//            System.out.println("3");
            data.put(ic.getDatasetValue(), prod.getArray(ic.getDatasetValue()));
        }

        
        Iterator<String> i = data.keySet().iterator();
        int a = 0;
        while (i.hasNext()) {
//            System.out.println("a=" + a++);
            String name = i.next();
            if (ic.getFormat().toLowerCase().matches("png")) {
                ic.getBuilder().setData(data.get(name));
                ic.getBuilder().saveToFile(
                        new File(output, "array_"
                                + name.replace("[^A-Za-z0-9]", "") + ".png"));
            } else if (ic.getFormat().toLowerCase().matches("txt")) {

                ArrayFilePrinter.printTXT(data.get(name), new File(output,
                        "array_" + name.replace("[^A-Za-z0-9]", "") + ".txt"));
            }
        }
    }
    
    /**
     * @param output
     * @param ic
     * @param file
     */
    private static void printToFile(ImagesController ic, File output,
            ArrayData array, String outputName) {

        
        
        File imgout = new File(output, outputName);
        System.out.println(outputName);
        if (ic.getFormat().toLowerCase().matches("png")) {
            ic.getBuilder().setData(array);
            ic.getBuilder().saveToFile(imgout);
            
        } else if (ic.getFormat().toLowerCase().matches("txt")) {
            ArrayFilePrinter.printTXT(array, imgout);
        }
    }
    
}
