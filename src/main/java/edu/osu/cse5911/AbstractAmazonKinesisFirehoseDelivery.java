package edu.osu.cse5911;

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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetRoleRequest;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClient;
import com.amazonaws.services.kinesisfirehose.model.DeleteDeliveryStreamRequest;
import com.amazonaws.services.kinesisfirehose.model.DeliveryStreamDescription;
import com.amazonaws.services.kinesisfirehose.model.DescribeDeliveryStreamRequest;
import com.amazonaws.services.kinesisfirehose.model.DescribeDeliveryStreamResult;
import com.amazonaws.services.kinesisfirehose.model.ListDeliveryStreamsRequest;
import com.amazonaws.services.kinesisfirehose.model.ListDeliveryStreamsResult;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchRequest;
import com.amazonaws.services.kinesisfirehose.model.PutRecordBatchResult;
import com.amazonaws.services.kinesisfirehose.model.PutRecordRequest;
import com.amazonaws.services.kinesisfirehose.model.Record;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * Abstract class that contains all the common methods used in samples for
 * Amazon S3 and Amazon Redshift destination.
 */
public abstract class AbstractAmazonKinesisFirehoseDelivery {
	// S3 properties
	protected static AmazonS3 s3Client;
	// protected static boolean createS3Bucket;
	protected static String s3BucketARN;
	protected static String s3BucketName;
	protected static String s3ObjectPrefix;
	protected static String s3RegionName;

	// DeliveryStream properties
	protected static AmazonKinesisFirehose firehoseClient;
	protected static String deliveryStreamName;
	protected static String firehoseRegion;
	// protected static boolean enableUpdateDestination;

	// S3Destination Properties
	protected static String iamRoleName;
	protected static Integer s3DestinationSizeInMBs;
	protected static Integer s3DestinationIntervalInSeconds;

	// Default wait interval for data to be delivered in specified destination.
	protected static final int DEFAULT_WAIT_INTERVAL_FOR_DATA_DELIVERY_SECS = 300;

	// S3 Bucket ARN
	private static final String S3_ARN_PREFIX = "arn:aws:s3:::";

	private static final int BATCH_PUT_MAX_SIZE = 500;

	// IAM Role
	protected static String iamRegion;
	protected static AmazonIdentityManagement iamClient;

	// Logger
	private static final Logger LOG = LogManager.getLogger(AbstractAmazonKinesisFirehoseDelivery.class);

