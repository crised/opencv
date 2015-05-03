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

    private Core cvCore;
    private HOGDescriptor Hog;
    private Consumer consumer;
    private IMats iMats;
    private WriteToDisk writeToDisk;
    private DayNight dayNight;

    public Pedestrian(Consumer consumer, WriteToDisk writeToDisk, DayNight dayNight, IMats iMats) {
        this.consumer = consumer;
        this.writeToDisk = writeToDisk;
        this.dayNight = dayNight;
        this.cvCore = new Core();
        this.Hog = new HOGDescriptor();
        this.Hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        this.iMats = iMats;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(PEDESTRIAN_DELAY);
                if (!dayNight.isDay()) continue;
                if (iMats.getFrame() == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }
                if (!(cvCore.countNonZero(iMats.getMask()) > LOWER_BOUND_PIXELS_PEDESTRIANS
                        && cvCore.countNonZero(iMats.getMask()) < UPPER_BOUND_PIXELS_PEDESTRIANS)) continue;
                MatOfRect mLocations = new MatOfRect();
                MatOfRect fLocations = new MatOfRect();
                MatOfDouble mWeights = new MatOfDouble();
                MatOfDouble fWeights = new MatOfDouble();
                Hog.detectMultiScale(iMats.getMask(), mLocations, mWeights);
                Hog.detectMultiScale(iMats.getFrame(), fLocations, fWeights);
                if (mLocations.empty() && fLocations.empty() && mWeights.empty() && fWeights.empty())
                    continue;
                LOG.info("Pedestrian Locations summed " + String.valueOf(mLocations.toList().size() + fLocations.toList().size()));
                writeToDisk.writeToDisk("p", iMats);
                consumer.queueItem(iMats.getFrame());
            } catch (InterruptedException e) {
                LOG.error("Thread Exception", e);
            } catch (Exception e) {
                LOG.error("Other Exception", e);
            }
        }
    }
}