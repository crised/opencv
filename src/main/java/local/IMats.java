package local;

import org.opencv.core.Mat;

/**
 * Created by crised on 4/28/15.
 * Immutable Mats
 */
public final class IMats {

    private final Mat frame, mask; //final allow variable to instanstiate only once

    public IMats(Mat frame, Mat mask) {
        this.frame = frame;
        this.mask = mask;
    }

    public Mat getFrame() {
        return frame;
    }

    public Mat getMask() {
        return mask;
    }
}
