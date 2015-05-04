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

    //TODO: HeartBeat image, if no motion has detected in 30 mins..

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private Consumer consumer;
    private WriteToDisk writeToDisk;

    public Periodic(Consumer consumer, WriteToDisk writeToDisk) {
        this.consumer = consumer;
        this.writeToDisk = writeToDisk;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(PERIODIC_DELAY);
                if (iMats == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }
                if (consumer.IsHeartBeatNeeded()) consumer.queueItem(iMats.getFrame());
                if (dayNight.isDay()) continue;
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
