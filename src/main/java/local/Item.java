package local;


import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Created by crised on 4/13/15.
 */
public class Item {

    private long timeStamp;
    private byte[] data;
    private long capturePixelScore;
    private String fileName;

    public Item(byte[] data, long capturePixelScore) {
        this.timeStamp = System.currentTimeMillis();
        this.data = data;
        this.capturePixelScore = capturePixelScore;
        Format formatter = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy_SX");
        this.fileName = formatter.format(timeStamp) + ".jpg";

    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public byte[] getData() {
        return data;
    }

    public long getCapturePixelScore() {
        return capturePixelScore;
    }

    public String getFileName() {
        return fileName;
    }
}
