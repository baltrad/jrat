/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.hdf5;

import static pl.imgw.jrat.data.hdf5.OdimH5Constans.*;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pl.imgw.jrat.util.HdfTreeUtil;
import pl.imgw.jrat.util.LogsHandler;
import pl.imgw.jrat.util.MessageLogger;

import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5Group;

/**
 *
 *  /Class description/
 *
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class OdimCompo extends RadarProduct implements OdimH5File {

    private boolean v = false;
    protected int numberOfNodes;
    protected HashMap<String, String> nodes;

    public OdimCompo(boolean verbose) {
        this.v = verbose;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pl.imgw.jrat.data.hdf5.OdimH5File#initializeFromRoot(ncsa.hdf.object.
     * Group)
     */
    @Override
    public boolean initializeFromRoot(Group root) {

        Group what = null;
        Group where = null;
        Group how = null;

        List<Group> dataset = new ArrayList<Group>();
        List<?> memberList = root.getMemberList();
        Iterator<?> itr = memberList.iterator();
        while (itr.hasNext()) {
            H5Group group = ((H5Group) itr.next());
            // System.out.println(group.getName());
            if (group.getName().matches(WHAT))
                what = group;
            else if (group.getName().matches(WHERE))
                where = group;
            else if (group.getName().matches(HOW))
                how = group;
            else if (group.getName().startsWith(DATASET))
                dataset.add(group);
            
        }
        if (what.hasAttribute()) {
            setObject(H5_Wrapper.getHDF5StringValue(what, OBJECT, v));
            setVersion(H5_Wrapper.getHDF5StringValue(what, VERSION, v));
            try {
                setDate(H5_Wrapper.getHDF5StringValue(what, DATE, v),
                        H5_Wrapper.getHDF5StringValue(what, TIME, v));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setSource(H5_Wrapper.getHDF5StringValue(what, SOURCE, v));
        }

        if (where.hasAttribute()) {
            setXsize(H5_Wrapper.getHDF5LongValue(where, XSIZE, v));
            setYsize(H5_Wrapper.getHDF5LongValue(where, YSIZE, v));
            setXscale(H5_Wrapper.getHDF5DoubleValue(where, XSCALE, v));
            setYscale(H5_Wrapper.getHDF5DoubleValue(where, YSCALE, v));
            setLL_lon(H5_Wrapper.getHDF5DoubleValue(where, LL_LON, v));
            setLL_lat(H5_Wrapper.getHDF5DoubleValue(where, LL_LAT, v));
            setUL_lon(H5_Wrapper.getHDF5DoubleValue(where, UL_LON, v));
            setUL_lat(H5_Wrapper.getHDF5DoubleValue(where, UL_LAT, v));
            setUR_lon(H5_Wrapper.getHDF5DoubleValue(where, UR_LON, v));
            setUR_lat(H5_Wrapper.getHDF5DoubleValue(where, UR_LAT, v));
            setLR_lon(H5_Wrapper.getHDF5DoubleValue(where, LR_LON, v));
            setLR_lat(H5_Wrapper.getHDF5DoubleValue(where, LR_LAT, v));
        }
        if (how.hasAttribute()) {
            String[] node = H5_Wrapper.getHDF5StringValue(how, NODES, v).split(",");
            HashMap<String, String> nodes = new HashMap<String, String>();
            
            for(int i = 0; i < node.length; i++) {
                String key = node[i].substring(4, 6);
                String value = node[i].substring(6, 9);
                if(nodes.containsKey(key))
                    nodes.put(key, nodes.get(key) + "," + value);
                else
                    nodes.put(key, value);
            }
            setNodes(nodes);
            setNumberOfNodes(node.length);
//            setNodesNames(nodes.split(","));
        }
        OdimH5Dataset[] dsCollection = new OdimH5Dataset[dataset.size()];
        for (int i = 0; i < dataset.size(); i++) {
            List<Group> data = new ArrayList<Group>();
            OdimH5DatasetCompo psd = new OdimH5DatasetCompo();
            List<?> datasetlist = dataset.get(i).getMemberList();
            Iterator<?> dtsitr = datasetlist.iterator();
            psd.setDatasetname(dataset.get(i).getName());
            while (dtsitr.hasNext()) {
                H5Group group = ((H5Group) dtsitr.next());
                if (group.getName().contains(WHAT))
                    what = group;
//                else if (group.getName().matches(WHERE))
//                    where = group;
//                else if (group.getName().matches(HOW))
//                    how = group;
                else if (group.getName().startsWith(DATA)
                        && group.getName().length() > 4)
                    data.add(group);
            }
            if (what.hasAttribute()) {
                psd.setProduct(H5_Wrapper.getHDF5StringValue(what, PRODUCT, v));
                try {
                    psd.setStartdate(
                            H5_Wrapper.getHDF5StringValue(what, STARTDATE, v),
                            H5_Wrapper.getHDF5StringValue(what, STARTTIME, v));
                    psd.setEnddate(
                            H5_Wrapper.getHDF5StringValue(what, ENDDATE, v),
                            H5_Wrapper.getHDF5StringValue(what, ENDTIME, v));

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                psd.setQuantity(H5_Wrapper.getHDF5StringValue(what,
                        QUANTITY, v));
                psd.setGain(H5_Wrapper.getHDF5DoubleValue(what, GAIN, v));
                psd.setOffset(H5_Wrapper.getHDF5DoubleValue(what, OFFSET, v));
                psd.setNodata(H5_Wrapper.getHDF5DoubleValue(what, NODATA, v));
                psd.setUndetect(H5_Wrapper.getHDF5DoubleValue(what,
                        UNDETECT, v));
                
            }

            OdimH5Data[] psCollection = new OdimH5Data[data.size()];
            for (int j = 0; j < data.size(); j++) {
                List<Dataset> dts = new ArrayList<Dataset>();

                OdimH5Data ps = new OdimH5Data();
                List<?> dataList = data.get(j).getMemberList();
                Iterator<?> dataitr = dataList.iterator();
                ps.setDataName(data.get(j).getName());
                while (dataitr.hasNext()) {
                    HObject group = ((HObject) dataitr.next());
                    // System.out.print("j="+j + " " + group.getName() + " ");
                    if (group.getName().matches(DATA))
                        dts.add((Dataset) group);
                }

                long xsize = getXsize();
                long ysize = getYsize();

                ArrayData arrayData = new ArrayData((int) xsize, (int) ysize);
                try {
                    // System.out.println("type: " +
                    // dts.get(j).getDatatype());
                    arrayData.data = H5_Wrapper.getHDF5DoubleDataset(
                            dts.get(j), (int) xsize, (int) ysize, v);
                } catch (OutOfMemoryError e) {
                    LogsHandler.saveProgramLogs("OdimCompo", "out of memory "
                            + e.getMessage());
                    return false;
                } catch (Exception e) {
                    LogsHandler.saveProgramLogs("OdimCompo", e.getMessage());
                    return false;
                }
                ps.setArray(arrayData);
                psCollection[j] = ps;

            }
            psd.setData(psCollection);
            dsCollection[i] = psd;
        }
        setDataset(dsCollection);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.hdf5.OdimH5File#displayTree()
     */
    @Override
    public void displayTree() {
        
        String gp = HdfTreeUtil.makeGrantparent("\\");
        System.out.println(gp);
        String p1 = HdfTreeUtil.makeParent(gp.length() - 1, "what");
        System.out.println(p1);
        int space = p1.length() - 1;
        HdfTreeUtil.makeAttribe(space, OBJECT, object);
        HdfTreeUtil.makeAttribe(space, VERSION, version);
        HdfTreeUtil.makeAttribe(space, DATE, getDate());
        HdfTreeUtil.makeAttribe(space, TIME, getTime());
        HdfTreeUtil.makeAttribe(space, SOURCE, source);

        String p2 = HdfTreeUtil.makeParent(gp.length() - 1, "where");
        System.out.println(p2);
        space = p2.length() - 1;
        HdfTreeUtil.makeAttribe(space, PROJDEF, projdef);
        HdfTreeUtil.makeAttribe(space, XSIZE, xsize);
        HdfTreeUtil.makeAttribe(space, YSIZE, ysize);
        HdfTreeUtil.makeAttribe(space, XSCALE, xscale);
        HdfTreeUtil.makeAttribe(space, YSCALE, yscale);
        HdfTreeUtil.makeAttribe(space, LL_LON, LL_lon);
        HdfTreeUtil.makeAttribe(space, LL_LAT, LL_lat);
        HdfTreeUtil.makeAttribe(space, UL_LON, UL_lon);
        HdfTreeUtil.makeAttribe(space, UL_LAT, UL_lat);
        HdfTreeUtil.makeAttribe(space, UR_LON, UR_lon);
        HdfTreeUtil.makeAttribe(space, UR_LAT, UR_lat);
        HdfTreeUtil.makeAttribe(space, LR_LON, LR_lon);
        HdfTreeUtil.makeAttribe(space, LR_LAT, LR_lat);

//        System.out.println("datasetsize=" + datasetSize);
        // String.valueOf(this.datasetSize)));
        for (int i = 0; i < datasetSize; i++) {
            String pn = HdfTreeUtil.makeParent(gp.length() - 1, dataset[i].getDatasetname());
            System.out.println(pn);
            space = pn.length() - 1;

            dataset[i].displayTree(space);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.imgw.jrat.data.hdf5.OdimH5File#printGeneralInfo(boolean)
     */
    @Override
    public void displayGeneralObjectInfo(boolean verbose) {
        String msg = "Odim composite loaded\n";
        msg += "Data source:\t" + getSource() + "\n";
        msg += "Data time:\t" + getFullDate() +"\n";
        msg += "No. of nodes:\t" + getNumberOfNodes() + "\n";
        msg += "Nodes names:\n";
        for (Map.Entry<String, String> entry : getNodes().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            msg += "\t" + key + ": " + value + "\n";
        }
        MessageLogger.showMessage(msg, verbose);
    }
    
    /**
     * @return the nodes
     */
    public HashMap<String, String> getNodes() {
        return nodes;
    }

    /**
     * @param nodes the nodes to set
     */
    public void setNodes(HashMap<String, String> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return the numberOfNodes
     */
    public int getNumberOfNodes() {
        return numberOfNodes;
    }
    
    public void setNumberOfNodes(int nodes) {
        this.numberOfNodes = nodes;
    }

    public static void main(String[] args) {
        boolean verbose = true;
        String fileName = "/home/vrolok/poligon/T_PAAH21_C_EUOC_20110930061500.hdf";
        
        File f = new File(fileName);
        H5File file = H5_Wrapper.openHDF5File(f.getAbsolutePath(),
                verbose);
        Group root = H5_Wrapper.getHDF5RootGroup(file, verbose);
        OdimH5File odim = new OdimCompo(verbose);
        odim.initializeFromRoot(root);
//        odim.displayTree();
    }
    

}
