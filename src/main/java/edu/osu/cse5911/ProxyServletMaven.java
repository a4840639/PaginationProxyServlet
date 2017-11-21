package edu.osu.cse5911;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
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
	private static URL url;
	private static Logger logger = LogManager.getLogger(ProxyServletMaven.class);
	// msec per sec
	final int MPS = 1000;
	
	final String pageRequest = "paginlation:Request";
	final String soapEnvelope = "soapenv:Envelope";
	// Executor for each pagination thread
	ExecutorService executor;
	// Executor for the main program
	ExecutorService executorMain;
	// Maximum tries for connecting the end point
	final int maxTries = 10;
	// Maximum number of concurrent sessions
	final int sessionPoolSize = 16;
	// Maximum number of concurrent page threads
	final int pagePoolSize = 128;
	// Prefix to the S3 file
	final String s3Prefix = "merged/";

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
		
		executor = Executors.newFixedThreadPool(pagePoolSize);
		executorMain = Executors.newFixedThreadPool(sessionPoolSize);
		tempdir = ((File) getServletContext().getAttribute(ServletContext.TEMPDIR)).getPath();
		logger.info("Working directory: " + tempdir);

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
		executorMain.submit(new ProxyServletRunnable(doc, session));
		response.getOutputStream().write(session.getBytes());

	}
	
	private Document createDocumentFromNode(Node node) throws ParserConfigurationException {
		Document newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		newDocument.appendChild(newDocument.importNode(node, true));
		return newDocument;
	}

	void iterationMT(int start, int total, Document is, Concat concat) throws ParserConfigurationException, InterruptedException {
		logger.info("Requesting pages...");
		logger.info("Requesting pages...");
		List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
		for (int i = start; i <= total; i++) {
			Document doc = createDocumentFromNode(is.getDocumentElement());
			callables.add(Executors.callable(new IterationRunnable(doc, i, concat), null));
		}
		executor.invokeAll(callables);
		logger.info("All pages complete");
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

	public class ProxyServletRunnable implements Runnable {
		Document doc;
		String session;

		public ProxyServletRunnable(Document in_doc, String in_session) {
			doc = in_doc;
			session = in_session;
		}

		public void run() {
			long startTime = System.currentTimeMillis();
			String directory = tempdir + "/" + session;
			int start = Integer.parseInt(getNode(page, doc).getTextContent());
			logger.info("Page " + start);

			InputStream remoteResponse = connect(doc);
			Document remoteDoc = parse(remoteResponse);
			int total = Integer.parseInt(getNode(totalPages, remoteDoc).getTextContent());

			remoteResponse = transformDocToInputStream(remoteDoc);
			Transformation.setTemplates(getServletContext().getResourceAsStream(xslt));
			String pageStr = Transformation.transformInMemory(remoteResponse);
			Concat concat = new Concat(directory);
			concat.concat(pageStr);
			try {
				iterationMT(start + 1, total, doc, concat);
			} catch (ParserConfigurationException e) {
				logger.info("Error creating DOM documents", e);
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				logger.info("Error joining threads", e);
				throw new RuntimeException(e);
			}
			concat.close();
			PushToS3.push(directory + "/mergedFile", bucketName, "merged/" + session, s3RegionName);
			logger.info("Deleting the working directory");
			Transformation.deleteDir(new File(directory));
			long totalTime = System.currentTimeMillis() - startTime;
			logger.info("Session " + session + " complete");
			logger.info("Time taken: " + totalTime / MPS + "." + totalTime % MPS + "s");
		}
	}

	public class IterationRunnable implements Runnable {
		Document doc;
		int i;
		Concat concat;

		public IterationRunnable(Document in_doc, int in_i, Concat in_concat) {
			doc = in_doc;
			i = in_i;
			concat = in_concat;
		}

		public void run() {
			logger.info("Requesting Page " + i);
			getNode(page, doc).setTextContent(Integer.toString(i));
			InputStream response = connect(doc);
			String pageStr = Transformation.transformInMemory(response);
			concat.concat(pageStr);
			logger.info("Page " + i + " complete");
		}
	}

}
