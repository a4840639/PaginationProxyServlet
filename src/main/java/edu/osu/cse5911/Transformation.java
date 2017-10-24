package edu.osu.cse5911;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.net.URI;

import org.apache.logging.log4j.*;

public class Transformation {
	public static void transform(String directory, int start, int total, URI xsltURI, Logger logger) {
		logger.info("Start Transforming");
		File dirTransformed = new File(directory + "/transformed");
		deleteDir(dirTransformed);
		dirTransformed.mkdirs();
//		File dir = new File(directory);
//		File[] rootFiles = dir.listFiles();
		
		int count = start;
		try {
		while (count <= total) {
			File rootFile = new File(directory + "/" + count);
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xslt = new StreamSource(new File(xsltURI));
			Transformer transformer = factory.newTransformer(xslt);
			Source text = new StreamSource(rootFile);
			String filePath = "" + dirTransformed + "/" + count;
//			System.out.println("rootFile is" + rootFile);
//			System.out.println("filePath is" + filePath);

			transformer.transform(text, new StreamResult(new File(filePath)));
			count++;			
		}
		} catch (Exception e) {
			logger.error("Error during transformation: ", e);
			throw new RuntimeException(e);
		}

		logger.info("Done Transforming");
	}

	static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

}