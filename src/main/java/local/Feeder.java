package local;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.ArrayList;
import java.util.List;

import static utils.Consts.*;

/**
 * Created by crised on 4/6/15.
 */
public class Feeder implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private Mat frame, mask, blur; //Feeder frameReady
    private BackgroundSubtractorMOG2 bS;
    private IMats iMats;
    private org.opencv.highgui.VideoCapture vCap;

    public Feeder() {
        this.frame = new Mat();
        this.mask = new Mat();
        this.blur = new Mat();
        // this.bS = new BackgroundSubtractorMOG2(3, 16, true);
        this.bS = new BackgroundSubtractorMOG2();
    }

    @Override
    public void run() {
        LOG.info("Feeder Started");
        try {
            setVideoCapture();
            Thread.sleep(IP_RETRY_INTERVAL);
            //LOG.info("Frame Width " + vCap.get(Highgui.CV_CAP_PROP_FRAME_WIDTH));
            while (true) {
                Thread.sleep(FEEDER_FRAME_DELAY); // 0 delay too fast in x220
                if (!vCap.read(frame)) {
                    LOG.error("Couldn't read Video Stream");
                    setVideoCapture();
                    Thread.sleep(IP_RETRY_INTERVAL);
                    continue;
                }
                Imgproc.blur(frame, blur, new Size(8.0, 8.0));
                bS.apply(blur, mask, -1);
                Imgproc.erode(mask, mask, new Mat());
                Imgproc.dilate(mask, mask, new Mat());
                iMats = new IMats(frame, mask);

            }
        } catch (InterruptedException e) {
            LOG.error("Thread Exception", e);
        } catch (Exception e) {
            LOG.error("Other Exception", e);
        }
    }


    private void setVideoCapture() {
        this.vCap = new org.opencv.highgui.VideoCapture();
        this.vCap.open(IP_STREAM_ADDRESS);
        if (!vCap.isOpened()) LOG.error("Couldn't open Video Stream");
    }

    public IMats getiMats() {
        return iMats;
    }
}