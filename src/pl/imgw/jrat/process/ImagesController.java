/**
 * (C) 2012 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.process;

import pl.imgw.jrat.tools.out.ColorScales;
import pl.imgw.jrat.tools.out.ImageBuilder;
import pl.imgw.util.Log;
import pl.imgw.util.LogManager;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class ImagesController {

    private static Log log = LogManager.getLogger();

    private final String FORMAT = "format=";
    private final String SCALE = "scale=";
    private final String DATASET = "dataset=";
    private final String NODATA = "nodata=";

    private ImageBuilder builder = new ImageBuilder();
    private String dataset;
    private String format;

    /**
     * Setting all require fields for <code>ImageBuilder</code>, if some fields
     * are not provided default value are set
     * 
     * @param args
     *            list of fields name and values for <code>ImageBuilder</code>,
     *            names and values must be separated by '=' e.g. "format=png".
     *            </br> Supported fields are: format, scale, dataset and nodata
     */
    public ImagesController(String args[]) {
        format = "";
        String scale = "";
        String nodata = "";
        dataset = "";

        for (String arg : args) {
            if (arg.contains(FORMAT)) {
                format = arg.replace(FORMAT, "").toLowerCase();
            } else if (arg.contains(SCALE)) {
                scale = arg.replace(SCALE, "").toLowerCase();
            } else if (arg.contains(DATASET)) {
                // dataset = "dataset";
                dataset = arg.replace(DATASET, "");
            } else if (arg.contains(NODATA)) {
                nodata = arg.replace(NODATA, "");
            }
        }

        /* receiving nodata */
        if (!nodata.isEmpty()) {
            try {
                double nd = Double.parseDouble(nodata);
                builder.setNoDataValue(nd);
            } catch (NumberFormatException e) {
                log.printMsg("Value: '" + nodata + "' for " + NODATA
                        + " is incorrect", Log.TYPE_WARNING, Log.MODE_VERBOSE);
                nodata = "";
            }
        }

        /* receiving format */
        if (!format.isEmpty()) {
            builder.setFormat(format);
        }

        /* receiving scale */

        if (scale.matches("rb")) {
            builder.setScale(ColorScales.getRBScale());
        } else if (scale.contains("gray")) {
            if (scale.contains("[") && scale.contains("]")) {
                try {
                    String[] param = scale.substring(scale.indexOf("[") + 1,
                            scale.indexOf("]")).split(",");

                    if (param.length != 2) {
                        log.printMsg("Argument: '" + scale + "' for " + SCALE
                                + " is incomplete, setting default gray scale",
                                Log.TYPE_WARNING, Log.MODE_VERBOSE);
                    } else {
                        int start = Integer.parseInt(removeDigits(param[0]));
                        int max = Integer.parseInt(removeDigits(param[1]));

                        System.out.println(start + " " + max);

                        builder.setScale(ColorScales.getGrayScale(start, max));
                    }
                } catch (NumberFormatException e) {
                    log.printMsg("Argument: '" + scale + "' for " + SCALE
                            + " is incorrect, setting default gray scale",
                            Log.TYPE_WARNING, Log.MODE_VERBOSE);
                    scale = "";
                }
            } else {
                builder.setScale(ColorScales.getGray256Scale());
            }

        }

    }

    public ImageBuilder getBuilder() {
        return builder;
    }

    /**
     * 
     * @return returns empty String if not set
     */
    public String getDatasetValue() {
        return dataset;
    }

    /**
     * 
     * @return returns empty String if not set
     */
    public String getFormat() {
        return format;
    }

    private String removeDigits(String input) {
        char[] arr = input.toCharArray();
        int j = 0;
        for (int i = 0; i < arr.length; ++i) {
            if (Character.isDigit(arr[i]) || arr[i] == '-') {
                arr[j++] = arr[i];
            }
        }
        return new String(arr, 0, j);
    }

}
