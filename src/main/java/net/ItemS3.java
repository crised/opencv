package net;


/**
 * Created by crised on 4/13/15.
 */
public class ItemS3 {

    private byte[] data;
    private String fileName;

    public ItemS3(byte[] data, String id) {
        this.data = data;
        this.fileName = id + ".jpg";
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }
}
