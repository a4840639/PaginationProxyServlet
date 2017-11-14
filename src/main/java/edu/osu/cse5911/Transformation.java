package edu.osu.cse5911;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;

import org.apache.logging.log4j.*;

public class Transformation {
	private static final Logger logger = LogManager.getLogger(Transformation.class);
	private static TransformerFactory factory = TransformerFactory.newInstance();
	private Templates templates;
	private String directory;
	
	public Transformation(InputStream xslt, String in_directory) {
		Source xsltSource = new StreamSource(xslt);
		directory = in_directory;
		new File(directory).mkdir();
		try {
			templates = factory.newTemplates(xsltSource);
		} catch (TransformerConfigurationException e) {
			logger.error("Error setting up the xlst transformation: ", e);
			throw new RuntimeException(e);
		}
	}
	
	public void transform(int i, InputStream is) {
		logger.info("Transforme Page " + i);
		try {
			Source text = new StreamSource(is);
			String filePath = directory + "/" + i;
			templates.newTransformer().transform(text, new StreamResult(new File(filePath)));
		} catch (Exception e) {
			logger.error("Error during transformation: ", e);
			throw new RuntimeException(e);
		}
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