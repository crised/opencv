package utils;

/**
 * Created by crised on 4/6/15.
 */
public final class Consts {

    private Consts() {
    }

    public static final String CL_TELEMATIC = "cl.telematic";

    public static final String IP_STREAM_ADDRESS = "http://192.168.1.67/videostream.cgi?user=admin&pwd=admin";
    public static final int IP_RETRY_INTERVAL = 4_000;
    public static final int TIME_BETWEEN_FRAME_EVENTS = 5_000;
    public static final int FEEDER_FRAME_DELAY = 10;
    public static final long PERIODIC_NIGHT_MODE = 5 * 60 * 1_000; //5 Minutes
    //use high pixels, to be very classifier.
    public static final double LOWER_BOUND_PIXELS_PEDESTRIANS = 1000; // 640 x 480 x 0.0005 = 150
    public static final double UPPER_BOUND_PIXELS_PEDESTRIANS = 25_000; // 640 x 480 x 0.01
    public static final double LOWER_BOUND_PIXELS_VEHICLES = 4000; // 640 x 480 x 0.005
    public static final double UPPER_BOUND_PIXELS_VEHICLES = 50_000; // 640 x 480 x 0.11
    public static final String BUCKET_NAME = "telematic.cama";
    public static final double LATITUDE = -34.3603452;
    public static final double LONGITUDE = -71.0147265;


}
