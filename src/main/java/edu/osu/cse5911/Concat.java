package edu.osu.cse5911;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.logging.log4j.*;

public class Concat {
	private static final Logger logger = LogManager.getLogger(Concat.class);
	
	OutputStream out;
	
	public Concat(String directory){
		new File(directory).mkdir();
		String outputPath = directory + "/mergedFile";
		try {
			out = new FileOutputStream(new File(outputPath));
		} catch (Exception e) {
			logger.error("Error during concatination: ", e);
			throw new RuntimeException(e);
		}
	}
	
	public void close(){
		try {
			out.close();
		} catch (IOException e) {
			logger.error("Error during concatination: ", e);
			throw new RuntimeException(e);
		}
	}
	
	public synchronized void concat(String str) {
		try {
			out.write(str.getBytes(Charset.forName("UTF-8")));
		} catch (IOException e) {
			logger.error("Error during concatination: ", e);
			throw new RuntimeException(e);
		}
	}
}