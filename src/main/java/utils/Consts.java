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
    public static final int FEEDER_FRAME_DELAY = 50;
    public static final double LOWER_BOUND_PIXELS_PEDESTRIANS = 50; // 640 x 480 x 0.0005 = 150
    public static final double UPPER_BOUND_PIXELS_PEDESTRIANS = 3_007; // 640 x 480 x 0.01
    public static final double LOWER_BOUND_PIXELS_VEHICLES = 1536; // 640 x 480 x 0.005
    public static final double UPPER_BOUND_PIXELS_VEHICLES = 33_800; // 640 x 480 x 0.11
    public static final String BUCKET_NAME = "telematic.cama";


}
