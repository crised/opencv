package local;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static utils.Consts.*;

/**
 * Created by crised on 4/13/15.
 */
public class Upload implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private AmazonS3 s3client;
    private byte[] data;
    private String filename;


    public Upload(byte[] data) {
        s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        this.data = data;
        Format formatter = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy_S_X");
        this.filename = formatter.format(new Date()) + ".jpg";

    }

    private PutObjectRequest getPutObjectRequest() {

        // byte[] data = fid.getData(); //Easy Change
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(data.length);
        return new PutObjectRequest(BUCKET_NAME,
                filename,
                new ByteArrayInputStream(data),
                objectMetadata);
    }

    @Override
    public void run() {

        try {
            LOG.info("Uploading image: " + filename);
            s3client.putObject(getPutObjectRequest());
            //Consider saving the image in case of network error.

        } catch (AmazonServiceException ase) {
            LOG.error("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            LOG.error("Error Message:    " + ase.getMessage());
            LOG.error("HTTP Status Code: " + ase.getStatusCode());
            LOG.error("AWS Error Code:   " + ase.getErrorCode());
            LOG.error("Error Type:       " + ase.getErrorType());
            LOG.error("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            LOG.error("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            LOG.error("Error Message: " + ace.getMessage());
        }
    }


}

