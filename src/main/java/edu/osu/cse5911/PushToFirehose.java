package edu.osu.cse5911;

import java.io.IOException;

/*
 * Copyright 2012-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.List;

import org.apache.logging.log4j.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.model.BufferingHints;
import com.amazonaws.services.kinesisfirehose.model.CompressionFormat;
import com.amazonaws.services.kinesisfirehose.model.CreateDeliveryStreamRequest;
import com.amazonaws.services.kinesisfirehose.model.DeleteDeliveryStreamRequest;
import com.amazonaws.services.kinesisfirehose.model.EncryptionConfiguration;
import com.amazonaws.services.kinesisfirehose.model.ExtendedS3DestinationConfiguration;
import com.amazonaws.services.kinesisfirehose.model.NoEncryptionConfig;


/**
 * Amazon Kinesis Firehose is a fully managed service for real-time streaming
 * data delivery to destinations such as Amazon S3 and Amazon Redshift. Firehose
 * is part of the Amazon Kinesis streaming data family, along with Amazon
 * Kinesis Streams. With Firehose, you do not need to write any applications or
 * manage any resources. You configure your data producers to send data to
 * Firehose and it automatically delivers the data to the destination that you
 * specified.
 *
 * Detailed Amazon Kinesis Firehose documentation can be found here:
 * https://aws.amazon.com/documentation/firehose/
 *
 * This is a sample java application to deliver data to Amazon S3 destination.
 */
public class PushToFirehose extends AbstractAmazonKinesisFirehoseDelivery {

	/*
	 * Before running the code:
	 *
	 * Step 1: Please check you have AWS access credentials set under
	 * (~/.aws/credentials). If not, fill in your AWS access credentials in the
	 * provided credentials file template, and be sure to move the file to the
	 * default location (~/.aws/credentials) where the sample code will load the
	 * credentials from.
	 * https://console.aws.amazon.com/iam/home?#security_credential
	 *
	 * WARNING: To avoid accidental leakage of your credentials, DO NOT keep the
	 * credentials file in your source directory.
	 *
	 * Step 2: Update the firehosetos3sample.properties file with the required
	 * parameters.
	 */

	// Properties File
	// private static final String CONFIG_FILE = "firehosetos3sample.properties";

	// Logger
	private static final Logger LOG = LogManager.getLogger(AbstractAmazonKinesisFirehoseDelivery.class);

	public static void push(String content, String in_s3RegionName, String in_s3BucketName, String in_s3ObjectPrefix, String in_firehoseRegion, String in_iamRoleName, String in_iamRegion) {
		s3RegionName = in_s3RegionName;
		s3BucketName = in_s3BucketName;
		s3BucketARN = getBucketARN(s3BucketName);
		s3ObjectPrefix = in_s3ObjectPrefix;

		s3DestinationSizeInMBs = 128;
		s3DestinationIntervalInSeconds = 60;

		deliveryStreamName = in_s3ObjectPrefix;
		firehoseRegion = in_firehoseRegion;
		iamRoleName = in_iamRoleName;
		iamRegion = in_iamRegion;
		
		try {
			initClients();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			// Create the DeliveryStream
			createDeliveryStream();

			// Put records into deliveryStream
			LOG.info("Putting records in deliveryStream : " + deliveryStreamName);
			putRecordIntoDeliveryStream(content);
//			deleteDeliveryStream(in_s3ObjectPrefix);

		} catch (AmazonServiceException ase) {
			LOG.error("Caught Amazon Service Exception");
			LOG.error("Status Code " + ase.getErrorCode());
			LOG.error("Message: " + ase.getErrorMessage(), ase);
		} catch (AmazonClientException ace) {
			LOG.error("Caught Amazon Client Exception");
			LOG.error("Exception Message " + ace.getMessage(), ace);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Method to create delivery stream for S3 destination configuration.
	 *
	 * @throws Exception
	 */
	private static void createDeliveryStream() throws Exception {

		boolean deliveryStreamExists = false;

		LOG.info("Checking if " + deliveryStreamName + " already exits");
		List<String> deliveryStreamNames = listDeliveryStreams();
		if (deliveryStreamNames != null && deliveryStreamNames.contains(deliveryStreamName)) {
			deliveryStreamExists = true;
			LOG.info("DeliveryStream " + deliveryStreamName + " already exists. Not creating the new delivery stream");
		} else {
			LOG.info("DeliveryStream " + deliveryStreamName + " does not exist");
		}

		if (!deliveryStreamExists) {
			// Create deliveryStream
			CreateDeliveryStreamRequest createDeliveryStreamRequest = new CreateDeliveryStreamRequest();
			createDeliveryStreamRequest.setDeliveryStreamName(deliveryStreamName);

			ExtendedS3DestinationConfiguration s3DestinationConfiguration = new ExtendedS3DestinationConfiguration();
			s3DestinationConfiguration.setBucketARN(s3BucketARN);
			s3DestinationConfiguration.setPrefix(s3ObjectPrefix);
			// Could also specify GZIP or ZIP
			s3DestinationConfiguration.setCompressionFormat(CompressionFormat.UNCOMPRESSED);

			// Encryption configuration is optional
			EncryptionConfiguration encryptionConfiguration = new EncryptionConfiguration();
			encryptionConfiguration.setNoEncryptionConfig(NoEncryptionConfig.NoEncryption);
			s3DestinationConfiguration.setEncryptionConfiguration(encryptionConfiguration);

			BufferingHints bufferingHints = null;
			if (s3DestinationSizeInMBs != null || s3DestinationIntervalInSeconds != null) {
				bufferingHints = new BufferingHints();
				bufferingHints.setSizeInMBs(s3DestinationSizeInMBs);
				bufferingHints.setIntervalInSeconds(s3DestinationIntervalInSeconds);
			}
			s3DestinationConfiguration.setBufferingHints(bufferingHints);

			// Create and set IAM role so that firehose service has access to the S3Buckets
			// to put data
			// and KMS keys (if provided) to encrypt data. Please check the
			// trustPolicyDocument.json and
			// permissionsPolicyDocument.json files for the trust and permissions policies
			// set for the role.
			String iamRoleArn = createIamRole(s3ObjectPrefix);
			s3DestinationConfiguration.setRoleARN(iamRoleArn);

			createDeliveryStreamRequest.setExtendedS3DestinationConfiguration(s3DestinationConfiguration);

			firehoseClient.createDeliveryStream(createDeliveryStreamRequest);

			// The Delivery Stream is now being created.
			LOG.info("Creating DeliveryStream : " + deliveryStreamName);
			waitForDeliveryStreamToBecomeAvailable(deliveryStreamName);
		}
	}

}
