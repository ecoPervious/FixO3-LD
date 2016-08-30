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

package de.pangaea.fixo3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

/**
 * <p>
 * Title: CreateBrowser
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

public class CreateBrowser {

	private static final String service = "http://seprojects.marum.de:3030/fixo3observatories/sparql";

	private void run() throws IOException {
		StringBuffer sb = new StringBuffer();

		sb.append("<!doctype html>");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta charset=\"utf-8\">");
		sb.append("<link rel=\"stylesheet\" href=\"main.css\">");
		sb.append("<script language=\"javascript\" type=\"text/javascript\" src=\"functions.js\"></script>");
		sb.append("</head>");
		sb.append("<body>");

		QueryExecution qe1 = QueryExecutionFactory.sparqlService(service,
				FileUtils.readFileToString(new File("src/main/resources/sparql/browser-query1.rq")));

		ResultSet rs1 = qe1.execSelect();

		sb.append("<div>");
		sb.append("<h2>Platforms</h2>");

		while (rs1.hasNext()) {
			QuerySolution qs1 = rs1.next();
			String obsId = qs1.getResource("obsId").getURI();
			String obsLocalName = qs1.getResource("obsId").getLocalName();
			String obsLabel = qs1.getLiteral("obsLabel").getLexicalForm();
			String obsTitle = qs1.getLiteral("obsTitle").getLexicalForm();
			String obsComment = qs1.getLiteral("obsComment").getLexicalForm();

			sb.append("<div class=\"platform\" id=\"a" + obsLocalName + "\">");
			sb.append("<a href=\"#a" + obsLocalName + "\" onclick=\"display('" + obsLocalName + "')\">");
			sb.append("<strong>");
			sb.append(obsLabel);
			sb.append("</strong>");
			sb.append("</a>");
			sb.append("</br>");
			sb.append(obsTitle);
			sb.append("<div class=\"displayble\" id=\"" + obsLocalName + "\">");
			sb.append("<div id=\"comment\">");
			sb.append(obsComment);
			sb.append("</div>");
		
			QueryExecution qe2 = QueryExecutionFactory.sparqlService(service,
					FileUtils.readFileToString(new File("src/main/resources/sparql/browser-query2.rq"))
							.replaceAll("OBS_ID", obsId));
			
			ResultSet rs2 = qe2.execSelect();
			
			boolean hasSensors = false;
			
			if (rs2.hasNext()) {
				sb.append("<div>");
				sb.append("<h3>Sensors</h3>");
				hasSensors = true;
			}
			
			while (rs2.hasNext()) {
				QuerySolution qs2 = rs2.next();
				String sensorId = qs2.getResource("sensorId").getURI();
				String sensorLocalName = qs2.getResource("sensorId").getLocalName();
				String sensorLabel = qs2.getLiteral("sensorLabel").getLexicalForm();
				
				sb.append("<div class=\"sensor\" id=\"a" + obsLocalName + "\">");
				sb.append("<a href=\"#a" + obsLocalName + "\" onclick=\"display('" + sensorLocalName + "')\">");
				sb.append("<strong>");
				sb.append(sensorLabel);
				sb.append("</strong>");
				sb.append("</a>");
				sb.append("<div class=\"displayble\" id=\"" + sensorLocalName + "\">");
				sb.append("<div>");
				sb.append("<table>");
				
				QueryExecution qe3 = QueryExecutionFactory.sparqlService(service,
						FileUtils.readFileToString(new File("src/main/resources/sparql/browser-query3.rq"))
								.replaceAll("SENSOR_ID", sensorId));
				
				ResultSet rs3 = qe3.execSelect();
				
				while (rs3.hasNext()) {
					QuerySolution qs3 = rs3.next();
					
					String value = null;
					String minValue = null;
					String maxValue = null;
					String unitSymbol = null;
					
					String propertyLabel = qs3.getLiteral("propertyLabel").getLexicalForm();
					
					if (qs3.getLiteral("value") != null)
						value = qs3.getLiteral("value").getLexicalForm();
					if (qs3.getLiteral("minValue") != null)
						minValue = qs3.getLiteral("minValue").getLexicalForm();
					if (qs3.getLiteral("maxValue") != null)
						maxValue = qs3.getLiteral("maxValue").getLexicalForm();
					if (qs3.getLiteral("unitSymbol") != null)
						unitSymbol = qs3.getLiteral("unitSymbol").getLexicalForm();
					
					sb.append("<tr>");
					sb.append("<td>");
					sb.append(propertyLabel);
					sb.append("</td>");
					sb.append("<td>");
					
					if (value != null) {
						sb.append(value);
					} else if (minValue != null && maxValue != null) {
						sb.append(minValue + " - " + maxValue); 
					}
					
					if (unitSymbol != null) {
						sb.append(" " + unitSymbol);
					}
					
					sb.append("</td>");
					sb.append("</tr>");
				}
				
				qe3.close();
				
				sb.append("</table>");
				sb.append("</div>");
				sb.append("</div>");
				sb.append("</div>");
			}
			
			qe2.close();

			if (hasSensors) {
				sb.append("</div>");
			}
			
			sb.append("</div>");
			sb.append("</div>");
		}

		qe1.close();

		sb.append("</div>");

		sb.append("</body>");
		sb.append("</html>");

		FileWriter fw = new FileWriter(new File("browser/index.html"));
		fw.append(sb);
		fw.close();
	}

	public static void main(String[] args) throws IOException {
		CreateBrowser app = new CreateBrowser();
		app.run();
	}

}
