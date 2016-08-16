/*
 * Copyright (c) PANGAEA - Data Publisher for Earth & Environmental Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.pangaea.fixo3.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.text.WordUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.pangaea.fixo3.vocab.EYP;

/**
 * <p>
 * Title: ProcessXmlFiles
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project:
 * </p>
 * <p>
 * Copyright: PANGAEA
 * </p>
 */

public class ProcessXmlFiles {

	private final String jsonFileName = "src/main/resources/data/esonetyellowpages-devicetypes-export.json";

	private final Logger log = Logger.getLogger(ProcessXmlFiles.class.getName());
	
	private static Map<String, String> unitCodeMap = new HashMap<String, String>();
	
	// http://qudt.org/1.1/vocab/unit
	static {
		unitCodeMap.put("kg", "http://qudt.org/vocab/unit#Kilogram");
		unitCodeMap.put("Hz", "http://qudt.org/vocab/unit#Hertz");
		unitCodeMap.put("kHz", "http://qudt.org/vocab/unit#KiloHertz");
		unitCodeMap.put("m", "http://qudt.org/vocab/unit#Meter");
		unitCodeMap.put("mm", "http://qudt.org/vocab/unit#Millimeter");
		unitCodeMap.put("nm", "http://qudt.org/vocab/unit#Nanometer");
		unitCodeMap.put("m/s", "http://qudt.org/vocab/unit#MeterPerSecond");
		unitCodeMap.put("mm/s", "http://qudt.org/vocab/unit#MetermeterPerSecond");
		unitCodeMap.put("ºC", "http://qudt.org/vocab/unit#DegreeCelsius");
		unitCodeMap.put("°C", "http://qudt.org/vocab/unit#DegreeCelsius");
		unitCodeMap.put("knots", "http://qudt.org/vocab/unit#Knot");
		unitCodeMap.put("Watts", "http://qudt.org/vocab/unit#Watt");
		unitCodeMap.put("bar", "http://qudt.org/vocab/unit#Bar");
		unitCodeMap.put("sec", "http://qudt.org/vocab/unit#SecondTime");
		unitCodeMap.put("s", "http://qudt.org/vocab/unit#SecondTime");
	}

	private void run() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException,
			XPathExpressionException {
		// src/test/resources/, src/main/resources/files
		File folder = new File("src/main/resources/files");
		File[] files = folder.listFiles();

		JsonArrayBuilder json = Json.createArrayBuilder();

		Document doc;
		String deviceLabel, deviceComment, deviceImage;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		final Map<String, String> prefixToNS = new HashMap<>();
		prefixToNS.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
		prefixToNS.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
		prefixToNS.put("sml", "http://www.opengis.net/sensorml/2.0");
		prefixToNS.put("gml", "http://www.opengis.net/gml/3.2");
		prefixToNS.put("gmd", "http://www.isotc211.org/2005/gmd");

		XPath x = XPathFactory.newInstance().newXPath();
		x.setNamespaceContext(new NamespaceContext() {
			@Override
			public String getNamespaceURI(String prefix) {
				Objects.requireNonNull(prefix, "Namespace prefix cannot be null");
				final String uri = prefixToNS.get(prefix);
				if (uri == null) {
					throw new IllegalArgumentException("Undeclared namespace prefix: " + prefix);
				}
				return uri;
			}

			@Override
			public String getPrefix(String namespaceURI) {
				throw new UnsupportedOperationException();
			}

			@Override
			public Iterator<?> getPrefixes(String namespaceURI) {
				throw new UnsupportedOperationException();
			}
		});

		XPathExpression exGmlName = x.compile("/sml:PhysicalSystem/gml:name");
		XPathExpression exGmlDescription = x.compile("/sml:PhysicalSystem/gml:description");
		XPathExpression exGmdUrl = x.compile(
				"/sml:PhysicalSystem/sml:documentation/sml:DocumentList/sml:document/gmd:CI_OnlineResource/gmd:linkage/gmd:URL");
		XPathExpression exTerm = x
				.compile("/sml:PhysicalSystem/sml:classification/sml:ClassifierList/sml:classifier/sml:Term");

		for (File file : files) {
			log.info(file.getName());
			doc = dbf.newDocumentBuilder().parse(new FileInputStream(file));

			deviceLabel = exGmlName.evaluate(doc).trim();
			deviceComment = exGmlDescription.evaluate(doc).trim();
			deviceImage = exGmdUrl.evaluate(doc).trim();
			NodeList terms = (NodeList) exTerm.evaluate(doc, XPathConstants.NODESET);

			JsonObjectBuilder jobDevice = Json.createObjectBuilder();

			jobDevice.add("name", toUri(deviceLabel));
			jobDevice.add("label", deviceLabel);
			jobDevice.add("comment", toAscii(deviceComment));
			jobDevice.add("image", deviceImage);

			JsonArrayBuilder jabSubClasses = Json.createArrayBuilder();

			for (int i = 0; i < terms.getLength(); i++) {
				Node term = terms.item(i);
				NodeList attributes = term.getChildNodes();

				String attributeLabel = null;
				String attributeValue = null;

				for (int j = 0; j < attributes.getLength(); j++) {
					Node attribute = attributes.item(j);
					String attributeName = attribute.getNodeName();

					if (attributeName.equals("sml:label")) {
						attributeLabel = attribute.getTextContent();
					} else if (attributeName.equals("sml:value")) {
						attributeValue = attribute.getTextContent();
					}
				}

				if (attributeLabel == null || attributeValue == null) {
					throw new RuntimeException("Attribute label or value cannot be null [attributeLabel = "
							+ attributeLabel + "; attributeValue = " + attributeValue + "]");
				}

				if (attributeLabel.equals("model")) {
					continue;
				}

				if (attributeLabel.equals("manufacturer")) {
					jobDevice.add("manufacturer", attributeValue);
					continue;
				}

				Quantity quantity = getQuantity(attributeValue);

				if (quantity == null) {
					continue;
				}

				JsonObjectBuilder jobSubClass = Json.createObjectBuilder();
				JsonObjectBuilder jobCapability = Json.createObjectBuilder();
				JsonObjectBuilder jobQuantity = Json.createObjectBuilder();

				String quantityLabel = getQuantityLabel(attributeLabel);
				String capabilityLabel = deviceLabel + " " + quantityLabel;
				
				jobCapability.add("label", capabilityLabel);

				jobQuantity.add("label", quantity.getLabel());

				if (quantity.getValue() != null) {
					jobQuantity.add("value", quantity.getValue());
				} else if (quantity.getMinValue() != null && quantity.getMaxValue() != null) {
					jobQuantity.add("minValue", quantity.getMinValue());
					jobQuantity.add("maxValue", quantity.getMaxValue());
				} else {
					throw new RuntimeException(
							"Failed to determine quantity value [attributeValue = " + attributeValue + "]");
				}
				
				jobQuantity.add("unitCode", quantity.getUnitCode());
				jobQuantity.add("type", toUri(quantityLabel));

				jobCapability.add("quantity", jobQuantity);
				jobSubClass.add("capability", jobCapability);

				jabSubClasses.add(jobSubClass);
			}

			jobDevice.add("subClasses", jabSubClasses);

			json.add(jobDevice);
		}

		Map<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);

		JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = jsonWriterFactory.createWriter(new FileWriter(new File(jsonFileName)));
		jsonWriter.write(json.build());
		jsonWriter.close();
	}

	public static void main(String[] args) throws FileNotFoundException, SAXException, IOException,
			ParserConfigurationException, XPathExpressionException {
		ProcessXmlFiles app = new ProcessXmlFiles();
		app.run();
	}

	private String getQuantityLabel(String s) {
		s = s.replace("Max.", "Maximum");
		
		s = WordUtils.capitalize(s);
		
		return s;
	}
	
	private String toUri(String s) {
		s = s.replaceAll(" ", "");
		s = s.replaceAll("_", "");
		s = s.replaceAll("-", "");
		s = s.replaceAll("/", "");
		s = s.replaceAll("&", "");
		s = s.replaceAll("\\(", "");
		s = s.replaceAll("\\)", "");
		s = s.replaceAll("™", "");
		s = s.replaceAll("'", "");
		s = s.replaceAll("\\+", "");

		if (!Character.isUpperCase(s.codePointAt(0))) {
			s = s.substring(0, 1).toUpperCase() + s.substring(1);
		}

		return EYP.ns.toString() + s;
	}

	private String toAscii(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		return s.replaceAll("[^\\x00-\\x7F]", "");
	}

	private Quantity getQuantity(String s) {
		Quantity ret = null;

		if (s.equals("-"))
			return ret;
			
		// number-space-unit case, e.g. 150 kHz		
		if (s.matches("[0-9]+\\.{0,1}[0-9]*\\s[a-zA-Z]*")) {
			String[] components = s.split(" ");
			String value = components[0];
			String unit = components[1];
			String unitCode = unitCodeMap.get(unit);
			
			if (unitCode == null) {
				log.warning("Unit not mapped [unit = " + unit + "]");
				return ret;
			}
			
			ret = new Quantity(s, value, unitCode);			
		} else {
//			System.out.println(s);
		}
		
		return ret;
	}

	private class Quantity {

		String label = null;
		String value = null;
		String minValue = null;
		String maxValue = null;
		String unitCode = null;

		public Quantity(String label, String value, String unitCode) {
			this.label = label;
			this.value = value;
			this.unitCode = unitCode;
		}
		
		public String getLabel() {
			return label;
		}

		public String getValue() {
			return value;
		}

		public String getMinValue() {
			return minValue;
		}

		public String getMaxValue() {
			return maxValue;
		}

		public String getUnitCode() {
			return unitCode;
		}

	}
}
