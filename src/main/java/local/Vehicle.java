package local;

import net.Consumer;
import net.ItemS3;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.HOGDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static utils.Consts.*;

/**
 * Created by crised on 4/27/15.
 */
public class Vehicle implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private final Feeder feeder;
    private final LinkedBlockingQueue queue;
    private Core cvCore;
    private HOGDescriptor Hog;
    private Consumer consumer;
    private IMats iMats;

    private CascadeClassifier cascade;

    public Vehicle(Feeder feeder, LinkedBlockingQueue queue, Consumer consumer) {
        this.feeder = feeder;
        this.queue = queue;
        this.consumer = consumer;
        this.cvCore = new Core();
        this.Hog = new HOGDescriptor();
        this.Hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        this.cascade = new CascadeClassifier("/home/crised/IdeaProjects/opencv/src/main/resources/cars3.xml");
    }


    @Override
    public void run() {
        LOG.info("Vehicle Started");
        while (true) {
            try {
                Thread.sleep(50);
                iMats = feeder.getiMats();
                if (iMats == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }

                if (!(cvCore.countNonZero(iMats.getMask()) > LOWER_BOUND_PIXELS_VEHICLES
                        && cvCore.countNonZero(iMats.getMask()) < UPPER_BOUND_PIXELS_VEHICLES)) continue;
                //LOG.info("passed if");
                MatOfRect foundLocations = new MatOfRect();
                cascade.detectMultiScale(iMats.getMask(), foundLocations); //cannot feed frame, because vehicle could be parked
                if (foundLocations.toList().size() > 0) { //could be size directly
                    LOG.info("Vehicle Locations " + String.valueOf(foundLocations.toList().size()));
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
        if (!queue.offer(new ItemS3(jpg.toArray(), "v")))
            LOG.error("Queue is full, lost frame!");
    }

    private void writeToDisk() throws Exception {
        Highgui.imwrite("img/" + "v" + System.currentTimeMillis() + ".jpg", iMats.getFrame());
        Highgui.imwrite("img/" + "v" + System.currentTimeMillis() + "-m.jpg", iMats.getMask());
    }
}
