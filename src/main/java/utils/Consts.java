package utils;

/**
 * Created by crised on 4/6/15.
 */
public final class Consts {

    private Consts() {
    }

    public static final String CL_TELEMATIC = "cl.telematic";

    public static final String IP_STREAM_ADDRESS = "http://192.168.1.47/videostream.cgi?user=admin&pwd=admin";
    public static final int IP_RETRY_INTERVAL = 5_000;
    public static final int REFRESH_RATE_DELAY = 200;
    public static final int CAPTURE_PIXELS_THRESHOLD = 3_000;
    public static final int EXPONENTIAL_INIT_INTERVAL = 5_000;
    public static final int EXPONENTIAL_MULTIPLIER = 4;
    public static final int EXPONENTIAL_RANDOMIZATION = 0;
    public static final int EXPONENTIAL_MAX_ELAPSED_TIME = 600_000; //320 seconds is the maximum value (the next is 1280, then BackOff.Stop
    public static final int EXPONENTIAL_MAX_INTERVAL_MILLIS = 8_000_000; //8000 seconds an arbitrary maximum value,
    // the next value after limit is x.
    public static final int TIME_BETWEEN_CAMERA_EVENTS = 360_000; //6 min
    public static final String BUCKET_NAME = "telematic.cam";


}
