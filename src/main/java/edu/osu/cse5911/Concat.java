package edu.osu.cse5911;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.*;

public class Concat {
	private static final Logger logger = LogManager.getLogger(AbstractAmazonKinesisFirehoseDelivery.class);
	public static void concat(String directory, int start, int total) {
		File dirOutput = new File(directory);
		// deleteDir(dirOutput);
		dirOutput.mkdirs();

		String outputPath = "" + dirOutput + "/mergedFile";
		try {
			OutputStream out = new FileOutputStream(new File(outputPath));
			byte[] buf = new byte[1024];

			for (int i = start; i <= total; i++) {
				File rootFile = new File(directory + "/transformed/" + i);
				@SuppressWarnings("resource")
				InputStream in = new FileInputStream(rootFile);
				int b = 0;
				while ((b = in.read(buf)) >= 0) {
					out.write(buf, 0, b);
					out.flush();
				}
				// Skip ahead in the input to the opening document element
				// System.out.println("RootFile " + rootFile);
			}
			out.close();

		} catch (Exception e) {
			logger.error("Error during concatination: ", e);
			throw new RuntimeException(e);
		}

		logger.info("Done Concatenating.");
		logger.info("Merged File: " + outputPath);
	}
}