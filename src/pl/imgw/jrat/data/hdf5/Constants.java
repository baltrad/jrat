/*
 * OdimH5 :: Converter software for OPERA Data Information Model
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.jrat.data.hdf5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5File;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pl.imgw.jrat.util.MessageLogger;

/**
 * Class encapsulating HDF5 processing methods.
 * 
 * @author szewczenko
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 *
 */
public class Constants {

    // General constants
    public static final String FILE_NAME_EXTENSION = ".hdf";
    public static final String FILE_NAME_EXTENSION1 = ".h5";
    
    // Inside file constants
    public static final String XML_ATTR = "attribute";

    public static final String H5_ROOT = "/";
    public static final String H5_GROUP = "group";
    public static final String H5_DATASET = "dataset";
    public static final String H5_OBJECT_NAME = "name";
    public static final String H5_OBJECT_CLASS = "class";
    public static final String H5_GZIP_LEVEL = "gzip_level";
    public static final String H5_DATA_CHUNK = "chunk";
    public static final String H5_DIMENSIONS = "dimensions";

    // XML settings
    public static final String XML_VERSION = "1.0";
    public static final String XML_ENCODING = "UTF-8";

    public static final String H5_CLASS = "CLASS";
    public static final String H5_IM_VER = "IMAGE_VERSION";

}
