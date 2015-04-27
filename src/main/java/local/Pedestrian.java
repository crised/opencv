package local;

import net.ItemS3;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.HOGDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.Consts.*;

/**
 * Created by crised on 4/27/15.
 */
public class Pedestrian implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private final Feeder feeder;
    private final LinkedBlockingQueue queue;
    private Core cvCore;
    private Mat frame, mask;
    private HOGDescriptor Hog;
      private List<Mat> frames;

    public Pedestrian(Feeder feeder, LinkedBlockingQueue queue) {
        this.feeder = feeder;
        this.queue = queue;
        this.cvCore = new Core();
        this.Hog = new HOGDescriptor();
        this.Hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
    }

    @Override
    public void run() {
        LOG.info("Pedestrian Started");
        while (true) {
            try {
                Thread.sleep(50);
                frames = feeder.getFrames();
                if (frames == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }
                this.frame = frames.get(0);
                this.mask = frames.get(1);
                if (!(cvCore.countNonZero(mask) > LOWER_BOUND_PIXELS_PEDESTRIANS
                        && cvCore.countNonZero(mask) < UPPER_BOUND_PIXELS_PEDESTRIANS)) continue;
                //writeToDisk();
                MatOfRect foundLocations = new MatOfRect();
                MatOfDouble foundWeights = new MatOfDouble();
                //Mat grayscale = new Mat();
                //frame.convertTo(grayscale, CvType.CV_8U);
                Hog.detectMultiScale(frame, foundLocations, foundWeights);
                if (foundLocations.toList().size() > 0 || foundLocations.toList().size() > 0) {
                    LOG.info("Pedestrian Locations " + String.valueOf(foundLocations.toList().size()));
                    writeToDisk();
                    queueItem();
                    LOG.info("sleeping");
                    Thread.sleep(TIME_BETWEEN_FRAME_EVENTS);
                }
            } catch (InterruptedException e) {
                LOG.error("Thread Exception", e);
            } catch (Exception e) {
                LOG.error("Other Exception", e);
            }
        }
    }


    private void queueItem() throws Exception {

        MatOfByte jpg = new MatOfByte();
        Highgui.imencode(".jpg", frame, jpg);
        if (!queue.offer(new ItemS3(jpg.toArray(), "p")))
            LOG.error("Queue is full, lost frame!");

    }

    private void writeToDisk() throws Exception {
        Highgui.imwrite("img/" + "p" + System.currentTimeMillis() + ".jpg", frame);
        Highgui.imwrite("img/" + "p" + System.currentTimeMillis() + "-m.jpg", mask);
    }
}