	/**
	 * Method to initialize the clients using the specified AWSCredentials.
	 *
	 * @param Exception
	 */
	protected static void initClients() throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default] credential profile
		 * by reading from the credentials file located at (~/.aws/credentials).
		 */
		// AWSCredentials credentials = null;
		// try {
		// credentials = new ProfileCredentialsProvider().getCredentials();
		// } catch (Exception e) {
		// throw new AmazonClientException("Cannot load the credentials from the
		// credential profiles file. "
		// + "Please make sure that your credentials file is at the correct "
		// + "location (~/.aws/credentials), and is in valid format.", e);
		// }

		// S3 client
		s3Client = AmazonS3Client.builder().withRegion(s3RegionName).build();

		// Firehose client
		firehoseClient = AmazonKinesisFirehoseClient.builder().withRegion(firehoseRegion).build();

		// IAM client
		iamClient = AmazonIdentityManagementClient.builder().withRegion(iamRegion).build();
	}

	/**
	 * Method to list all the delivery streams in the customer account.
	 *
	 * @return the collection of delivery streams
	 */
	protected static List<String> listDeliveryStreams() {
		ListDeliveryStreamsRequest listDeliveryStreamsRequest = new ListDeliveryStreamsRequest();
		ListDeliveryStreamsResult listDeliveryStreamsResult = firehoseClient
				.listDeliveryStreams(listDeliveryStreamsRequest);
		List<String> deliveryStreamNames = listDeliveryStreamsResult.getDeliveryStreamNames();
		while (listDeliveryStreamsResult.isHasMoreDeliveryStreams()) {
			if (deliveryStreamNames.size() > 0) {
				listDeliveryStreamsRequest
						.setExclusiveStartDeliveryStreamName(deliveryStreamNames.get(deliveryStreamNames.size() - 1));
			}

			listDeliveryStreamsResult = firehoseClient.listDeliveryStreams(listDeliveryStreamsRequest);
			deliveryStreamNames.addAll(listDeliveryStreamsResult.getDeliveryStreamNames());

		}
		return deliveryStreamNames;
	}

	/**
	 * Method to put records in the specified delivery stream by reading contents
	 * from sample input file using PutRecord API.
	 *
	 * @throws IOException
	 */
	protected static void putRecordIntoDeliveryStream(InputStream inputStream) throws IOException {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				PutRecordRequest putRecordRequest = new PutRecordRequest();
				putRecordRequest.setDeliveryStreamName(deliveryStreamName);

				String data = line + "\n";
				Record record = createRecord(data);
				// System.out.println(record);
				putRecordRequest.setRecord(record);

				// Put record into the DeliveryStream
				// System.out.println(putRecordRequest);
				firehoseClient.putRecord(putRecordRequest);
			}
		}
	}

	/**
	 * Method to put records in the specified delivery stream by reading contents
	 * from sample input file using PutRecord API.
	 *
	 * @throws IOException
	 */
	protected static void putRecordIntoDeliveryStream(String str) throws IOException {

		PutRecordRequest putRecordRequest = new PutRecordRequest();
		putRecordRequest.setDeliveryStreamName(deliveryStreamName);

		Record record = createRecord(str);
		putRecordRequest.setRecord(record);

		// Put record into the DeliveryStream
		firehoseClient.putRecord(putRecordRequest);

	}

	/**
	 * Method to put records in the specified delivery stream by reading contents
	 * from sample input file using PutRecordBatch API.
	 *
	 * @throws IOException
	 */
	protected static void putRecordBatchIntoDeliveryStream(InputStream inputStream) throws IOException {

		List<Record> recordList = new ArrayList<Record>();
		int batchSize = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String data = line + "\n";
				Record record = createRecord(data);
				recordList.add(record);
				batchSize++;

				if (batchSize == BATCH_PUT_MAX_SIZE) {
					putRecordBatch(recordList);

					recordList.clear();
					batchSize = 0;
				}
			}

			if (batchSize > 0) {
				putRecordBatch(recordList);
			}
		}
	}
	
	//TODO
	protected static void deleteDeliveryStream() throws Exception {

		DeleteDeliveryStreamRequest deleteStreamRequest = new DeleteDeliveryStreamRequest();
		deleteStreamRequest = deleteStreamRequest.withDeliveryStreamName(s3ObjectPrefix);
		firehoseClient.deleteDeliveryStream(deleteStreamRequest);
	}

	/**
	 * Method to create the IAM role.
	 *
	 * @param s3Prefix
	 *            the s3Prefix to be specified in role policy (only when KMS key ARN
	 *            is specified)
	 * @return the role ARN
	 * @throws InterruptedException
	 */
	protected static String createIamRole(String s3Prefix) throws InterruptedException {
		String roleARN = iamClient.getRole(new GetRoleRequest().withRoleName(iamRoleName)).getRole().getArn();
		return roleARN;
	}

	/**
	 * Method to wait until the delivery stream becomes active.
	 *
	 * @param deliveryStreamName
	 *            the delivery stream
	 * @throws Exception
	 */
	protected static void waitForDeliveryStreamToBecomeAvailable(String deliveryStreamName) throws Exception {

		LOG.info("Waiting for " + deliveryStreamName + " to become ACTIVE...");

		long startTime = System.currentTimeMillis();
		long endTime = startTime + (10 * 60 * 1000);
		while (System.currentTimeMillis() < endTime) {
			try {
				Thread.sleep(1000 * 20);
			} catch (InterruptedException e) {
				// Ignore interruption (doesn't impact deliveryStream creation)
			}

			DeliveryStreamDescription deliveryStreamDescription = describeDeliveryStream(deliveryStreamName);
			String deliveryStreamStatus = deliveryStreamDescription.getDeliveryStreamStatus();
			LOG.info("  - current state: " + deliveryStreamStatus);
			if (deliveryStreamStatus.equals("ACTIVE")) {
				return;
			}
		}

		throw new AmazonServiceException("DeliveryStream " + deliveryStreamName + " never went active");
	}

	/**
	 * Method to describe the delivery stream.
	 *
	 * @param deliveryStreamName
	 *            the delivery stream
	 * @return the delivery description
	 */
	protected static DeliveryStreamDescription describeDeliveryStream(String deliveryStreamName) {
		DescribeDeliveryStreamRequest describeDeliveryStreamRequest = new DescribeDeliveryStreamRequest();
		describeDeliveryStreamRequest.withDeliveryStreamName(deliveryStreamName);
		DescribeDeliveryStreamResult describeDeliveryStreamResponse = firehoseClient
				.describeDeliveryStream(describeDeliveryStreamRequest);
		return describeDeliveryStreamResponse.getDeliveryStreamDescription();
	}

	/**
	 * Method to wait for the specified buffering interval seconds so that data will
	 * be delivered to corresponding destination.
	 *
	 * @param waitTimeSecs
	 *            the buffering interval seconds to wait upon
	 * @throws InterruptedException
	 */
	protected static void waitForDataDelivery(int waitTimeSecs) throws InterruptedException {
		LOG.info("Since the Buffering Hints IntervalInSeconds parameter is specified as: " + waitTimeSecs
				+ " seconds. Waiting for " + waitTimeSecs + " seconds for the data to be written to S3 bucket");
		TimeUnit.SECONDS.sleep(waitTimeSecs);

		LOG.info("Data delivery to S3 bucket " + s3BucketName + " is complete");
	}

	/**
	 * Method to return the bucket ARN.
	 *
	 * @param bucketName
	 *            the bucket name to be formulated as ARN
	 * @return the bucket ARN
	 * @throws IllegalArgumentException
	 */
	protected static String getBucketARN(String bucketName) throws IllegalArgumentException {
		return new StringBuilder().append(S3_ARN_PREFIX).append(bucketName).toString();
	}

	/**
	 * Method to perform PutRecordBatch operation with the given record list.
	 *
	 * @param recordList
	 *            the collection of records
	 * @return the output of PutRecordBatch
	 */
	private static PutRecordBatchResult putRecordBatch(List<Record> recordList) {
		PutRecordBatchRequest putRecordBatchRequest = new PutRecordBatchRequest();
		putRecordBatchRequest.setDeliveryStreamName(deliveryStreamName);
		putRecordBatchRequest.setRecords(recordList);

		// Put Record Batch records. Max No.Of Records we can put in a
		// single put record batch request is 500
		return firehoseClient.putRecordBatch(putRecordBatchRequest);
	}

	/**
	 * Method to create the record object for given data.
	 *
	 * @param data
	 *            the content data
	 * @return the Record object
	 */
	private static Record createRecord(String data) {
		return new Record().withData(ByteBuffer.wrap(data.getBytes()));
	}
}
