/**
 * (C) 2013 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import static pl.imgw.jrat.tools.out.Logging.PROGRESS_BAR_ONLY;

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
import pl.imgw.jrat.tools.out.ConsoleProgressBar;
import pl.imgw.jrat.tools.out.LogHandler;

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

        ConsoleProgressBar.getProgressBar().initialize(20, files.size(),
                LogHandler.getLogs().getVerbose() == PROGRESS_BAR_ONLY,
                "Printing images...");
        
        for (File file : files) {
            if (!pm.initialize(file))
                continue;
            ic = new ImagesController(options);
            
            DataContainer prod = pm.getProduct();
            String folderName;
            if(file.getName().contains("."))
                folderName = file.getName().split("\\.")[0];
            else
                folderName = file.getName();
            
            /*
             * creating sub-folder for each file
             */
            File out = new File(output, folderName);
            out.mkdirs();
            
            if(prod == null)
                continue;
            
            printToFile(ic, out, prod);
            ConsoleProgressBar.getProgressBar().evaluate();
        }
        ConsoleProgressBar.getProgressBar().printDoneMsg("done");

    }

    private static void printToFile(ImagesController ic, File output,
            DataContainer prod) {

        Map<String, ArrayData> data = new HashMap<String, ArrayData>();

            
        if (ic.getDatasetValue().isEmpty()) {
            data.putAll(prod.getArrayList());
        } else {
            data.put(ic.getDatasetValue(), prod.getArray(ic.getDatasetValue()));
        }

        
        Iterator<String> i = data.keySet().iterator();
        while (i.hasNext()) {
            String name = i.next();
            if (ic.getFormat().toLowerCase().matches("png")) {
                ic.getBuilder().setData(data.get(name));
                ic.getBuilder().saveToFile(
                        new File(output, "array_"
                                + name.replace("[^A-Za-z0-9]", "") + ".png"));
            } else if (ic.getFormat().toLowerCase().matches("txt")) {

                File out = new File(output,
                        "array_" + name.replaceAll("[^A-Za-z0-9]", "") + ".txt");
                
                ArrayFilePrinter.printTXT(data.get(name),  out);
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
