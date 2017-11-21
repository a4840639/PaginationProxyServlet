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
import org.w3c.dom.Element;
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

	private String endpoint;
	private String page;
	private String totalPages;
	private String xslt;
	private String bucketName;
	private String s3RegionName;
	private String awsKey;
	private String awsSecret;
	private URL url;
	private Transformation trans;
	private static Logger logger = LogManager.getLogger(ProxyServletMaven.class);
	private static String tempdir;
	final int MPS = 1000;
	final String pageRequest = "paginlation:Request";
	final String soapEnvelope = "soapenv:Envelope";
	// Maximum tries for connecting the end point
	final int maxTries = 10;
	// Maximum number of threads
	final int poolSize = 256;
	// Prefix to the S3 file
	final String s3Prefix = "merged/";

	@Override
	public void init() throws ServletException {
		tempdir = ((File) getServletContext().getAttribute(ServletContext.TEMPDIR)).getPath();
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

		Thread myThread = new Thread(new ProxyServletRunnable(doc, session));
		myThread.start();
		response.getOutputStream().write(session.getBytes());

	}

	void iterationMT(int start, int total, Document is) throws ParserConfigurationException, InterruptedException {
		logger.info("Requesting pages...");
		ExecutorService executor = Executors.newFixedThreadPool(poolSize);
		List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
		for (int i = start; i <= total; i++) {
			Document doc = createDocumentFromNode(is.getDocumentElement());
			callables.add(Executors.callable(new IterationRunnable(doc, i), null));
		}
		executor.invokeAll(callables);
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

	private void parseConfiguration(Document doc) {
		endpoint = getNode("paginlation:Endpoint", doc).getTextContent();
		page = getNode("paginlation:Page", doc).getTextContent();
		totalPages = getNode("paginlation:TotalPages", doc).getTextContent();
		xslt = getNode("paginlation:XSLT", doc).getTextContent();
		bucketName = getNode("paginlation:BucketName", doc).getTextContent();
		s3RegionName = getNode("paginlation:S3RegionName", doc).getTextContent();
		try {
			awsKey = getNode("paginlation:AWSKey", doc).getTextContent();
			awsSecret = getNode("paginlation:AWSSecret", doc).getTextContent();
		} catch (NullPointerException e) {
			logger.info("No external AWS credentials");
		}

		if (awsKey == "" || awsSecret == "") {
			awsKey = null;
			awsSecret = null;
			logger.info("No external AWS credentials");
		}

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
		logger.info("AWSKey : " + awsKey);
		logger.info("AWSSecret : " + awsSecret);
	}

	private Document restoreOriginalRequest(Document doc) throws ParserConfigurationException {
		Node node = ((Element) doc.getElementsByTagName(pageRequest).item(0)).getElementsByTagName(soapEnvelope)
				.item(0);
		Document newDocument = createDocumentFromNode(node);
		return newDocument;
	}

	private Document createDocumentFromNode(Node node) throws ParserConfigurationException {
		Document newDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		newDocument.appendChild(newDocument.importNode(node, true));
		return newDocument;
	}

	public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(out, "UTF-8")));
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

			parseConfiguration(doc);
			try {
				doc = restoreOriginalRequest(doc);
			} catch (ParserConfigurationException e1) {
				logger.info("Error restoring the original request", e1);
				throw new RuntimeException(e1);
			}

			String directory = tempdir + "/" + session;
			int start = Integer.parseInt(getNode(page, doc).getTextContent());
			int total = 0;
			int iterationStart = start;
			trans = new Transformation(getServletContext().getResourceAsStream(xslt), directory);
			if (getNode(totalPages, doc) == null) {
				logger.info("No total pages availble, getting the first page");
				logger.info("Requesting Page " + start);
				InputStream remoteResponse = connect(doc);
				Document remoteDoc = parse(remoteResponse);
				total = Integer.parseInt(getNode(totalPages, remoteDoc).getTextContent());
				remoteResponse = transformDocToInputStream(remoteDoc);
				trans.transform(start, remoteResponse);
				iterationStart++;
			} else {
				logger.info("Total pages availble, iterate immediately");
				total = Integer.parseInt(getNode(totalPages, doc).getTextContent());
			}

			try {
				iterationMT(iterationStart, total, doc);
			} catch (ParserConfigurationException e) {
				logger.info("Error creating DOM documents", e);
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				logger.info("Error joining threads", e);
				throw new RuntimeException(e);
			}
			Concat.concat(directory, start, total);
			PushToS3.push(directory + "/mergedFile", bucketName, s3Prefix + session, s3RegionName, awsKey, awsSecret);
			logger.info("Deleting the working directory");
			logger.info(directory);
			Transformation.deleteDir(new File(directory));
			long totalTime = System.currentTimeMillis() - startTime;
			logger.info("Session " + session + " complete");
			logger.info("Time taken: " + totalTime / MPS + "." + totalTime % MPS + "s");
		}
	}

	public class IterationRunnable implements Runnable {
		Document doc;
		int i;

		public IterationRunnable(Document in_doc, int in_i) {
			doc = in_doc;
			i = in_i;
		}

		public void run() {
			logger.info("Requesting Page " + i);
			getNode(page, doc).setTextContent(Integer.toString(i));
			InputStream response = connect(doc);
			trans.transform(i, response);
			logger.info("Page " + i + " complete");
		}
	}

}
