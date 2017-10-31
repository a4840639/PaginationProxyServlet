package edu.osu.cse5911;

import org.apache.logging.log4j.*;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class PushToS3 {
	private static final Logger logger = LogManager.getLogger(AbstractAmazonKinesisFirehoseDelivery.class);

	public static void push(String uploadFileName, String bucketName, String keyName) {
		AmazonS3 s3client = AmazonS3Client.builder().withRegion("us-east-1").withForceGlobalBucketAccessEnabled(true)
				.build();
		s3client.getBucketLocation(bucketName);
		try {
			logger.info("Uploading a new object to S3 from a file");
			File file = new File(uploadFileName);
			s3client.putObject(new PutObjectRequest(bucketName, keyName, file));

		} catch (AmazonServiceException ase) {
			logger.error("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			logger.error("Error Message:    " + ase.getMessage());
			logger.error("HTTP Status Code: " + ase.getStatusCode());
			logger.error("AWS Error Code:   " + ase.getErrorCode());
			logger.error("Error Type:       " + ase.getErrorType());
			logger.error("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			logger.error("Caught an AmazonClientException, which " + "means the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			logger.error("Error Message: " + ace.getMessage());
		}
	}
}