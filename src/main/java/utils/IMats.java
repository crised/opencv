package utils;

import org.opencv.core.Mat;

/**
 * Created by crised on 4/28/15.
 * Immutable Mats, final class -> can't be extended, final member -> can instanstiated only once - reference does not change.
 */
public final class IMats {

    private final Mat frame, mask; //final allow variable to instantiate only once
    private final long timestamp;

    public IMats(Mat frame, Mat mask) {
        this.frame = frame;
        this.mask = mask;
        this.timestamp = System.currentTimeMillis();
    }

    public Mat getFrame() {
        return frame;
    }

    public Mat getMask() {
        return mask;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
