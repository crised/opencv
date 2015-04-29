package local;

import net.Consumer;
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
    private HOGDescriptor Hog;
    private Consumer consumer;
    private IMats iMats;

    public Pedestrian(Feeder feeder, LinkedBlockingQueue queue, Consumer consumer) {
        this.feeder = feeder;
        this.queue = queue;
        this.consumer = consumer;
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
                iMats = feeder.getiMats();
                if (iMats == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }

                if (!(cvCore.countNonZero(iMats.getMask()) > LOWER_BOUND_PIXELS_PEDESTRIANS
                        && cvCore.countNonZero(iMats.getMask()) < UPPER_BOUND_PIXELS_PEDESTRIANS)) continue;
                //writeToDisk();
                MatOfRect foundLocations = new MatOfRect();
                MatOfDouble foundWeights = new MatOfDouble();
                //Mat grayscale = new Mat();
                //frame.convertTo(grayscale, CvType.CV_8U);
                Hog.detectMultiScale(iMats.getMask(), foundLocations, foundWeights);
                if (foundLocations.toList().size() > 0 || foundLocations.toList().size() > 0) {
                    LOG.info("Pedestrian Locations " + String.valueOf(foundLocations.toList().size()));
                    writeToDisk();
                    queueItem();
                }
            } catch (InterruptedException e) {
                LOG.error("Thread Exception", e);
            } catch (Exception e) {
                LOG.error("Other Exception", e);
            }
        }
    }


    private void queueItem() throws Exception {

        if (System.currentTimeMillis() - consumer.getLastUploadedTime() < 5000) {
            LOG.info("did not queue!");
            return;
        }

        MatOfByte jpg = new MatOfByte();
        Highgui.imencode(".jpg", iMats.getFrame(), jpg);
        if (!queue.offer(new ItemS3(jpg.toArray(), "p")))
            LOG.error("Queue is full, lost frame!");

    }

    private void writeToDisk() throws Exception {
        Highgui.imwrite("img/" + "p" + System.currentTimeMillis() + ".jpg", iMats.getFrame());
        Highgui.imwrite("img/" + "p" + System.currentTimeMillis() + "-m.jpg", iMats.getMask());
    }
}
