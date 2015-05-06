package local;

import net.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static utils.Consts.*;

/**
 * Created by crised on 4/30/15.
 * This class can serve as backup to files too.
 */
public class Periodic implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private Consumer consumer;
    private long lastTimestamp;

    public Periodic(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        LOG.info("Periodic Thread Started");
        while (true) {
            try {
                Thread.sleep(PERIODIC_DELAY);
                if (iMats == null) {
                    LOG.info("waiting frame list");
                    Thread.sleep(10000);
                    continue;
                }
                if (lastTimestamp == iMats.getTimestamp()) {
                    LOG.info("duplicated image, not uploading;");
                    Thread.sleep(PERIODIC_DAY_MODE);
                    continue;
                }
                lastTimestamp = iMats.getTimestamp();
                //day mode:
                if (dayNight.isDay()) {
                    consumer.queueItem(iMats.getFrame(), iMats.getTimestamp() + PERIODIC_DAY_KIND);
                    Thread.sleep(PERIODIC_DAY_MODE);
                    continue;
                }
                consumer.queueItem(iMats.getFrame(), iMats.getTimestamp() + PERIODIC_NIGHT_KIND);
                Thread.sleep(PERIODIC_NIGHT_MODE);
            } catch (InterruptedException e) {
                LOG.error("Thread Exception", e);
            } catch (Exception e) {
                LOG.error("Other Exception", e);
            }
        }
    }
}
