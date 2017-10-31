package edu.osu.cse5911;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;

import org.apache.logging.log4j.*;
import org.w3c.dom.Document;

public class Transformation {
	private static final Logger logger = LogManager.getLogger(AbstractAmazonKinesisFirehoseDelivery.class);
	
	public static String transformInMemory(InputStream is, URI xsltURI) {
		logger.info("Start Transforming");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult( writer );
		Source s = new StreamSource(is);
		
		try {
			TransformerFactory factory = TransformerFactory.newInstance();		
			Source xslt = new StreamSource(new File(xsltURI));
			Transformer transformer = factory.newTransformer(xslt);
//			System.out.println("rootFile is" + rootFile);
//			System.out.println("filePath is" + filePath);

			transformer.transform(s, result);	
		} catch (Exception e) {
			logger.error("Error during transformation: ", e);
			throw new RuntimeException(e);
		}

		logger.info("Done Transforming");
		return writer.toString();
	}
	
	public static String transformInMemory(Document id, URI xsltURI) {
		logger.info("Start Transforming");
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult( writer );
		Source s = new DOMSource(id);
		
		try {
			TransformerFactory factory = TransformerFactory.newInstance();		
			Source xslt = new StreamSource(new File(xsltURI));
			Transformer transformer = factory.newTransformer(xslt);
//			System.out.println("rootFile is" + rootFile);
//			System.out.println("filePath is" + filePath);

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