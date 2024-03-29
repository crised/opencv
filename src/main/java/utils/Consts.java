package utils;

/**
 * Created by crised on 4/6/15.
 */
public final class Consts {

    private Consts() {
    }

    public static IMats iMats;
    public static final DayNight dayNight = new DayNight();

    public static final String CL_TELEMATIC = "cl.telematic";
    public static final String BUCKET_NAME = "telematic.apu";
    public static final String IP_ADDRESS = "192.168.1.10";
    public static final String IP_STREAM_ADDRESS = "http://" + IP_ADDRESS + "/videostream.cgi?user=admin&pwd=admin";
    public static final int IP_RETRY_INTERVAL = 10_000;
    public static final int FEEDER_DELAY = 500;
    public static final int PERIODIC_DELAY = 10_000;
    public static final long PERIODIC_NIGHT_MODE = 30 * 60 * 1_000; //30 Minutes
    public static final long PERIODIC_DAY_MODE = 5 * 60 * 1_000; //5 Minutes
    public static final double LOWER_BOUND_PIXELS = 1000; // 640 x 480 x 0.0005 = 150
    public static final double UPPER_BOUND_PIXELS = 50_000; // 640 x 480 x 0.11
    public static final double LATITUDE = -34.3603452;
    public static final double LONGITUDE = -71.0147265;
    public static final String PERIODIC_DAY_KIND = "d";
    public static final String PERIODIC_NIGHT_KIND = "n";
}
