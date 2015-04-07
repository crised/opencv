package local;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static void main(String[] args) throws Exception{

        Local local = new Local();
        local.init();
        local.capture();
    }

}
