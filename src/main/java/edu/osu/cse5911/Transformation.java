package edu.osu.cse5911;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.logging.log4j.*;

public class Transformation {
	private static final Logger logger = LogManager.getLogger(Transformation.class);
	private static TransformerFactory factory = TransformerFactory.newInstance();
	private static Transformer transformer;
	
	public static void setTransformer(InputStream xslt) {
		Source xsltSource = new StreamSource(xslt);
		try {
			transformer = factory.newTransformer(xsltSource);
		} catch (TransformerConfigurationException e) {
			logger.error("Error setting up the xlst transformation: ", e);
			throw new RuntimeException(e);
		}
	}
	
	public static String transformInMemory(InputStream is) {
		logger.info("Start Transforming");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult( writer );
		Source s = new StreamSource(is);
		
		try {
			transformer.reset();
			transformer.transform(s, result);	
		} catch (Exception e) {
			logger.error("Error during transformation: ", e);
			throw new RuntimeException(e);
		}

		logger.info("Done Transforming");
		return writer.toString();
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