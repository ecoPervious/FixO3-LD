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

package de.pangaea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
	
	private final Logger log = Logger.getLogger(ProcessXmlFiles.class.getName());

	private void run() throws FileNotFoundException, SAXException, IOException, ParserConfigurationException,
			XPathExpressionException {
		File folder = new File("src/main/resources/files");
		File[] files = folder.listFiles();

		XPath x = XPathFactory.newInstance().newXPath();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		XPathExpression ex = x.compile("/");

		for (File file : files) {
			log.info(file.getName());
			Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(file));

		}

	}

	public static void main(String[] args)
			throws FileNotFoundException, SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		ProcessXmlFiles app = new ProcessXmlFiles();
		app.run();
	}

}
