package com.samteladze.vzradio.android.common;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import android.util.Log;

public class XMLParser {

	// constructor
	public XMLParser() {

	}

	/**
	 * Getting XML from URL making HTTP request
	 * 
	 * @param url
	 *            string
	 * */
	public String getXmlFromUrl(String url) {
		String xml = null;

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return xml;
	}

	/**
	 * Getting XML DOM element
	 * 
	 * @param XML
	 *            string
	 * */
	public Document getDomElement(String xml) {
		Document document = null;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		try {

			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();

			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xml));
			document = documentBuilder.parse(inputSource);

		} catch (Exception e) {
			Log.e("error: ", e.getMessage());
			return null;
		}

		return document;
	}

	/**
	 * Getting node value
	 * 
	 * @param element
	 *            element
	 */
	public final String getElementValue(Node element) {
		Node child;
		if (element != null) {
			if (element.hasChildNodes()) {
				for (child = element.getFirstChild(); child != null; 
					 child = child.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/**
	 * Getting node value
	 * 
	 * @param Element
	 *            node
	 * @param key
	 *            string
	 * */
	public String getValue(Element element, String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName);
		return this.getElementValue(nodeList.item(0));
	}
}