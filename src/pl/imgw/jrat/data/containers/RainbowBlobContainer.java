/*
 * OdimH5 :: Converter software for OPERA Data Information Model
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.jrat.data.containers;

import static pl.imgw.jrat.tools.out.Logging.ERROR;
import pl.imgw.jrat.tools.out.LogHandler;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZStream;

/**
 * Class implementing data container functionality.
 *
 * @author <a href="mailto:lukasz.wojtas@imgw.pl">Lukasz Wojtas</a>
 *
 */
public class RainbowBlobContainer {

    private int decompressedSize = 0;
    // Data buffer
    private byte[] compressed;
    private byte[] decompressed;
    // Number of bits used to describe one pixel
//    private int depth = 0;
    private int depth = 0;
    
    /**
     * @return the decompressed, null if decompression failed
     */
    public byte[] getDecompressed() {
        if(decompressed == null);
            if(decompress())
        return decompressed;
            return null;
    }
    
    /**
     * 
     */
    private boolean decompress() {
        if(decompressed != null)
            return true;
        if (compressed == null || compressed.length == 0)
            return false;

        decompressed = new byte[decompressedSize];

        ZStream defStream = new ZStream();
        defStream.next_in = compressed;
        defStream.next_in_index = 0;
        defStream.next_out = decompressed;
        defStream.next_out_index = 0;
        int err = defStream.inflateInit();
        // int a = 0;
        while (defStream.total_out < decompressedSize
                && defStream.total_in < compressed.length) {
            defStream.avail_in = defStream.avail_out = 1;
            err = defStream.inflate(JZlib.Z_NO_FLUSH);
            if (err == JZlib.Z_STREAM_END)
                break;
            // a++;
            if (!isOK(defStream, err, "Inflation error")) {
                return false;
            }
        }
        // System.out.println(a);
        err = defStream.inflateEnd();
        if (!isOK(defStream, err, "Inflation end error")) {
            return false;
        }
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
    
    /**
     * @param size the buffLen to set
     */
    public void setDecompressedSize(int size) {
        this.decompressedSize = size;
    }
    /**
     * @param compressed the compressed to set
     */
    public void setCompressedArray(byte[] compressed) {
        this.compressed = compressed;
    }
    /**
     * @param compression the compression to set
     */
    public void setDepth(int compression) {
        this.depth = compression;
    }
    
    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }
    /**
     * @param blobdata
     */
    public void setDecompressedArray(byte[] decompressed) {
        this.decompressed = decompressed;
        
    }

}
