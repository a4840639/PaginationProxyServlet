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
	
	public static String transformInMemory(InputStream is, InputStream xslt) {
		logger.info("Start Transforming");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult( writer );
		Source s = new StreamSource(is);
		
		try {
			xslt.reset();
			TransformerFactory factory = TransformerFactory.newInstance();		
			Source xsltSource = new StreamSource(xslt);
			Transformer transformer = factory.newTransformer(xsltSource);

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