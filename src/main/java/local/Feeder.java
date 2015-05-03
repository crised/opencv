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
    private IMats iMats;
    private org.opencv.highgui.VideoCapture vCap;

    public Feeder(IMats iMats) {
        this.frame = new Mat();
        this.mask = new Mat();
        this.blur = new Mat();
        this.bS = new BackgroundSubtractorMOG2();
        this.iMats = iMats;
    }

    @Override
    public void run() {
        LOG.info("Feeder Started");
        try {
            setVideoCapture();
            Thread.sleep(IP_RETRY_INTERVAL);
            //LOG.info("Frame Width " + vCap.get(Highgui.CV_CAP_PROP_FRAME_WIDTH));
            while (true) {
                Thread.sleep(FEEDER_DELAY); // 0 delay too fast in x220
                if (!InetAddress.getByName(IP_ADDRESS).isReachable(1000)) {
                    LOG.error("Camera not reachable");
                    Thread.sleep(5000);
                    continue;
                }
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
                this.iMats = new IMats(frame, mask);

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

}