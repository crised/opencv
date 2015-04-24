package local;

import com.google.api.client.http.HttpIOExceptionHandler;
import com.google.api.client.util.ExponentialBackOff;
import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import org.opencv.core.*;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.print.attribute.HashPrintJobAttributeSet;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.Consts.*;

/**
 * Created by crised on 4/6/15.
 */
public class Producer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private final LinkedBlockingQueue queue;
    private Mat frame, blur, mask, winnerFrame, lastMask, abbsDiff;
    private Core cvCore;
    private long timeSlot, capturePixelScore, winnerFrameScore, lastPassed;
    private ExponentialBackOff backOff;
    private BackgroundSubtractorMOG2 bS;
    private NavigableMap<Long, Mat> candidatesMap;
    private HOGDescriptor Hog;


    public Producer(LinkedBlockingQueue queue) {
        this.queue = queue;
        this.frame = new Mat();
        this.blur = new Mat();
        this.mask = new Mat();
        this.cvCore = new Core();
        this.lastMask = new Mat();
        this.abbsDiff = new Mat();
        this.bS = new BackgroundSubtractorMOG2();
        constructBackOff();
        this.candidatesMap = new TreeMap<>();
        this.Hog = new HOGDescriptor();
        this.Hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());

    }

    @Override
    public void run() {
        try {

            LOG.info("Producer started");
            org.opencv.highgui.VideoCapture vCap = new org.opencv.highgui.VideoCapture();
            vCap.open(IP_STREAM_ADDRESS);
            // vCap.open(0); //usb camera
            if (!vCap.isOpened()) LOG.error("Couldn't open Video Stream");
            //LOG.info("Frame Width " + vCap.get(Highgui.CV_CAP_PROP_FRAME_WIDTH));
            //LOG.info("Frame Height " + vCap.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT));

            //initial conditions
            this.lastPassed = System.currentTimeMillis();
            getNextTimeSlot(false);

            while (true) {

                Thread.sleep(REFRESH_RATE_DELAY);
                if (!vCap.read(frame)) {
                    LOG.error("Couldn't read Video Stream");
                    Thread.sleep(IP_RETRY_INTERVAL);
                }

                Imgproc.blur(frame, blur, new Size(8.0, 8.0));
                bS.apply(blur, mask, -1);
                Imgproc.erode(mask, mask, new Mat());
                Imgproc.dilate(mask, mask, new Mat());

                //  Hog.compute(mask, descriptors);
                MatOfRect foundLocations = new MatOfRect();
                MatOfDouble foundWeights = new MatOfDouble();
                Hog.detectMultiScale(mask, foundLocations, foundWeights);


                if (foundWeights.toList().size() > 0) {
                    LOG.info("Locations " + String.valueOf(foundLocations.toList().size()));
                    LOG.info("Weights " + String.valueOf(foundWeights.toList().size()));
                    Highgui.imwrite("img/" + System.currentTimeMillis() + ".jpg", frame);
                    Highgui.imwrite("img/" + System.currentTimeMillis() + "-m.jpg", mask);
                }


            }
        } catch (InterruptedException e) {
            LOG.error("Thread Exception", e);
        } catch (Exception e) {
            LOG.error("Other Exception", e);
        }

    }

    private void addQueueItem() throws Exception {

        MatOfByte jpg = new MatOfByte();
        Highgui.imencode(".jpg", winnerFrame, jpg);
        if (!queue.offer(new Item(jpg.toArray(), winnerFrameScore)))
            LOG.error("Queue is full, lost frame!");

    }


    private void getNextTimeSlot(boolean hasCaptured) throws Exception {
        //2 conditions to reset the timer.
        //1st Contition: Too much time has passed.
        if (backOff.getElapsedTimeMillis() >= TIME_BETWEEN_CAMERA_EVENTS) {
            LOG.info("Camera event has passed, resetting timer.");
            backOff.reset();
        }
        //by calling nextBackOffMillis, time slot is upgraded.
        if (hasCaptured) { //only if it has captured upgrade time slot.
            if (backOff.nextBackOffMillis() == ExponentialBackOff.STOP) {
                LOG.info("Max Interval has been reached, resetting.");
                backOff.reset();
            }
        }

        this.timeSlot = backOff.getCurrentIntervalMillis();
    }

    private void constructBackOff() {
        ExponentialBackOff.Builder builder = new ExponentialBackOff.Builder();
        builder.setInitialIntervalMillis(EXPONENTIAL_INIT_INTERVAL);
        builder.setMultiplier(EXPONENTIAL_MULTIPLIER);
        builder.setRandomizationFactor(EXPONENTIAL_RANDOMIZATION);
        builder.setMaxElapsedTimeMillis(EXPONENTIAL_MAX_ELAPSED_TIME);
        builder.setMaxIntervalMillis(EXPONENTIAL_MAX_INTERVAL_MILLIS);
        this.backOff = builder.build();
    }


}