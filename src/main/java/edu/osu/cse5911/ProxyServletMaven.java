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
	// XML attributes to read
	private static String page;
	private static String totalPages;
	// Path to the XSLT file
	private static String xslt;

	private static String bucketName;
	private static String s3RegionName;
	private static String firehoseRegion;
	private static String iamRoleName;
	private static String iamRegion;

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
		firehoseRegion = getInitParameter("firehoseRegion");
		iamRoleName = getInitParameter("iamRoleName");
		iamRegion = getInitParameter("iamRegion");

		logger.info("Endpoint : " + endpoint);
		logger.info("Page number attribute : " + page);
		logger.info("Total page number attribute : " + totalPages);
		logger.info("Relative path to the XSLT file : " + xslt);
		logger.info("S3 bucket : " + bucketName);
		logger.info("S3 region : " + s3RegionName);
		logger.info("Firehose region : " + firehoseRegion);
		logger.info("IAM role name : " + iamRoleName);
		logger.info("IAM region : " + iamRegion);

		PushToFirehose.init(s3RegionName, bucketName, firehoseRegion, iamRoleName, iamRegion);

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

	private void iteration(int start, int total, String session, Document is) {
		for (int i = start + 1; i <= total; i++) {
			logger.info("Page  " + i + " :");
			getNode(page, is).setTextContent(Integer.toString(i));
			InputStream response = connect(is);
			String content = Transformation.transformInMemory(response, getServletContext().getResourceAsStream(xslt));
			PushToFirehose.push(content, session);
		}
	}

	private Document parse(InputStream is) {
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

	private void sendDocument(Document doc, OutputStream os) {
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

	private Node getNode(String xpath, Document doc) {
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

	private int getS3DestinationIntervalInSeconds(int pages) {
		return pages < 20 ? 60 : pages * 3;
	}

	public class ProxyServletThread extends Thread {
		Document doc;
		String session;

		public ProxyServletThread(Document in_doc, String in_session) {
			doc = in_doc;
			session = in_session;
		}

		public void run() {
			InputStream remoteResponse = connect(doc);
			Document remoteDoc = parse(remoteResponse);
			int start = Integer.parseInt(getNode(page, doc).getTextContent());
			int total = Integer.parseInt(getNode(totalPages, remoteDoc).getTextContent());

			// create delivery stream
			int s3DestinationIntervalInSeconds = getS3DestinationIntervalInSeconds(total - start + 1);
			PushToFirehose.createDeliveryStreamHelper(session, s3DestinationIntervalInSeconds);
			
			logger.info("Page " + start + " :");
			InputStream is = transformDocToInputStream(remoteDoc);
			String content = Transformation.transformInMemory(is, getServletContext().getResourceAsStream(xslt));
			PushToFirehose.push(content, session);
			
			iteration(start, total, session, doc);

			logger.info("Wait " + s3DestinationIntervalInSeconds + "s to make the stream deliverd");
			try {
				Thread.sleep(1000 * s3DestinationIntervalInSeconds);
			} catch (InterruptedException e) {
				logger.error("Error trying to wait", e);
			}
			AbstractAmazonKinesisFirehoseDelivery.deleteDeliveryStream(session);
			logger.info("Session complete");
		}
	}
}
