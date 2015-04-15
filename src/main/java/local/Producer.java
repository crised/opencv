package local;

import com.google.api.client.util.ExponentialBackOff;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.Consts.*;

/**
 * Created by crised on 4/6/15.
 */
public class Producer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private final LinkedBlockingQueue queue;
    private Mat frame, blur, mask, winnerFrame;
    private Core cvCore;
    private long timeSlot, capturePixelScore, winnerFrameScore, lastPassed;
    private ExponentialBackOff backOff;
    private BackgroundSubtractorMOG2 bS;
    private NavigableMap<Long, Mat> candidatesMap;


    public Producer(LinkedBlockingQueue queue) {
        this.queue = queue;
        this.frame = new Mat();
        this.blur = new Mat();
        this.mask = new Mat();
        this.cvCore = new Core();
        this.bS = new BackgroundSubtractorMOG2();
        constructBackOff();
        this.candidatesMap = new TreeMap<>();
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
                capturePixelScore = cvCore.countNonZero(mask);
                LOG.info(String.valueOf(capturePixelScore));
                Highgui.imwrite("img/" + String.valueOf(System.currentTimeMillis()) + ".jpg", blur);
                Highgui.imwrite("img/" + String.valueOf(System.currentTimeMillis()) + "-m.jpg", mask);

                /*
               if (capturePixelScore > CAPTURE_PIXELS_THRESHOLD) {
                    this.candidatesMap.put(capturePixelScore, frame);
                }


                if (System.currentTimeMillis() - lastPassed > timeSlot) {
                    LOG.info("Current time slot done, interval: " + timeSlot / 1000);
                    lastPassed = System.currentTimeMillis();

                    if (candidatesMap.size() == 0) {
                        //no need to do backoff
                        getNextTimeSlot(false);
                    } else {

                        Iterator it = candidatesMap.entrySet().iterator();
                        for (int i = 1; i < candidatesMap.size() / 2; i++) it.next();

                        Map.Entry<Long, Mat> pair = (Map.Entry) it.next();
                        winnerFrame = pair.getValue();
                        winnerFrameScore = pair.getKey();
                        //winnerFrameScore = candidatesMap.lastEntry().getKey();
                        //winnerFrame = candidatesMap.lastEntry().getValue();
                        LOG.info("adding to queue");
                        addQueueItem();
                        this.candidatesMap.clear();
                        getNextTimeSlot(true);
                    }
                }*/
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