package local;

import net.Consumer;
import org.opencv.core.*;
import org.opencv.objdetect.CascadeClassifier;
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
public class Vehicle implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private final Feeder feeder;
    private Core cvCore;
    private HOGDescriptor Hog;
    private Consumer consumer;
    private IMats iMats;
    private WriteToDisk writeToDisk;
    private DayNight dayNight;

    private CascadeClassifier cascade;

    public Vehicle(Feeder feeder, Consumer consumer, WriteToDisk writeToDisk, DayNight dayNight, IMats iMats) {
        this.feeder = feeder;
        this.consumer = consumer;
        this.writeToDisk = writeToDisk;
        this.dayNight = dayNight;
        this.cvCore = new Core();
        this.Hog = new HOGDescriptor();
        this.Hog.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
        this.cascade = new CascadeClassifier("/home/crised/IdeaProjects/opencv/src/main/resources/cars3.xml");
        this.iMats = iMats;
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(VEHICLE_DELAY);
                if (!dayNight.isDay()) return;
                if (iMats.getFrame() == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }
                if (!(cvCore.countNonZero(iMats.getMask()) > LOWER_BOUND_PIXELS_VEHICLES
                        && cvCore.countNonZero(iMats.getMask()) < UPPER_BOUND_PIXELS_VEHICLES)) continue;
                MatOfRect mLocations = new MatOfRect();
                cascade.detectMultiScale(iMats.getMask(), mLocations);
                MatOfRect fLocations = new MatOfRect();
                cascade.detectMultiScale(iMats.getFrame(), fLocations);
                if (mLocations.empty() && fLocations.empty()) continue;
                LOG.info("Vehicle Locations " + String.valueOf(mLocations.toList().size() + fLocations.toList().size()));
                writeToDisk.writeToDisk("v" + cvCore.countNonZero(iMats.getMask()), iMats);
                consumer.queueItem(iMats.getFrame());
            } catch (InterruptedException e) {
                LOG.error("Thread Exception", e);
            } catch (Exception e) {
                LOG.error("Other Exception", e);
            }
        }
    }


}
