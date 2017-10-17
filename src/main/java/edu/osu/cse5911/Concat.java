package edu.osu.cse5911;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Concat {
	public static void concat(String directory, int start, int total) throws Throwable {
		File dirOutput = new File(directory);
		// deleteDir(dirOutput);
		dirOutput.mkdirs();

		String outputPath = "" + dirOutput + "/mergedFile";
		OutputStream out = new FileOutputStream(new File(outputPath));
		byte[] buf = new byte[40];
		
		for (int i = start; i <= total; i++) {
			File rootFile = new File(directory + "/transformed/" + i);
			@SuppressWarnings("resource")
			InputStream in = new FileInputStream(rootFile);
			int b = 0;
	        while ( (b = in.read(buf)) >= 0) {
	            out.write(buf, 0, b);
	            out.flush();
	        }
			// Skip ahead in the input to the opening document element
			System.out.println("RootFile " + rootFile);
		}

		System.out.println("Done Concatenating.");
		System.out.println("Merged File: " + outputPath);
		out.close();
	}
}