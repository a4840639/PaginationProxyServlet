package edu.osu.cse5911;

import java.io.*;
import java.net.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
//import javax.xml.xpath.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

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
	private static String tempdir;
	private static Logger logger;

	@Override
	public void init() throws ServletException {
		logger = LogManager.getLogger(ProxyServletMaven.class);
		endpoint = getInitParameter("endpoint");
		page = getInitParameter("page");
		totalPages = getInitParameter("totalPages");
		xslt = getInitParameter("xslt");
		bucketName = getInitParameter("bucketName");
		tempdir = ((File) getServletContext().getAttribute(ServletContext.TEMPDIR)).getPath();
		logger.info("Done init");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("Enterring application");
		String session = request.getSession().getId();
		String directory = tempdir + "/" + session;
		Document doc = parse(request.getInputStream());
		InputStream remoteResponse = connect(doc);
		Document remoteDoc = parse(remoteResponse);
		int start = Integer.parseInt(getNode(page, doc).getTextContent());
		int total = Integer.parseInt(getNode(totalPages, remoteDoc).getTextContent());
		writeDocument(remoteDoc, directory, "/" + start);
		iteration(start, total, directory, doc);

		// System.out.println(getServletContext().getResource("/"));
		try {
			Transformation.transform(directory, start, total, getServletContext().getResource(xslt).toURI(), logger);
		} catch (Exception e) {
			logger.error("Wrong xslt URI", e);
			throw new RuntimeException(e);
		}
		Concat.concat(directory, start, total, logger);
		PushToS3.push(directory + "/mergedFile", bucketName, "merged/" + session, logger);
		Transformation.deleteDir(new File(directory));

	}

	void iteration(int start, int total, String session, Document is) throws IOException {
		for (int i = start + 1; i <= total; i++) {
			getNode(page, is).setTextContent(Integer.toString(i));
			InputStream response = connect(is);
			writeDocument(response, session, Integer.toString(i));
		}
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
		// // Evaluate XPath against Document itself
		// XPath xPath = XPathFactory.newInstance().newXPath();
		// NodeList nodes = null;
		// try {
		// nodes = (NodeList) xPath.evaluate(xpath, doc.getDocumentElement(),
		// XPathConstants.NODESET);
		// } catch (XPathExpressionException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// System.err.println("Error finding node");
		// }
		// return nodes.item(0);
		return doc.getElementsByTagName(xpath).item(0);

	}

	InputStream connect(Document doc) throws IOException {
		URL url = new URL(endpoint);
		URLConnection con = url.openConnection();

		con.setRequestProperty("SOAPAction", endpoint);
		con.setDoOutput(true);

		sendDocument(doc, con.getOutputStream());

		return con.getInputStream();
	}

}
