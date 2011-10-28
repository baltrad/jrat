/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.trec;

import java.util.List;
import java.util.ListIterator;

import pl.imgw.jrat.data.hdf5.ArrayData;
import pl.imgw.jrat.data.hdf5.RadarProduct;

/**
 * 
 * Contains constructor of motion vector field and methods for moving radar products.
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class MVFactory {
    
    private MVPrepContainer prep0 = new MVPrepContainer();
    private MVPrepContainer prep1 = new MVPrepContainer();
    
    /**
     * 
     */
    public MVFactory() {

    }

    /*
     * 
     * for test 
     *
    public MVFactory(String fileName, String folder, int dbzThreshold, int gridsize) {

        DataSource a = DataFactory.newData(fileName, folder, null);

        MVPrepContainer prep0 = new MVPrepContainer();

        if (a.isRadarData()) {
            RadarProduct rd = (RadarProduct) a;

            prep0.setData(getData(rd, dbzThreshold));
            prep0.setGridsize(gridsize);
        }

    }
     */


    /**
     * 
     * Constructs motion vector field based on two radar products.
     * 
     * @param rp0
     *            radar product of time t0
     * @param rp1
     *            radar product of time t+1
     * @param dbzThreshold
     *            lowest dBZ value that is taken to calculations
     * @param corrthreshold
     *            minimal correlation that makes the motion vector valid
     * @param gridsize
     *            size of grids that product is divided by
     * @param allVectors
     *            all vectors that are check when calculating correlation
     */
    public MVFactory(RadarProduct rp0, RadarProduct rp1, int dbzThreshold,
            double corrthreshold, int gridsize, List<MV> allVectors) {

        prep0.setData(getData(rp0, dbzThreshold));
        prep0.setGridsize(gridsize);
        prep1.setData(getData(rp1, dbzThreshold));
        prep1.setGridsize(gridsize);
        

        ArrayData dens0 = densityForGrid(prep0.getData(), gridsize, 20);
        ArrayData dens1 = densityForGrid(prep1.getData(), gridsize, 20);
        consolidDensity(dens0, gridsize, prep0);
        consolidDensity(dens1, gridsize, prep1);
        prepareAndCalculate(prep0, prep1, corrthreshold, allVectors);
        expandAggrMap(prep1, corrthreshold);
        expandAggrMap(prep1, corrthreshold);
        expandAggrMap(prep1, corrthreshold);
        expandAggrMap(prep1, corrthreshold);
        expandAggrMap(prep1, corrthreshold);

        // set mv field

    }

    
    /**
     * 
     * Returns forecast based on products used in constructing the class.
     * 
     * @param time
     * @return
     */
    public ArrayData moveData(double time) {

        if(!prep1.isValid())
            return null;
        
        int gridSize = prep1.getGridsize();
        
        ArrayData data = prep1.getData();

        ArrayData newData = new ArrayData(data.getSizeX(), data
                .getSizeY());

        MV mv = null;
        double cor = 0;
        int value = 0, index;

        for (int y = 0; y < data.getSizeY(); y++)
            for (int x = 0; x < data.getSizeX(); x++) {
                value = prep1.getData().getPoint(x, y);
                index = prep1.getAggrData()
                        .getPoint(x / gridSize, y / gridSize);

                if (index != 0) {
                    if (value > 0) {
                        // System.out.println(index + " " + value);
                        mv = prep1.getCorrMV()[index - 1].getMv();
                        cor = prep1.getCorrMV()[index - 1].getCorrelation();
                        if (newData.getPoint(x, y, mv, -time) < value
                                && cor > 0.6 && prep1.getAggrValidGrid()[index - 1])
                            newData.setPoint(x, y, mv, -time, value);
                        //
                    }

                }
            }

        depixelizing(newData, 3);
        depixelizing(newData, 2);
        depixelizing(newData, 1);

        return newData;

    }
    
    /**
     * Returns forecast made by moving given radar product by calculated motion vector
     * 
     * @param rp
     * @param time
     * @return
     */
    public ArrayData moveData(RadarProduct rp, double time) {

        int xmax = rp.getData().getSizeX();
        int ymax = rp.getData().getSizeY();
        
        ArrayData data = new ArrayData(xmax, ymax);

        for (int y = 0; y < ymax; y++)
            for (int x = 0; x < xmax; x++)
                    data.setPoint(x, y, rp.getData().getPoint(x, y));
        
        if(!prep1.isValid())
            return null;
        
        int gridSize = prep1.getGridsize();
        
        ArrayData newData = new ArrayData(data.getSizeX(), data
                .getSizeY());

        MV mv = null;
        double cor = 0;
        int value = 0, index;

        for (int y = 0; y < data.getSizeY(); y++)
            for (int x = 0; x < data.getSizeX(); x++) {
                value = prep1.getData().getPoint(x, y);
                index = prep1.getAggrData()
                        .getPoint(x / gridSize, y / gridSize);

                if (index != 0) {
                    if (value > 0) {
                        // System.out.println(index + " " + value);
                        mv = prep1.getCorrMV()[index - 1].getMv();
                        cor = prep1.getCorrMV()[index - 1].getCorrelation();
                        if (newData.getPoint(x, y, mv, -time) < value
                                && cor > 0.6 && prep1.getAggrValidGrid()[index - 1])
                            newData.setPoint(x, y, mv, -time, value);
                        //
                    }

                }
            }

        depixelizing(newData, 3);
        depixelizing(newData, 2);
        depixelizing(newData, 1);

        return newData;

    }
    
    /**
     * 
     * @param rp
     * @param threshold
     * @return
     */
    private ArrayData getData(RadarProduct rp, int threshold) {
        ArrayData data;
        int ymax;
        int xmax;
        ymax = rp.getData().getSizeX();
        xmax = rp.getData().getSizeY();
        
        data = new ArrayData(xmax, ymax);

        for (int y = 0; y < ymax; y++)
            for (int x = 0; x < xmax; x++) {
                if (rp.getData().getPoint(x, y) > dBZ2raw(threshold))
                    data.setPoint(x, y, rp.getData().getPoint(x, y));
                else
                    data.setPoint(x, y, 0);
            }
        depixelizing(data, 3);
        depixelizing(data, 2);
        depixelizing(data, 1);
        return data;
    }

    /**
     * 
     * @param array
     * @param dist
     */
    private void depixelizing(ArrayData array, int dist) {

        if (array == null)
            return;

        int xmax = array.getSizeX();
        int ymax = array.getSizeY();

        for (int y = 0; y < ymax; y++)
            for (int x = 0; x < xmax; x++) {
                if (array.getPoint(x, y) > 0) {

                    if ((array.getPoint(x + dist, y) == 0)
                            && (array.getPoint(x - dist, y) == 0)
                            && (array.getPoint(x, y + dist) == 0)
                            && (array.getPoint(x, y - dist) == 0)) {
                        for (int yz = -dist; yz < dist; yz++)
                            for (int xz = -dist; xz < dist; xz++)
                                array.setPoint(x + yz, y + xz, 0);
                    }

                }
            }

    }

    /**
     * Converts dBZ values to Rainbow 8-bit data
     * 
     * @param x
     * @return
     */
    private int dBZ2raw(double x) {

        return (int) (2 * (x + 31.5)) + 1;
    }

    /**
     * 
     * @param array
     * @param gridSize
     * @param threshold
     * @return
     */
    private ArrayData densityForGrid(ArrayData array,
            int gridSize, int threshold) {

        if (array == null)
            return null;

        int xmax = array.getSizeX();
        int ymax = array.getSizeY();

        int yGridSize = ymax / gridSize;
        int xGridSize = xmax / gridSize;

        threshold = threshold * (gridSize * gridSize) / 100;

        ArrayData density = new ArrayData(xGridSize, yGridSize);

        for (int x = 0; x < xmax; x++)
            for (int y = 0; y < ymax; y++) {
                if (array.getPoint(x, y) > 0) {
                    density.incrementPoint(x / gridSize, y / gridSize);
                }
            }
        for (int x = 0; x < xGridSize; x++)
            for (int y = 0; y < yGridSize; y++) {
                if (density.getPoint(x, y) < threshold)
                    density.setPoint(x, y, 0);
            }

        return density;
    }

    /**
     * 
     * @param density
     * @param gridSize
     * @param mvctr
     */
    private void consolidDensity(ArrayData density, int gridSize,
            MVPrepContainer dc) {
        int xdens = density.getSizeX();
        int ydens = density.getSizeY();

        int xmax = dc.getData().getSizeX();
        int ymax = dc.getData().getSizeY();

        ArrayData aggrMapTemp = new ArrayData(xdens, ydens);
        ArrayData aggrMap = new ArrayData(xmax, ymax);

        int index = 1;

        for (int y = 0; y < ydens; y++)
            for (int x = 0; x < xdens; x++) {
                if (aggragade(density, aggrMapTemp, x, y, index))
                    index++;

            }
        System.out.println("index=" + (index - 1));

        for (int y = 0; y < ymax; y++) {
            for (int x = 0; x < xmax; x++) {
                aggrMap.setPoint(x, y,
                        aggrMapTemp.getPoint(x / gridSize, y / gridSize));
            }

        }

        dc.setAggrData(aggrMapTemp);
        dc.setNumberOfAggr(index - 1);
    }

    /**
     * 
     * @param array
     * @param aggr
     * @param x
     * @param y
     * @param index
     * @return
     */
    private static boolean aggragade(ArrayData array, ArrayData aggr,
            int x, int y, int index) {

        if (aggr.getPoint(x, y) == 0 && array.getPoint(x, y) > 0) {
            aggr.setPoint(x, y, index);
            // _ x _
            // _ o _
            // _ _ _
            if (array.checkPoint(x, y - 1)) {
                aggragade(array, aggr, x, y - 1, index);
            }
            // _ _ x
            // _ o _
            // _ _ _
            if (array.checkPoint(x + 1, y - 1)) {
                aggragade(array, aggr, x + 1, y - 1, index);
            }
            // _ _ _
            // _ o x
            // _ _ _
            if (array.checkPoint(x + 1, y)) {
                aggragade(array, aggr, x + 1, y, index);
            }
            // _ _ _
            // _ o _
            // _ _ x
            if (array.checkPoint(x + 1, y + 1)) {
                aggragade(array, aggr, x + 1, y + 1, index);
            }
            // _ _ _
            // _ o _
            // _ x _
            if (array.checkPoint(x, y + 1)) {
                aggragade(array, aggr, x, y + 1, index);
            }
            // _ _ _
            // _ o _
            // x _ _
            if (array.checkPoint(x - 1, y + 1)) {
                aggragade(array, aggr, x - 1, y + 1, index);
            }
            // _ _ _
            // x o _
            // _ _ _
            if (array.checkPoint(x - 1, y)) {
                aggragade(array, aggr, x - 1, y, index);
            }
            // x _ _
            // _ o _
            // _ _ _
            if (array.checkPoint(x - 1, y - 1)) {
                aggragade(array, aggr, x - 1, y - 1, index);
            }
            return true;
        }
        return false;
    }

    private boolean prepareAndCalculate(MVPrepContainer dataT0,
            MVPrepContainer dataT1, double corrthreshold, List<MV> allVectors) {

        int gridSize = dataT0.getGridsize();
        
        int consSet = dataT0.getNumberOfAggr();

        int[] consCellSize = new int[consSet];
        boolean[] consCellValid = new boolean[consSet];

        for (int y = 0; y < dataT0.getAggrData().getSizeY(); y++)
            for (int x = 0; x < dataT0.getAggrData().getSizeX(); x++) {
                if (dataT0.getAggrData().getPoint(x, y) > 0)
                    consCellSize[dataT0.getAggrData().getPoint(x, y) - 1]++;
            }

        for (int i = 0; i < consSet; i++) {
            consCellSize[i] *= (gridSize * gridSize);
        }

        for (int i = 0; i < consSet; i++) {
            boolean good = false;
            for (int y = 0; y < dataT0.getAggrData().getSizeY(); y++)
                for (int x = 0; x < dataT0.getAggrData().getSizeX(); x++) {
                    if (dataT0.getAggrData().getPoint(x, y) == (i + 1)) {
                        if (dataT1.getAggrData().getPoint(x, y) > 0) {
                            good = true;
                        }
                    }
                }
            if (good)
                consCellValid[i] = true;
        }

        MVCorrelation[] corMV = new MVCorrelation[consSet];
        for (int i = 0; i < consSet; i++) {
            corMV[i] = new MVCorrelation(0, null);
        }

        ListIterator<MV> itr = allVectors.listIterator();

        while (itr.hasNext()) {

            MVDataForCorrelation[] correlation = new MVDataForCorrelation[consSet];
            for (int i = 0; i < consSet; i++) {
                correlation[i] = new MVDataForCorrelation(consCellSize[i]);
            }

            MV mv = itr.next();
            for (int y = 0; y < dataT0.getData().getSizeY(); y++)
                for (int x = 0; x < dataT0.getData().getSizeX(); x++) {
                    if (dataT0.getAggrData().getPoint(x / gridSize, y / gridSize) > 0) {

                        correlation[dataT0.getAggrData().getPoint(
                                x / gridSize, y / gridSize) - 1].addPoint(
                                dataT0.getData().getPoint(x, y, mv), dataT1
                                        .getData().getPoint(x, y));
                    }

                }

            double cor = 0;

            for (int i = 0; i < consSet; i++) {
                cor = getCorrelation(correlation[i].getData1(),
                        correlation[i].getData2());

                // if(cor > corMV[i].getCorrelation() && cor > corrthreshold) {
                if (cor > corMV[i].getCorrelation()) {
                    corMV[i] = new MVCorrelation(cor, mv);
                }

            }
        }

        dataT0.setCorrMV(corMV);
        dataT1.setAggrValidGrid(consCellValid);
        dataT1.setCorrMV(corMV);
        dataT1.setNumberOfAggr(dataT0.getNumberOfAggr());
        dataT1.setAggrData(dataT0.getAggrData());

        for (int i = 0; i < consSet; i++) {
            System.out.println((i + 1) + " " + corMV[i] + " "
                    + consCellValid[i]);
        }

        return true;
    }

    /**
     * 
     * @param scores1
     * @param scores2
     * @return
     */
    private double getCorrelation(double[] scores1, double[] scores2) {
        double result = 0;
        double sum_sq_x = 0;
        double sum_sq_y = 0;
        double sum_coproduct = 0;
        double mean_x = scores1[0];
        double mean_y = scores2[0];
        for (int i = 2; i < scores1.length + 1; i += 1) {
            double sweep = Double.valueOf(i - 1) / i;
            double delta_x = scores1[i - 1] - mean_x;
            double delta_y = scores2[i - 1] - mean_y;
            sum_sq_x += delta_x * delta_x * sweep;
            sum_sq_y += delta_y * delta_y * sweep;
            sum_coproduct += delta_x * delta_y * sweep;
            mean_x += delta_x / i;
            mean_y += delta_y / i;
        }
        double pop_sd_x = (double) Math.sqrt(sum_sq_x / scores1.length);
        double pop_sd_y = (double) Math.sqrt(sum_sq_y / scores1.length);
        double cov_x_y = sum_coproduct / scores1.length;
        result = cov_x_y / (pop_sd_x * pop_sd_y);
        return result;
    }

    private void expandAggrMap(MVPrepContainer prep, 
            double corTreshold) {

        int sizeX = prep.getAggrData().getSizeX();
        int sizeY = prep.getAggrData().getSizeY();

        ArrayData newAggr = new ArrayData(sizeX, sizeY);

        for (int y = 0; y < sizeY; y++)
            for (int x = 0; x < sizeX; x++) {
                if (prep.getAggrData().getPoint(x, y) > 0
                        && prep.getAggrValidGrid()[prep.getAggrData().getPoint(
                                x, y) - 1]
                        && prep.getCorrMV()[prep.getAggrData().getPoint(x, y) - 1]
                                .getCorrelation() > corTreshold) {
                    newAggr.setPoint(x, y, prep.getAggrData().getPoint(x, y));
                    if (newAggr.checkPoint(x + 1, y))
                        checkCorrAndSet(prep, newAggr, x, y, x + 1, y);

                    if (newAggr.checkPoint(x + 1, y + 1))
                        checkCorrAndSet(prep, newAggr, x, y, x + 1, y + 1);

                    if (newAggr.checkPoint(x + 1, y - 1))
                        checkCorrAndSet(prep, newAggr, x, y, x + 1, y - 1);

                    if (newAggr.checkPoint(x, y + 1))
                        checkCorrAndSet(prep, newAggr, x, y, x, y + 1);

                    if (newAggr.checkPoint(x, y - 1))
                        checkCorrAndSet(prep, newAggr, x, y, x, y - 1);

                    if (newAggr.checkPoint(x - 1, y))
                        checkCorrAndSet(prep, newAggr, x, y, x - 1, y);

                    if (newAggr.checkPoint(x - 1, y + 1))
                        checkCorrAndSet(prep, newAggr, x, y, x - 1, y + 1);

                    if (newAggr.checkPoint(x - 1, y - 1))
                        checkCorrAndSet(prep, newAggr, x, y, x - 1, y - 1);

                }
            }

        prep.setAggrData(newAggr);

    }

    private void checkCorrAndSet(MVPrepContainer prep, ArrayData newAggr,
            int x1, int y1, int x2, int y2) {

        if (prep.getAggrData().getPoint(x2, y2) == 0)
            if (newAggr.getPoint(x2, y2) > 0) {
                if (prep.getCorrMV()[newAggr.getPoint(x2, y2) - 1]
                        .getCorrelation() < prep.getCorrMV()[prep.getAggrData()
                        .getPoint(x1, y1) - 1].getCorrelation())
                    newAggr.setPoint(x2, y2,
                            (prep.getAggrData().getPoint(x1, y1)));
            } else {
                newAggr.setPoint(x2, y2, (prep.getAggrData().getPoint(x1, y1)));
            }

    }
    
    
    /**
     * @param data
     * @param rd
     *
    private static void makeImage(int[][] data, Set<MapColor> scale, String title, int gridSize) {
        PictureFromArray pic = null;;
        if(gridSize == 0)
            pic = new PictureFromArray(data, scale);
        else
            pic = new PictureFromArray(data, scale, gridSize);
        
//        ImageFrame frame = new ImageFrame(pic.getImg(), title, pic.getWidth(),
//                pic.getHeight());
//
//        frame.displayImage();

        try {

            String folder = "data/output/";

            File file = new File(folder + title + ".png");
            ImageIO.write(pic.getImg(), "png", file);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    */
    
    /*
     * for tests 
     *
    public static void main(String[] args) {
        
        String fileName0 = "2011071401102800dBZ.ppi";
        String fileName1 = "2011071401202800dBZ.ppi";
        
        String folder = "/home/lwojtas/workspace/FileAnalizerOfRadarData/data/LEG_B/";
        
        int dbzThreshold = 5;
        double corrthreshold = 0.6;
        int gridsize = 20;
        RadarProduct rp0 = null, rp1 = null;
        
        
        
        DataSource a0 = DataFactory.newData(fileName0,
                folder, null);
        if (a0.isRadarData()) {
            rp0 = (RadarProduct) a0;
        }
        
        DataSource a1 = DataFactory.newData(fileName1,
                folder, null);
        if (a1.isRadarData()) {
            rp1 = (RadarProduct) a1;
        }
        
        List<MV> allVectors = new MVCollection(14).getAllVectors();
        MVFactory mvf = new MVFactory(rp0, rp1, dbzThreshold, corrthreshold, gridsize, allVectors);
        
//        makeImage(mvf.moveData(1).getData(), ColorScales.getRainbowScale(), "progn1", gridsize);
//        makeImage(mvf.moveData(2).getData(), ColorScales.getRainbowScale(), "progn2", gridsize);
//        makeImage(mvf.moveData(3).getData(), ColorScales.getRainbowScale(), "progn3", gridsize);
       
        
    }
     */
    
 }
