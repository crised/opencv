package local;

import net.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DayNight;
import utils.IMats;
import utils.WriteToDisk;


import static utils.Consts.*;

/**
 * Created by crised on 4/30/15.
 * This class can serve as backup to files too.
 */
public class Periodic implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private Consumer consumer;
    private IMats iMats;
    private WriteToDisk writeToDisk;
    private DayNight dayNight;

    public Periodic(Consumer consumer, WriteToDisk writeToDisk, DayNight dayNight, IMats iMats) {
        this.consumer = consumer;
        this.writeToDisk = writeToDisk;
        this.dayNight = dayNight;
        this.iMats = iMats;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(PERIODIC_DELAY);
                if (dayNight.isDay()) continue;
                if (iMats.getFrame() == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }
                consumer.queueItem(iMats.getFrame());
                Thread.sleep(PERIODIC_NIGHT_MODE);
            } catch (InterruptedException e) {
                LOG.error("Thread Exception", e);
            } catch (Exception e) {
                LOG.error("Other Exception", e);
            }
        }
    }


}
