package local;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Created by crised on 4/2/15.
 */
public class SimpleSample {

    //-Djava.library.path=/opt/opencv-2.4.10/build/lib
    //JAVA_HOME to correct jvm

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {

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
