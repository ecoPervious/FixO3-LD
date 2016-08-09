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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * <p>
 * Title: GetXmlFiles
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Project: ENVRIplus
 * </p>
 * <p>
 * Copyright: PANGAEA
 * </p>
 */

public class GetXmlFiles {

	private final String inputFileName = "src/main/resources/isi_sensorNanny_emso-yp-sml.html";
	private final String sensorBaseUrl = "http://www.ifremer.fr/isi/sensorNanny/emso-yp-sml/sensor";
	private final String outputBaseDir = "src/main/resources/files/";
	private final String replacePath = "/isi/sensorNanny/emso-yp-sml/";
	
	private void run() throws IOException, ParserConfigurationException, SAXException {
		List<String> lines = Files.readAllLines(Paths.get(inputFileName));
		
		for (String line : lines) {
			if (!line.contains(sensorBaseUrl))
				continue;
			
			Pattern pattern = Pattern.compile("(a href=\")(.*?)(\")");
			Matcher matcher = pattern.matcher(line);
			
			if (!matcher.find())
				continue;
			
			String xmlFile = matcher.group(2);
			
			System.out.println("Processing: " + xmlFile);
			
			URL url = new URL(xmlFile);
			String xmlFileName = url.getFile().replace(replacePath, "");
			
			Scanner scanner = new Scanner(url.openStream());
			
			PrintWriter printer = new PrintWriter(outputBaseDir + xmlFileName);
			
	        while(scanner.hasNextLine()) {
	            printer.write(scanner.nextLine());
	        }
	        
	        printer.close();
	        scanner.close();
		}
	}
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		GetXmlFiles app = new GetXmlFiles();
		app.run();
	}

}
