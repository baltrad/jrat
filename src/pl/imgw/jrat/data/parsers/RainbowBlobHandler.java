/**
 * (C) 2011 INSTITUT OF METEOROLOGY AND WATER MANAGEMENT
 */
package pl.imgw.jrat.data.parsers;

import static pl.imgw.jrat.tools.out.Logging.*;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.Inflater;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

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
    
    protected static final Byte BITS_IN_BYTE = Byte.SIZE;
    protected static final Byte FLAG_ZERO = 0;
    
    private byte[] fileBuff;
    
    /**
     * @param file_buf
     */
    public RainbowBlobHandler(byte[] file_buf) {
        this.fileBuff = file_buf;
    }

    /**
     * 
     * Function reads Rainbow data section from Rainbow file and puts it into an
     * DataBufferContainer
     * 
     * @param fileBuff
     * @param verbose
     * @return hash map containing blob number and data buffer container
     */
    public HashMap<Integer, DataBufferContainer> getAllRainbowDataBlobs() {

        HashMap<Integer, DataBufferContainer> blobs = new HashMap<Integer, DataBufferContainer>();

        // Data section tags
        final String START_BIN = "<blob";
        final String END_BIN = "</blob>";
        // Tag buffers
        byte[] start_bin_buf = new byte[6];
        byte[] end_bin_buf = new byte[8];
        // Data buffer
        byte[] data_buf = null;
        // Tag strings
        String start_bin_seq = "";
        String end_bin_seq = "";
        // Current offset
        int offset = 0;
        // Data section markers
        int start_bin = 0;
        int end_bin = 0;
        // Seek for data section

        int blobsection = 0;
        
        int currentBlob = 0;
        HashMap<String, Integer> header = null;

        int current = -1;
        while (offset < fileBuff.length) {
            String xmlHeader = "";
            try {
                while (offset < fileBuff.length) {
                    // Data section start

                    start_bin_buf[5] = fileBuff[offset];
                    for (int i = 1; i <= 5; i++) {
                        start_bin_buf[i - 1] = start_bin_buf[i];
                    }
                    for (int i = 0; i < 5; i++) {
                        start_bin_seq += (char) start_bin_buf[i];
                    }
                    if (start_bin_seq.toLowerCase().matches(START_BIN)) {
                        if(blobsection == 0)
                            blobsection = offset - START_BIN.length();
                        while ((char) fileBuff[offset] != '>') {
                            xmlHeader += (char) fileBuff[offset];
                            offset++;
                        }
                        start_bin = offset;
                        current++;
                    }

                    // Data section end
                    end_bin_buf[7] = fileBuff[offset];
                    for (int i = 1; i <= 7; i++) {
                        end_bin_buf[i - 1] = end_bin_buf[i];
                    }
                    for (int i = 0; i < 7; i++) {
                        end_bin_seq += (char) end_bin_buf[i];
                    }
                    if (end_bin_seq.toLowerCase().matches(END_BIN) && current == currentBlob) {
                        end_bin = offset;
                        break;
                    }
                    start_bin_seq = "";
                    end_bin_seq = "";
                    offset++;
                }

                if (xmlHeader.isEmpty())
                    continue;

                header = getHeader(xmlHeader);
                xmlHeader = "";
                int buffLen = 0;
                
                if (header.get(compression) == 1) {
                    // Read 4 bytes representing data length
                    // only when compression is set to "qt"
                    byte[] data_byte = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        data_byte[i] = fileBuff[start_bin + i + 2];
                    }
                    buffLen = byteArray2Int(data_byte);
                    start_bin += 6;
                    end_bin -= 6;

                } else {
                    buffLen = header.get(size);
                    start_bin += 3;
                    end_bin -= 8;
                }
                // Read data into data array
                int bin_count = 0;
                data_buf = new byte[end_bin - start_bin];
                for (int i = start_bin; i < end_bin; i++) {
                    data_buf[bin_count] = fileBuff[i];
                    bin_count++;
                }

                DataBufferContainer dbc = new DataBufferContainer();
                dbc.setDataBuffer(data_buf);
                dbc.setDataBufferLength(buffLen);
                dbc.setCompression(header.get(compression));

                blobs.put(header.get(blobid), dbc);
                currentBlob++;

            } catch (Exception e) {
                LogHandler.getLogs().displayMsg("Error while reading RAINBOW data section from BLOB "
                                + header.get(blobid), ERROR);
                LogHandler.getLogs().saveErrorLogs(this.getClass().getName(), "Error while reading RAINBOW data section from BLOB "
                        + header.get(blobid));
            }
        }
        
        fileBuff = Arrays.copyOfRange(fileBuff, 0, blobsection);
        return blobs;
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
     * Method converts byte array value into integer value.
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
     * Method parses RAINBOW metadata buffer
     * 
     * @param hdrBuff
     *            RAINBOW metadata buffer
     * @param verbose
     *            Verbose mode toggle
     * @return XML document object
     */
    public Document parseXML() {

        Document doc = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(fileBuff);
            DOMParser parser = new DOMParser();
            InputSource is = new InputSource(bis);
            parser.parse(is);
            doc = parser.getDocument();
            
        } catch (Exception e) {
            
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
