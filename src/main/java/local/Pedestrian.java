package local;

import net.Consumer;
import org.opencv.core.*;
import org.opencv.objdetect.HOGDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DayNight;
import utils.IMats;
import utils.WriteToDisk;


import static utils.Consts.*;

/**
 * Created by crised on 4/27/15.
 */
public class Pedestrian implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private final Feeder feeder;
    private Core cvCore;
    private HOGDescriptor Hog;
    private Consumer consumer;
    private IMats iMats;
    private WriteToDisk writeToDisk;
    private DayNight dayNight;

    public Pedestrian(Feeder feeder, Consumer consumer, WriteToDisk writeToDisk, DayNight dayNight) {
        this.feeder = feeder;
        this.consumer = consumer;
        this.writeToDisk = writeToDisk;
        this.dayNight = dayNight;
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
                if (!dayNight.isDay()) continue;
                iMats = feeder.getiMats();
                if (iMats == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }
                if (!(cvCore.countNonZero(iMats.getMask()) > LOWER_BOUND_PIXELS_PEDESTRIANS
                        && cvCore.countNonZero(iMats.getMask()) < UPPER_BOUND_PIXELS_PEDESTRIANS)) continue;
                MatOfRect foundLocations = new MatOfRect();
                MatOfDouble foundWeights = new MatOfDouble();
                Hog.detectMultiScale(iMats.getFrame(), foundLocations, foundWeights); //frame for pedestrian detection, could be mask. -> needs tuning.
                if (foundLocations.toList().size() > 0 || foundLocations.toList().size() > 0) {
                    LOG.info("Pedestrian Locations " + String.valueOf(foundLocations.toList().size()));
                    writeToDisk.writeToDisk("p", iMats.getFrame());
                    writeToDisk.writeToDisk("p.m", iMats.getMask());
                    consumer.queueItem(iMats.getFrame());
                }
            } catch (InterruptedException e) {
                LOG.error("Thread Exception", e);
            } catch (Exception e) {
                LOG.error("Other Exception", e);
            }
        }
    }


}
