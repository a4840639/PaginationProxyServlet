package edu.osu.cse5911;

import java.io.*;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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

/**
 * Servlet implementation class ProxyServlet
 */
@WebServlet("/ProxyServlet")
public class ProxyServletMaven extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final String ep = "http://127.0.0.1:8080";
	final String page = "bsvc:Page";
	final String totalPages = "wd:Total_Pages";
	final String xslt = "DW_WD_HR_GetWorkers_All.xsl";
	private static String bucketName = "wso2mystreammykinase";

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String session = request.getSession().getId();
		String directory = getServletContext().getRealPath("/") + "/" + session;
		Document doc = parse(request.getInputStream());
		InputStream remoteResponse = connect(doc);
		Document remoteDoc = parse(remoteResponse);
		int start = Integer.parseInt(getNode(page, doc).getTextContent());
		int total = Integer.parseInt(getNode(totalPages, remoteDoc).getTextContent());
		writeDocument(remoteDoc, directory, "/" + start);
		iteration(start, total, directory, doc);

		System.out.println(getServletContext().getRealPath("/"));
		try {
			Transformation.transform(directory, start, total, getServletContext().getRealPath("/") + "/" + xslt);
			Concat.concat(directory, start, total);
			PushToS3.push(directory + "/mergedFile", bucketName, session);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void iteration(int start, int total, String session, Document is) throws IOException {
		for (int i = start; i <= total; i++) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error parsing stream");
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
			System.err.println("Error writing document");
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
			e.printStackTrace();
			System.err.println("Error writing document");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error sending document");
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
		URL url = new URL(ep);
		URLConnection con = url.openConnection();

		con.setRequestProperty("SOAPAction", ep);
		con.setDoOutput(true);

		sendDocument(doc, con.getOutputStream());

		return con.getInputStream();
	}

}
