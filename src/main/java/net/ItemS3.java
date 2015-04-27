package net;


import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Created by crised on 4/13/15.
 */
public class ItemS3 {

    private long timeStamp;
    private byte[] data;
    private String fileName;

    public ItemS3(byte[] data, String kind) {
        this.timeStamp = System.currentTimeMillis();
        this.data = data;
        Format formatter = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy_SX");
        this.fileName = formatter.format(timeStamp) + kind + ".jpg";

    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public byte[] getData() {
        return data;
    }


    public String getFileName() {
        return fileName;
    }
}
