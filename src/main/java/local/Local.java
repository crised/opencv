package local;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static utils.Consts.CL_TELEMATIC;

/**
 * Created by crised on 4/6/15.
 */
public class Local {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private String videoStreamAddress = "http://192.168.1.34/videostream.cgi?user=admin&pwd=admin";
    Mat frame;
    int delay;

    public Local() {
        frame = new Mat();
    }

    public void capture() throws Exception {

        VideoCapture vCap = new VideoCapture();
        vCap.open(videoStreamAddress);
       // vCap.open(0);
        if (!vCap.isOpened()) LOG.error("Couldn't open Video Stream");

        double frame_width = vCap.get(Highgui.CV_CAP_PROP_FRAME_WIDTH);
        double frame_height = vCap.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT);
        LOG.info("Frame Width " + frame_width);
        LOG.info("Frame Height " + frame_height);

        //Thread.sleep(1000);
        //Highgui.imwrite();

        BackgroundSubtractorMOG2 bS = new BackgroundSubtractorMOG2();


        while (true) {
            //Thread.sleep(delay);
            if (!vCap.read(frame)) LOG.error("Couldn't read Video Stream");
            Mat mask = new Mat();

            Imgproc.blur(frame,frame,new Size(3.0,3.0));

            bS.apply(frame, mask, -1);

            //Imgproc.GaussianBlur(mask, mask, new Size(5, 5), 3.5, 3.5); //With this line works good.
           // Imgproc.threshold(mask, mask, 10, 255, Imgproc.THRESH_BINARY);

            long time = System.currentTimeMillis();
            Highgui.imwrite("img/" + time + ".jpg", frame);
            Highgui.imwrite("img/" + time + "m.jpg", mask);
        }
    }

    public void init() {
        System.out.println("Welcome to OpenCV " + Core.VERSION + "Lib Name: " + Core.NATIVE_LIBRARY_NAME);
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());
    }
}
