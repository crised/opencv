import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import utils.DayNight;

import static utils.Consts.*;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;

public class Small {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);


    @Test
    public void Formatter(){

        Format formatter = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss_X");
        LOG.info(formatter.format(System.currentTimeMillis()));


    }

    @Test(enabled=false)
    public void isDay() {

        LOG.info("hello");
        DayNight dayNight = new DayNight();
        LOG.info(String.valueOf(dayNight.isDay()));
        /*
        LOG.info("Sunrise: " + String.valueOf(dayNight.getSunrise().getTime().toString()));
        LOG.info("Sunset: " + String.valueOf(dayNight.getSunset().getTime().toString()));*/


    }


    @Test(enabled = false)
    public void aws() {

        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File("/home/crised/IdeaProjects/uareu/src/test/java/Small.java");
            s3client.putObject(new PutObjectRequest(
                    BUCKET_NAME, "1", file));

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

    }}