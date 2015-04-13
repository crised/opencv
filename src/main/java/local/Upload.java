package local;

import com.amazonaws.services.s3.AmazonS3;

/**
 * Created by crised on 4/13/15.
 */
public class Upload implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger("app");

    private AmazonS3 s3client;



    public NetworkSide(Reader.CaptureResult captureResult) {
        s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        this.captureResult = captureResult;
        this.fid = captureResult.image;
        convert();
    }

    private void convert() {
        try {
            Engine engine = UareUGlobal.GetEngine();
            this.fmd = engine.CreateFmd(fid, Fmd.Format.ISO_19794_2_2005);
        } catch (UareUException e) {
            LOG.error("Couldn't convert Minutiae");
        }
    }

    private PutObjectRequest getPutObjectRequest() {

        // byte[] data = fid.getData(); //Easy Change
        byte[] data = fmd.getData();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(data.length);
        return new PutObjectRequest(BUCKET_NAME,
                String.valueOf(System.currentTimeMillis()),
                new ByteArrayInputStream(data),
                objectMetadata);
    }

    @Override
    public void run() {

        try {
            LOG.info("Uploading a new object, capture quality: " + captureResult.quality.toString());
            s3client.putObject(getPutObjectRequest());

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
}
