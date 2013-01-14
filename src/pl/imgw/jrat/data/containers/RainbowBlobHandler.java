/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.containers;

import static pl.imgw.jrat.tools.out.Logging.ERROR;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.Inflater;

import org.apache.commons.io.IOUtils;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import pl.imgw.jrat.data.parsers.DataBufferContainer;
import pl.imgw.jrat.tools.out.LogHandler;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;

/**
 * 
 * /Class description/
 * 
 * 
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 * 
 */
public class RainbowBlobHandler {

    private static final String blobid = "blobid";
    private static final String size = "size";
    private static final String compression = "compression";
    private final char[] end = "<!-- END XML -->".toCharArray();
    private final char[] tag = "<BLOB".toCharArray();
    
    protected static final Byte BITS_IN_BYTE = Byte.SIZE;
    protected static final Byte FLAG_ZERO = 0;
    
    private int offset = 0;
    
    private byte[] fileBuff;
    private Document doc;
    
    /**
     * 
     * Function reads Rainbow data section from Rainbow file and puts it into an
     * DataBufferContainer
     * 
     * @param fileBuff
     * @param verbose
     * @return hash map containing blob number and data buffer container
     */
    public HashMap<Integer, RainbowBlobContainer> getAllRainbowDataBlobs(FileInputStream fis) {

//        HashMap<Integer, BlobContainer> blobs = new HashMap<Integer, BlobContainer>();
        byte[] byteBuff;
        try {
            byteBuff = IOUtils.toByteArray(fis);
            if(!findXMLEnd(byteBuff))
                return null;
            
            doc = parseXML(byteBuff, 0, offset);
//            Node element = doc.getDocumentElement();
//            System.out.println(element.getLocalName());
            
            HashMap<Integer, RainbowBlobContainer> blobs =  setBlobs(byteBuff);
            return blobs;
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        
        return null;
    }

    /**
     * @return the doc
     */
    public Document getDoc() {
        return doc;
    }

    private HashMap<Integer, RainbowBlobContainer> setBlobs(byte[] b) {
        
        HashMap<Integer, RainbowBlobContainer> blobs = new HashMap<Integer, RainbowBlobContainer>();
        
        boolean match = false;
        while(offset < b.length) {
            String xmlHeader = "";
            
            //looking for '<BLOB' tag
            for(int c = 0; c < tag.length; c++) {
                if((char) b[offset + c] == tag[c]) {
//                    System.out.println((char) b[offset + c]);
                    match = true;
                } else {
                    match = false;
                    break;
                }
            }
            if(match) {
                offset += tag.length;
                //looking for closing tag
                while((char) b[++offset] != '>') {
                    xmlHeader += (char) b[offset];
                }
                RainbowBlobHeader header = new RainbowBlobHeader(xmlHeader);
//                if(header.isValid())
//                    System.out.println("OK");
                
                int blobsize = header.getSize();
                offset += 2; //skip closing sign and new line
//                System.out.println((char)b[offset]);
                if(header.getCompression().matches("qt")) {
                    byte[] uncopressedSize = new byte[4];
                    for(int i = 0; i < 4; i++)
                        uncopressedSize[i] = b[offset++];
                    header.setSize(byteArray2Int(uncopressedSize));
                    blobsize -= 4; //skip 4 byte representing size
                }
                byte[] blobdata = Arrays.copyOfRange(b, offset, offset + blobsize);
//                System.out.println("wczytanych " + blobdata.length);
                offset += blobsize;
                RainbowBlobContainer bc = new RainbowBlobContainer();
                if(header.getCompression().matches("qt")) {
                    bc.setCompressedArray(blobdata);
                } else {
                    bc.setDecompressedArray(blobdata);
                }
                bc.setDecompressedSize(header.getSize());
                blobs.put(header.getBlobid(), bc);
            } else {
                offset++;
            }
        }
        
        return blobs;
    }
    
    private int findXMLTag(byte[] b, int offset) {
        boolean match = false;
        String xmlHeader = "";
        for(int c = 0; c < tag.length; c++) {
            if((char) b[offset + c] == tag[c]) {
                match = true;
            } else {
                match = false;
                break;
            }
        }
        
        return 0;
    }
    
    private boolean findXMLEnd(byte[] b) {
        
        
//        System.out.println("dlugosc  " + end.length);
        boolean match;
        for (int i = 0; i < b.length; i++) {
            match = false;
            for (int c = 0; c < end.length; c++) {
                if ((char) b[i + c] == end[c]) {
                    match = true;
                } else {
                    match = false;
                    break;
                }
            }
            if(match) {
                offset = i;
                return true;
            }
            
        }
        
        return false;
    }
    
    
    /**
     * Method inflates compressed RAINBOW data section with ZLib algorithm
     * 
     * @param input_buf
     *            Compressed buffer
     * @param cols
     *            Output array width
     * @param rows
     *            Output array height
     * @param verbose
     *            Verbose mode toggle
     * @return Inflated data section as a byte array
     */
    public byte[][] inflateDataSection(DataBufferContainer dbc,
            int rays, int bins, int depth) {
        byte[] input_buf = dbc.getDataBuffer();
        byte[][] output_buf = new byte[rays][bins];
        int len = rays * bins;
        if (depth == 1) {
            len = rays * bins / 8 + 8;
        }
        if (depth == 2) {
            len = rays * bins / 4 + 4;
        }
        byte[] byte_buf = new byte[len];

        // Inflate input stream
        ZStream defStream = new ZStream();
        defStream.next_in = input_buf;
        defStream.next_in_index = 0;
        defStream.next_out = byte_buf;
        defStream.next_out_index = 0;
        int err = defStream.inflateInit();
//        int a = 0;
        while (defStream.total_out < len
                && defStream.total_in < input_buf.length) {
            defStream.avail_in = defStream.avail_out = 1;
            err = defStream.inflate(JZlib.Z_NO_FLUSH);
            if (err == JZlib.Z_STREAM_END)
                break;
//            a++;
            if(!isOK(defStream, err, "Inflation error")) {
                return null;
            }
        }
        // System.out.println(a);
        err = defStream.inflateEnd();
        if(!isOK(defStream, err, "Inflation end error")) {
            return null;
        }

     
        int count = 0;
        for (int x = 0; x < rays; x++) {
            for (int y = 0; y < bins; y++) {
                count = y * rays + x;
                if (depth == 8) {
                    output_buf[x][y] = byte_buf[count];
                }
                else if (depth == 1) {
                    if (isBitSet(byte_buf[count / 8], count % 8))
                        output_buf[x][y] = 1;
                } else if (depth == 2) {
                    if (!areFlagsOK(byte_buf, (short)2, count))
                        output_buf[x][y] = 1;
//                    if (isBitSet(byte_buf[count / 4], 2 * (count % 4) + 1))
//                        output_buf[x][y] = 1;
                }
//                count++;
            }
        }
        return output_buf;
    }

    /**
     * Decompress data; returned data is allocated in this method.
     *
     * 
     * 
     * 
     * @param compressedData - data to be decompressed
     * @param size - count of bytes that should be in decompressed data
     * @return decompressed data
     */
    public byte[][] decompress(DataBufferContainer dbc, int rays, int bins) {
        byte[] compressedData = dbc.getDataBuffer();
        byte[][] output_buf = new byte[rays][bins];
        int size = dbc.getDataBufferLength();
        Inflater inflater;
        byte[] decompressedData;

        System.out.println("przed: " + compressedData.length + " po " + size);
        
        decompressedData = new byte[rays*bins];
        inflater = new Inflater();

        try {
            while (inflater.needsInput()) {
                inflater.setInput(compressedData);
                inflater.inflate(decompressedData, 0, size);
            }
            inflater.end();
        } catch (Exception e) {
//            e.printStackTrace();
            LogHandler.getLogs().displayMsg("Error while decompressing data",
                    ERROR);
            LogHandler.getLogs().saveErrorLogs(
                    RainbowBlobHandler.class.getName(),
                    "decompress: exception!!!\n" + e.getMessage());
            return null;
        }
        int count = 0;
        for (int x = 0; x < rays; x++) {
            for (int y = 0; y < bins; y++) {
                count = y * rays + x;
                    output_buf[x][y] = decompressedData[count];
            }
        }
        
        return output_buf;
    }
    
    private Boolean isBitSet(byte b, int bit) {
        return (b & (1 << (8 - bit ))) != 0;
    }

    public short firstAzimuth(byte[] input_buf, int len) {
        
        byte[] byte_buf = new byte[2 * len];

        // Inflate input stream
        ZStream defStream = new ZStream();
        defStream.next_in = input_buf;
        defStream.next_in_index = 0;
        defStream.next_out = byte_buf;
        defStream.next_out_index = 0;
        int err = defStream.inflateInit();
//        int a = 0;
        while (defStream.total_out < 2*len
                && defStream.total_in < input_buf.length) {
            defStream.avail_in = defStream.avail_out = 1;
            err = defStream.inflate(JZlib.Z_NO_FLUSH);
            if (err == JZlib.Z_STREAM_END)
                break;
//            a++;
            if(!isOK(defStream, err, "Inflation error")) {
                return 0;
            }
        }
        // System.out.println(a);
        err = defStream.inflateEnd();
        if(!isOK(defStream, err, "Inflation end error")) {
            return 0;
        }

        int azimuth = 2 * Short.MAX_VALUE;
        short firstAzimuth = 0;
        int a = 0;
        for (short i = 0; i < len; i++) {
            a = unsignedShortToInt(new byte[] { byte_buf[i * 2],
                    byte_buf[i * 2 + 1] });
            if (a < azimuth) {
                azimuth = a;
                firstAzimuth = i;
            }
        }
        
        return firstAzimuth;
        
    }
    
    /**
     * Check whether flag bits for given byte of 2D data are OK (e.g. all set to zero).
     * 
     * 
     * 
     * 
     * 
     * @param flagData - array with all flag bits
     * @param flagDepth - amount of flag bits that are relative to one data byte
     * @param byteNumber - number of byte in data2D collection
     * @return logical value: true if all (flagDepth) flags for given byte are OK (e.g. set to zero), false - if not
     */
    protected Boolean areFlagsOK(byte[] flagData, Short flagDepth, Integer byteNumber) {

        Integer bitNumber, startByte, startBitInByte, flagsBitsByteArrayLength, bitsUnusedFromEndOfArray, temp;
        byte[] flagsBitsByteArray;

        // number of starting bit in flagData
        bitNumber = byteNumber * flagDepth;
        // number of starting byte in flagData where the starting bit is located
        startByte = bitNumber / BITS_IN_BYTE;
        // number of bit in the starting byte in flagData where the starting bit is located
        startBitInByte = bitNumber % BITS_IN_BYTE;

        // reading as many bytes as it is needed to represent flag set as byte array
        flagsBitsByteArrayLength = ((startBitInByte + flagDepth - 1) / BITS_IN_BYTE) + 1;
        flagsBitsByteArray = new byte[flagsBitsByteArrayLength];
        for (int i = 0; i < flagsBitsByteArrayLength; i++)
            flagsBitsByteArray[i] = flagData[i + startByte];

        // how many bits are unused from the last bit of flag set to the bit at the end of byte array
        bitsUnusedFromEndOfArray = (flagsBitsByteArrayLength * BITS_IN_BYTE) - startBitInByte - flagDepth;

        // "clearing" first and last bytes from bits that do not represent flag set
        flagsBitsByteArray[0] <<= startBitInByte;
        temp = (flagsBitsByteArray[flagsBitsByteArrayLength - 1]) & 0xFF;
        if (flagsBitsByteArrayLength > 1)
            temp >>= bitsUnusedFromEndOfArray;
        else // flagsBitsByteArrayLength == 1
            temp >>= BITS_IN_BYTE - flagDepth;
        flagsBitsByteArray[flagsBitsByteArrayLength - 1] = temp.byteValue();

        // checking if all flags are ok
//FIXME FOR DEBUG ONLY - DELETE BELOW
        for (int i = 0; i < flagsBitsByteArrayLength; i++)
            if (flagsBitsByteArray[i] != FLAG_ZERO)
                // if one part of analyzed flag set is not ok
                //TODO might be that it is guilty only when the "correct" flag is zero flag
                return false;
        // if all bits in flag set are ok
        return true;
    }
    
    /**
     * Method checks deflation errors
     * 
     * @param z
     *            Input ZStream
     * @param err
     *            Error code
     * @param msg
     *            Message
     * @param verbose
     *            Verbose mode toggle
     */
    private boolean isOK(ZStream z, int err, String msg) {
        if (err != JZlib.Z_OK) {
            if (z.msg != null) {
                msg += ": " + z.msg;
            }
            LogHandler.getLogs().displayMsg("Decompresing error: " + msg, ERROR);
            return false;
        }
        return true;
    }

    private HashMap<String, Integer> getHeader(String xmlHeader) {
        HashMap<String, Integer> header = new HashMap<String, Integer>();

        header.put(blobid, getAttribute(blobid, xmlHeader));
        header.put(size, getAttribute(size, xmlHeader));
        header.put(compression, getAttribute(compression, xmlHeader));

        return header;
    }

    /**
     * Method converts 4 byte array value into integer value.
     * 
     * @param b
     *            Byte array
     * @return Integer
     */
    private int byteArray2Int(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0xFF) << shift;
        }
        return value;
    }

    /**
     * Converts a two byte array to an integer
     * @param b a byte array of length 2
     * @return an int representing the unsigned short
     */
    private int unsignedShortToInt(byte[] b) {
        int i = 0;
        i |= b[0] & 0xFF;
        i <<= 8;
        i |= b[1] & 0xFF;
        return i;
    }
    
    /**
     * Method parses RAINBOW metadata buffer
     * 
     * @param hdrBuff
     *            RAINBOW metadata buffer
     * @param verbose
     *            Verbose mode toggle
     * @return XML document object
     */
    private Document parseXML(byte[] fileBuff, int offset, int length) {

        Document doc = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(fileBuff, offset, length);
            DOMParser parser = new DOMParser();
            InputSource is = new InputSource(bis);
            parser.parse(is);
            doc = parser.getDocument();
            
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        return doc;
    }
    
    /**
     * 
     * Helping method
     * 
     * @param word
     * @param sentance
     * @return
     */
    private int getAttribute(String word, String sentance) {

        int start = 0, stop = 0;

        start = sentance.indexOf(word) + word.length() + 2;
        stop = sentance.indexOf("\"", start);
        int value = 0;
        try {
            value = Integer.valueOf(sentance.substring(start, stop));
        } catch (NumberFormatException e) {
            if (sentance.substring(start, stop).equals("qt"))
                return 1;
            else
                return 0;
        }
        return value;

    }

}
