package edu.osu.cse5911;

import java.io.*;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.apache.logging.log4j.*;

/**
 * Servlet implementation class ProxyServlet
 */
public class ProxyServletMaven extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static String endpoint;
	private static String page;
	private static String totalPages;
	private static String xslt;
	private static String bucketName;
	private static String s3RegionName;
	private static String tempdir;
	private static Logger logger = LogManager.getLogger(ProxyServletMaven.class);

	@Override
	public void init() throws ServletException {
		logger.info("Initializing...");
		endpoint = getInitParameter("endpoint");
		page = getInitParameter("page");
		totalPages = getInitParameter("totalPages");
		xslt = getInitParameter("xslt");
		bucketName = getInitParameter("bucketName");
		s3RegionName = getInitParameter("s3RegionName");

		logger.info("Endpoint : " + endpoint);
		logger.info("Page number attribute : " + page);
		logger.info("Total page number attribute : " + totalPages);
		logger.info("Relative path to the XSLT file : " + xslt);
		logger.info("S3 bucket : " + bucketName);
		logger.info("S3 region : " + s3RegionName);

		logger.info("Initializtion complete");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String session = request.getSession().getId();
		if (session.length() > 32) {
			session = session.substring(0, 31);
		}
		logger.info("Starting the session : " + session);
		logger.info("Multi-threaded access");
		Document doc = parse(request.getInputStream());
		Thread myThread = new ProxyServletThread(doc, session);
		myThread.start();
		response.getOutputStream().write(session.getBytes());

	}

	void iterationMT(int start, int total, String session, Document is) throws ParserConfigurationException, InterruptedException {
		logger.info("Requesting pages...");
		Thread[] myThreads = new Thread[total - start];
		for (int i = start + 1; i <= total; i++) {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			doc.appendChild(doc.importNode(is.getDocumentElement(), true));
			
			myThreads[i - start - 1] = new IterationThread(doc, session, i);
			myThreads[i - start - 1].start();
		}
		for (int i = start + 1; i <= total; i++) {
			myThreads[i - start - 1].join();
		}
		logger.info("All pages complete");
	}

	Document parse(InputStream is) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
		} catch (Exception e) {
			logger.error("Error parsing stream", e);
			throw new RuntimeException(e);
		}
		return doc;
	}

	void writeDocument(InputStream is, String dict, String file) {
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		new File(dict).mkdirs();
		File outputFile = new File(dict + "/" + file);
		FileWriter fout;
		try {
			fout = new FileWriter(outputFile);
			while ((line = rd.readLine()) != null) {
				fout.write(line);
			}
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error writing document", e);
			throw new RuntimeException(e);
		}

	}

	void writeDocument(Document doc, String dict, String file) {
		new File(dict).mkdirs();
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(new File(dict + "/" + file));
			Source input = new DOMSource(doc);
			transformer.transform(input, output);
		} catch (Exception e) {
			logger.error("Error writing document", e);
			throw new RuntimeException(e);
		}
	}

	void sendDocument(Document doc, OutputStream os) {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(os);
			Source input = new DOMSource(doc);

			transformer.transform(input, output);
		} catch (Exception e) {
			logger.error("Error sending document", e);
			throw new RuntimeException(e);
		}
	}

	Node getNode(String xpath, Document doc) {
		return doc.getElementsByTagName(xpath).item(0);

	}

	private InputStream connect(Document doc) {
		URL url;
		URLConnection con;
		InputStream is;
		try {
			url = new URL(endpoint);
			con = url.openConnection();

			con.setRequestProperty("SOAPAction", endpoint);
			con.setDoOutput(true);

			sendDocument(doc, con.getOutputStream());

			is = con.getInputStream();
		} catch (Exception e) {
			logger.error("Error connnecting to the endpoint", e);
			throw new RuntimeException(e);
		}
		return is;
	}

	public class ProxyServletThread extends Thread {
		Document doc;
		String session;

		public ProxyServletThread(Document in_doc, String in_session) {
			doc = in_doc;
			session = in_session;
		}

		public void run() {
			String directory = tempdir + "/" + session;
			int start = Integer.parseInt(getNode(page, doc).getTextContent());
			logger.info("Page " + start);

			InputStream remoteResponse = connect(doc);
			Document remoteDoc = parse(remoteResponse);
			int total = Integer.parseInt(getNode(totalPages, remoteDoc).getTextContent());

			writeDocument(remoteDoc, directory, "/" + start);
			try {
				iterationMT(start, total, directory, doc);
			} catch (ParserConfigurationException e) {
				logger.info("Error creating DOM documents", e);
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				logger.info("Error joining threads", e);
				throw new RuntimeException(e);
			}
			Transformation.transform(directory, start, total, getServletContext().getResourceAsStream(xslt));
			Concat.concat(directory, start, total);
			PushToS3.push(directory + "/mergedFile", bucketName, "merged/" + session, s3RegionName);
			logger.info("Deleting the working directory");
			Transformation.deleteDir(new File(directory));
			logger.info("Session complete");
		}
	}

	public class IterationThread extends Thread {
		String session;
		Document doc;
		int i;

		public IterationThread(Document in_doc, String in_session, int in_i) {
			doc = in_doc;
			session = in_session;
			i = in_i;
		}

		public void run() {
			logger.info("Requesting Page " + i);
			getNode(page, doc).setTextContent(Integer.toString(i));
			InputStream response = connect(doc);
			writeDocument(response, session, Integer.toString(i));
			logger.info("Page " + i + " complete");
		}
	}

}
