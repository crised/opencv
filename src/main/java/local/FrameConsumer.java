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
import java.util.concurrent.LinkedBlockingQueue;

import static utils.Consts.*;

/**
 * Created by crised on 4/13/15.
 */
public class FrameConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);

    private final LinkedBlockingQueue queue;
    private AmazonS3 s3client;

    public FrameConsumer(LinkedBlockingQueue queue) {
        this.queue = queue;
        s3client = new AmazonS3Client(new ProfileCredentialsProvider());
    }

    @Override
    public void run() {

        try {
            LOG.info("Consumer started");
            while (true) {
                Item item = (Item) queue.take(); //blocking method
                LOG.info("Uploading image: " + item.getFileName());
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(item.getData().length);
                s3client.putObject(new PutObjectRequest(BUCKET_NAME,
                        item.getFileName(),
                        new ByteArrayInputStream(item.getData()),
                        objectMetadata));
                //Consider saving the image in case of network error.
            }
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
        } catch (InterruptedException e) {
            LOG.error("Probably interrupted queue", e);

        } catch (Exception e) {
            LOG.error("FrameConsumer error", e);
        }
    }

}
