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
	private static Logger logger;

	@Override
	public void init() throws ServletException {
		logger = LogManager.getLogger(ProxyServletMaven.class);
		endpoint = getInitParameter("endpoint");
		page = getInitParameter("page");
		totalPages = getInitParameter("totalPages");
		xslt = getInitParameter("xslt");
		bucketName = getInitParameter("bucketName");
		logger.info("Done init");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String session = request.getSession().getId();
		if (session.length() > 10) {
			session = session.substring(0, 10);
		}
		logger.info("Entering the application with session " + session);
		Document doc = parse(request.getInputStream());
		Thread myThread = new ProxyServletThread(doc, session);
		myThread.start();
		response.getOutputStream().write(session.getBytes());
	}

	private void iteration(int start, int total, String session, Document is) {
		for (int i = start + 1; i <= total; i++) {
			logger.info("Page  " + i + ":");
			getNode(page, is).setTextContent(Integer.toString(i));
			InputStream response = connect(is);
			String content;
			try {
				content = Transformation.transformInMemory(response, getServletContext().getResourceAsStream(xslt));
			} catch (Exception e) {
				logger.error("Error during transformation", e);
				throw new RuntimeException(e);
			}
			try {
				PushToFirehose.push(content);
			} catch (Exception e) {
				logger.error("Firehose error", e);
			}
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

	private InputStream transformDocToInputStream(Document id) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(id);
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
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
			String content;
			logger.info("Page  " + start + ":");
			try {
				InputStream is = transformDocToInputStream(remoteDoc);
				content = Transformation.transformInMemory(is, getServletContext().getResourceAsStream(xslt));
			} catch (Exception e) {
				logger.error("Error during transformation", e);
				throw new RuntimeException(e);
			}

			try {
				PushToFirehose.init("us-east-1", bucketName, session, "us-east-1", "firehose_delivery_role",
						"us-east-1");
			} catch (Exception e) {
				logger.error("Error while creating the delivery stream", e);
			}
			PushToFirehose.push(content);

			iteration(start, total, session, doc);

			logger.info("Sleep for ");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				logger.error("Error trying to sleep", e);
			}
			logger.info("Done wait");

			try {
				AbstractAmazonKinesisFirehoseDelivery.deleteDeliveryStream();
			} catch (Exception e) {
				logger.error("Error while deleting the delivery stream", e);
			}
		}
	}
}
