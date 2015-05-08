package local;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.IMats;


import java.net.InetAddress;

import static utils.Consts.*;

/**
 * Created by crised on 4/6/15.
 */
public class Feeder implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private Mat frame, mask, blur; //Feeder frameReady
    private BackgroundSubtractorMOG2 bS;
    private org.opencv.highgui.VideoCapture vCap;
    private Core cvCore;

    public Feeder() {
        this.frame = new Mat();
        this.mask = new Mat();
        this.blur = new Mat();
        this.bS = new BackgroundSubtractorMOG2();
        this.cvCore = new Core();
        this.vCap = new org.opencv.highgui.VideoCapture();
        this.vCap.open(IP_STREAM_ADDRESS);


    }

    @Override
    public void run() {
        LOG.info("Feeder Started");
        try {
            while (true) {
                Thread.sleep(FEEDER_DELAY); // 0 delay too fast in x220
                if (!InetAddress.getByName(IP_ADDRESS).isReachable(10_000)) {
                    LOG.error("Camera not reachable");
                    Thread.sleep(10_000);
                    continue;
                }
                if (!vCap.read(frame) || !vCap.isOpened()) {
                    LOG.error("Couldn't read Video Stream");
                    Thread.sleep(IP_RETRY_INTERVAL);
                    vCap = new org.opencv.highgui.VideoCapture();
                    vCap.open(IP_STREAM_ADDRESS);
                    continue;
                }
                if (!dayNight.isDay()) {
                    iMats = new IMats(frame, null);
                    continue;
                }
                Imgproc.blur(frame, blur, new Size(8.0, 8.0));
                bS.apply(blur, mask, -1);
                Imgproc.erode(mask, mask, new Mat());
                Imgproc.dilate(mask, mask, new Mat());
                if (dayNight.isDay() && !//npe
                        (cvCore.countNonZero(mask) > LOWER_BOUND_PIXELS
                                && cvCore.countNonZero(mask) < UPPER_BOUND_PIXELS)) continue;
                iMats = new IMats(frame, mask); //could post duplicates
            }
        } catch (InterruptedException e) {
            LOG.error("Thread Exception", e);
        } catch (Exception e) {
            LOG.error("Other Exception", e);
        }
    }


}