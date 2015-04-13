package local;

import com.google.api.client.util.ExponentialBackOff;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static utils.Consts.*;

/**
 * Created by crised on 4/6/15.
 */
public class Local {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private Mat frame, blur, mask;
    private Core cvCore;
    private long captureDelay, capturePixelScore;
    private ExponentialBackOff backOff;
    private BackgroundSubtractorMOG2 bS;


    public Local() {

        this.frame = new Mat();
        this.blur = new Mat();
        this.mask = new Mat();
        this.cvCore = new Core();
        this.bS = new BackgroundSubtractorMOG2();

        ExponentialBackOff.Builder builder = new ExponentialBackOff.Builder();
        builder.setInitialIntervalMillis(EXPONENTIAL_INIT_INTERVAL);
        builder.setMultiplier(EXPONENTIAL_MULTIPLIER);
        builder.setRandomizationFactor(EXPONENTIAL_RANDOMIZATION);
        builder.setMaxElapsedTimeMillis(EXPONENTIAL_MAX_ELAPSED_TIME);
        builder.setMaxIntervalMillis(EXPONENTIAL_MAX_INTERVAL_MILLIS);

        this.backOff = builder.build();

    }

    public void capture() {

        VideoCapture vCap = new VideoCapture();
        vCap.open(IP_STREAM_ADDRESS);
        // vCap.open(0); //usb camera
        if (!vCap.isOpened()) LOG.error("Couldn't open Video Stream");
        LOG.info("Frame Width " + vCap.get(Highgui.CV_CAP_PROP_FRAME_WIDTH));
        LOG.info("Frame Height " + vCap.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT));

        while (true) {
            try {

                Thread.sleep(REFRESH_RATE_DELAY);
                if (!vCap.read(frame)) {
                    LOG.error("Couldn't read Video Stream");
                    Thread.sleep(5000);
                }

                Imgproc.blur(frame, blur, new Size(3.0, 3.0));
                bS.apply(blur, mask, -1);
                capturePixelScore = cvCore.countNonZero(mask);

                if (capturePixelScore > CAPTURE_PIXELS_THRESHOLD) {
                    Format formatter = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy_S_X");
                    String timestamp = formatter.format(new Date());
                    Highgui.imwrite("img/" + timestamp + ".jpg", frame);
                    Highgui.imwrite("img/" + timestamp + "m.jpg", mask);

                    calculateDelay();
                    LOG.info(timestamp + ": " + String.valueOf(capturePixelScore) + "Delay: " + captureDelay);
                    Thread.sleep(captureDelay);
                }

            } catch (InterruptedException e) {
                LOG.error("Thread Exception");
            } catch (Exception e) {
                LOG.error("Other Exception");
            }

        }
    }

    private void calculateDelay() throws Exception {


        //2 conditions to reset the timer.
        //1st Contition: Too much time has passed.
        if (backOff.getElapsedTimeMillis() >= TIME_BETWEEN_CAMERA_EVENTS) {
            LOG.info("Camera event has passed, resetting timer.");
            backOff.reset();
        }

        if (backOff.nextBackOffMillis() == ExponentialBackOff.STOP) {
            LOG.info("Max Interval has been reached, resetting.");
            backOff.reset();
        }

        this.captureDelay = backOff.nextBackOffMillis();


    }


    public void init() {
        System.out.println("Welcome to OpenCV " + Core.VERSION + "Lib Name: " + Core.NATIVE_LIBRARY_NAME);
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());
    }
}
