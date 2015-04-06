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

        Local local = new Local();
        local.capture();
    }

}
