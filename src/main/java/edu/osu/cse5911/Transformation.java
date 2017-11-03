package edu.osu.cse5911;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;

import org.apache.logging.log4j.*;

public class Transformation {
	private static final Logger logger = LogManager.getLogger(Transformation.class);
	
	public static void transform(String directory, int start, int total, InputStream xslt) {
		logger.info("Transforming...");
		File dirTransformed = new File(directory + "/transformed");
		dirTransformed.mkdirs();

		int count = start;
		try {
		while (count <= total) {
			xslt.reset();
			File rootFile = new File(directory + "/" + count);
			TransformerFactory factory = TransformerFactory.newInstance();
			Source xsltSource = new StreamSource(xslt);
			Transformer transformer = factory.newTransformer(xsltSource);
			Source text = new StreamSource(rootFile);
			String filePath = "" + dirTransformed + "/" + count;

			transformer.transform(text, new StreamResult(new File(filePath)));
			count++;			
		}
		} catch (Exception e) {
			logger.error("Error during transformation: ", e);
			throw new RuntimeException(e);
		}

		logger.info("Transforming complete");
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