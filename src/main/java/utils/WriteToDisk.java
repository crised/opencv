package utils;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static utils.Consts.CL_TELEMATIC;

/**
 * Created by crised on 4/30/15.
 */
public class WriteToDisk {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);
    private long index;

    public void writeToDisk(String prepend, Mat mat) {

        try {
            index++;
            Highgui.imwrite("img/" + index + prepend + ".jpg", mat);

        } catch (Exception e) {
            LOG.error("Write Exception", e);
        }

    }
}
