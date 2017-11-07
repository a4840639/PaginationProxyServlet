package edu.osu.cse5911;

import java.io.*;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
	private static URL url;
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
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			logger.error("Failed to initialize the endpoint", e);
			throw new RuntimeException(e);
		}

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
		Document doc = parse(request.getInputStream());
		Thread myThread = new ProxyServletThread(doc, session);
		myThread.start();
		response.getOutputStream().write(session.getBytes());
		
	}

	void iteration(int start, int total, String session, Document is) {
		logger.info("Requesting pages...");
		for (int i = start + 1; i <= total; i++) {
			logger.info("Page " + i);
			getNode(page, is).setTextContent(Integer.toString(i));
			InputStream response = connect(is);
			Transformation.transform(i, response);
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
		URLConnection con;
		InputStream is;
		int maxTries = 10;
		int count = 0;
		while (true) {
			try {
				con = url.openConnection();

				con.setRequestProperty("SOAPAction", endpoint);
				con.setDoOutput(true);

				sendDocument(doc, con.getOutputStream());

				is = con.getInputStream();
				return is;
			} catch (Exception e) {
				if (++count >= maxTries) {
					logger.error("Error connnecting to the endpoint", e);
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	private InputStream transformDocToInputStream(Document id) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(id);
		Result outputTarget = new StreamResult(outputStream);
		try {
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
		} catch (TransformerException | TransformerFactoryConfigurationError e) {
			logger.error("Error transforming XML to InputStream", e);
		}
		InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
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
			
			remoteResponse = transformDocToInputStream(remoteDoc);
			Transformation.newDir(directory);
			Transformation.setTemplates(getServletContext().getResourceAsStream(xslt));
			Transformation.transform(start, remoteResponse);
			
			iteration(start, total, directory, doc);
			Concat.concat(directory, start, total);
			PushToS3.push(directory + "/mergedFile", bucketName, "merged/" + session, s3RegionName);
			logger.info("Deleting the working directory");
			Transformation.deleteDir(new File(directory));
			logger.info("Session " + session + " complete");
		}
	}

}
