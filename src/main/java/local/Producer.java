package local;

import com.google.api.client.util.ExponentialBackOff;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.LinkedBlockingQueue;

import static utils.Consts.*;

/**
 * Created by crised on 4/6/15.
 */
public class Producer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private final LinkedBlockingQueue queue;
    private Mat frame, blur, mask;
    private Core cvCore;
    private long initTime;
    private ExponentialBackOff backOff;
    private BackgroundSubtractorMOG2 bS;
    private HOGDescriptor Hog;
    private CascadeClassifier cascade;

    /*
    pMOG2_g.history = 3000; //300;
	pMOG2_g.varThreshold =128; //64; //128; //64; //32;//;
	pMOG2_g.bShadowDetection = false; // true;//
     */

    public Producer(LinkedBlockingQueue queue) {
        this.queue = queue;
        this.frame = new Mat();
        this.blur = new Mat();
        this.mask = new Mat();
        this.cvCore = new Core();
        this.bS = new BackgroundSubtractorMOG2(300, 128, true);
        this.Hog = new HOGDescriptor();
        this.Hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        this.cascade = new CascadeClassifier("/home/crised/IdeaProjects/opencv/src/main/resources/cars3.xml");

    }

    @Override
    public void run() {
        try {

            LOG.info("Producer started");
            org.opencv.highgui.VideoCapture vCap = new org.opencv.highgui.VideoCapture();
            vCap.open(IP_STREAM_ADDRESS);
            if (!vCap.isOpened()) LOG.error("Couldn't open Video Stream");
            //LOG.info("Frame Width " + vCap.get(Highgui.CV_CAP_PROP_FRAME_WIDTH));

            while (true) {

                initTime = System.currentTimeMillis();
                //Thread.sleep(REFRESH_RATE_DELAY);
                if (!vCap.read(frame)) {
                    LOG.error("Couldn't read Video Stream");
                    Thread.sleep(IP_RETRY_INTERVAL);
                }

                //Background Substractor, could be done in different thread.
                Imgproc.blur(frame, blur, new Size(8.0, 8.0));
                bS.apply(blur, mask, -1);
                Imgproc.erode(mask, mask, new Mat());
                Imgproc.dilate(mask, mask, new Mat());

                if (cvCore.countNonZero(mask) > 0.11 * 640 * 480) {
                    LOG.warn("Discard frame, too much info");
                    continue;
                }

                if (cvCore.countNonZero(mask) < 0.001 * 640 * 480)
                    continue;


                //Pedestrian detection.
                MatOfRect foundLocationsPed = new MatOfRect();
                MatOfDouble foundWeights = new MatOfDouble();
                Hog.detectMultiScale(mask, foundLocationsPed, foundWeights);


                if (foundLocationsPed.toList().size() > 0 || foundLocationsPed.toList().size() > 0) {
                    LOG.info("Pedestiran Locations " + String.valueOf(foundLocationsPed.toList().size()));
                    writeToDisk("PED");
                    queueItem();
                    continue;
                }


                //Vehicle detection
                MatOfRect foundLocationsVeh = new MatOfRect();
                cascade.detectMultiScale(mask, foundLocationsVeh);
                if (foundLocationsVeh.toList().size() > 0) {
                    LOG.info("Vehicle Locations " + String.valueOf(foundLocationsVeh.toList().size()));
                    writeToDisk("VEH");
                    queueItem();
                }
                LOG.info("Iteration time: " + String.valueOf(System.currentTimeMillis() - initTime));
            }
        } catch (InterruptedException e) {
            LOG.error("Thread Exception", e);
        } catch (Exception e) {
            LOG.error("Other Exception", e);
        }

    }

    private void queueItem() throws Exception {

        MatOfByte jpg = new MatOfByte();
        Highgui.imencode(".jpg", frame, jpg);
        if (!queue.offer(new ItemS3(jpg.toArray())))
            LOG.error("Queue is full, lost frame!");

    }

    private void writeToDisk(String prepend) throws Exception{

        Highgui.imwrite("img/" + prepend + System.currentTimeMillis() + ".jpg", frame);
        Highgui.imwrite("img/" + prepend + System.currentTimeMillis() + "-m.jpg", frame);

    }


}