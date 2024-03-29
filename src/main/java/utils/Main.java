package utils;

import local.Feeder;
import local.Periodic;
import net.Consumer;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

import static utils.Consts.CL_TELEMATIC;

/**
 * Created by crised on 4/2/15.
 */
public class Main {

    //-Djava.library.path=/opt/opencv-2.4.10/build/lib
    //JAVA_HOME to correct jvm

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception {
        //checkOpenCV();
        new Thread(new Feeder()).start();
        final Consumer consumer = new Consumer(new LinkedBlockingQueue(100));
        new Thread(consumer).start();
        new Thread(new Periodic(consumer)).start();
    }


    private static void checkOpenCV() {
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
