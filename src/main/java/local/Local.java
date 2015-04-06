package local;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;

/**
 * Created by crised on 4/6/15.
 */
public class Local {

    private String videoStreamAddress="http://192.168.1.34/videostream.cgi?user=admin&pwd=admin";

    public void capture(){

        VideoCapture videoCapture = new VideoCapture();
        if(!videoCapture.open(videoStreamAddress)){
            System.out.println("ouch");
        } else {
            videoCapture.release();
            System.out.println("done");
        }


    }

    public void init(){
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
