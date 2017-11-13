package edu.osu.cse5911;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.*;

public class TransformationConcat {
	private static final Logger logger = LogManager.getLogger( TransformationConcat.class);
	private TransformerFactory factory;
	private Transformer transformer;
	StreamResult out;
	
	public  TransformationConcat (String directory, InputStream xslt) {
		factory = TransformerFactory.newInstance();
		new File(directory).mkdir();
		String outputPath = directory + "/mergedFile";
		try {
			out = new StreamResult(new FileWriter(new File(outputPath), true));
		} catch (IOException e1) {
			logger.error("Error during concatination: ", e1);
			throw new RuntimeException(e1);
		}
		Source xsltSource = new StreamSource(xslt);
		try {
			transformer = factory.newTransformer(xsltSource);
		} catch (TransformerConfigurationException e) {
			logger.error("Error setting up the xlst transformation: ", e);
			throw new RuntimeException(e);
		}
	}
	
	public synchronized void transform(int i, InputStream is) {
		logger.info("Transforme Page " + i);
		try {
			transformer.reset();
			Source text = new StreamSource(is);
			transformer.transform(text, out);
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