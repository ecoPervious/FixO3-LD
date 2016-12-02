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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;

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
	private static final String unit = "http://qudt.org/vocab/unit#";
	private static final String qudt = "http://qudt.org/schema/qudt#";
	private static final String xsd = "http://www.w3.org/2001/XMLSchema#";
	private static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	private void run() throws IOException {
		StringBuffer observatories = new StringBuffer();

		Set<String> propertiesBlackList = new HashSet<String>();
		propertiesBlackList.add("Property");

		Set<String> featuresBlackList = new HashSet<String>();
		featuresBlackList.add("Feature of Interest");
		
		Set<String> stimulusBlackList = new HashSet<String>();
		stimulusBlackList.add("Stimulus");

		observatories.append("<!doctype html>");
		observatories.append("<html>");
		observatories.append("<head>");
		observatories.append("<meta charset=\"utf-8\">");
		observatories.append("<link rel=\"stylesheet\" href=\"main.css\">");
		observatories.append("</head>");
		observatories.append("<body>");

		QueryExecution qe1 = QueryExecutionFactory.sparqlService(service,
				FileUtils.readFileToString(new File("src/main/resources/sparql/browser-query1.rq")));

		ResultSet rs1 = qe1.execSelect();

		observatories.append("<div id=\"main\">");
		observatories.append("<h1>FixO3 Observatories</h1>");

		while (rs1.hasNext()) {
			QuerySolution qs1 = rs1.next();
			String obsId = qs1.getResource("obsId").getURI();
			String obsLocalName = qs1.getResource("obsId").getLocalName();
			String obsLabel = qs1.getLiteral("obsLabel").getLexicalForm();
			String obsTitle = qs1.getLiteral("obsTitle").getLexicalForm();
			String obsComment = qs1.getLiteral("obsComment").getLexicalForm();
			String obsLocation = qs1.getLiteral("obsLocation").getLexicalForm();
			Point point = new Point(obsLocation);

			observatories.append("<div class=\"platform\">");
			observatories.append("<a href=\"" + obsLocalName + ".html\">");
			observatories.append("<strong>");
			observatories.append(obsLabel);
			observatories.append("</strong>");
			observatories.append("</a>");
			observatories.append("</br>");
			observatories.append(obsTitle);
			observatories.append("</div>");

			StringBuffer observatory = new StringBuffer();

			observatory.append("<!doctype html>");
			observatory.append("<html>");
			observatory.append("<head>");
			observatory.append("<meta charset=\"utf-8\">");
			observatory.append("<link rel=\"stylesheet\" href=\"main.css\">");
			observatory.append(
					"<script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyCeLgOdZllXtTwtjlmuvbbw5Z8AeSFYwjE&signed_in=true\"></script>");
			observatory.append("<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>");
			observatory.append("</head>");
			observatory.append("<body>");

			observatory.append("<div id=\"main\">");
			observatory.append("<section id=\"header\">");
			observatory.append("<h1>");
			observatory.append(obsLabel);
			observatory.append("</h1>");
			observatory.append("<h2>");
			observatory.append(obsTitle);
			observatory.append("</h2>");
			observatory.append("</section>");

			observatory.append("<section id=\"content\">");
			observatory.append("<div id=\"description\">");
			observatory.append(obsComment);
			observatory.append("</div>");
			observatory.append("<div id=\"location\">");
			observatory.append("<div id=\"map\"></div>");
			observatory.append("<script>");
			observatory.append("var coords = {lat: " + point.getLat() + ", lng: " + point.getLon() + "};");
			observatory.append("new google.maps.Marker({");
			observatory.append("position: coords,");
			observatory.append("map: new google.maps.Map(document.getElementById('map'), {");
			observatory.append("zoom: 4,");
			observatory.append("center: coords");
			observatory.append("})");
			observatory.append("});");
			observatory.append("</script>");
			observatory.append("</div>");
			observatory.append("</section>");

			QueryExecution qe2 = QueryExecutionFactory.sparqlService(service,
					FileUtils.readFileToString(new File("src/main/resources/sparql/browser-query2.rq"))
							.replaceAll("OBS_ID", obsId));

			ResultSet rs2 = qe2.execSelect();

			observatory.append("<div id=\"sensors\">");

			if (rs2.hasNext()) {
				observatory.append("<h3>Sensors</h3>");
			}

			int propertyCount = 0;
			int plotCount = 0;

			while (rs2.hasNext()) {
				QuerySolution qs2 = rs2.next();
				String sensorId = qs2.getResource("sensorId").getURI();
				String sensorLabel = qs2.getLiteral("sensorLabel").getLexicalForm();
				String observedPropertyLabel = qs2.getLiteral("propertyLabel").getLexicalForm();
				String featureLabel = qs2.getLiteral("featureLabel").getLexicalForm();
				String stimulusLabel = qs2.getLiteral("stimulusLabel").getLexicalForm();

				if (propertiesBlackList.contains(observedPropertyLabel))
					continue;
				if (featuresBlackList.contains(featureLabel))
					continue;
				if (stimulusBlackList.contains(stimulusLabel))
					continue;

				observatory.append("<div class=\"sensor\">");
				observatory.append("<h4>");
				observatory.append(sensorLabel);
				observatory.append("</h4>");

				observatory.append("<div>");
				observatory.append("<table>");

				observatory.append("<tr>");
				observatory.append("<td>");
				observatory.append("<i>Observed Property</i>");
				observatory.append("</td>");
				observatory.append("<td>");
				observatory.append(observedPropertyLabel);
				observatory.append("</td>");
				observatory.append("</tr>");
				
				observatory.append("<tr>");
				observatory.append("<td>");
				observatory.append("<i>Monitored Feature</i>");
				observatory.append("</td>");
				observatory.append("<td>");
				observatory.append(featureLabel);
				observatory.append("</td>");
				observatory.append("</tr>");

				observatory.append("<tr>");
				observatory.append("<td>");
				observatory.append("<i>Detected Stimulus</i>");
				observatory.append("</td>");
				observatory.append("<td>");
				observatory.append(stimulusLabel);
				observatory.append("</td>");
				observatory.append("</tr>");

				observatory.append("</table>");

				observatory.append("<h5>Measurement Capabilities</h5>");
				
				observatory.append("<table>");

				QueryExecution qe3 = QueryExecutionFactory.sparqlService(service,
						FileUtils.readFileToString(new File("src/main/resources/sparql/browser-query3.rq"))
								.replaceAll("SENSOR_ID", sensorId));

				ResultSet rs3 = qe3.execSelect();

				while (rs3.hasNext()) {
					QuerySolution qs3 = rs3.next();

					String value = null;
					String minValue = null;
					String maxValue = null;
					String unitId = null;
					String unitSymbol = null;

					String propertyLabel = qs3.getLiteral("propertyLabel").getLexicalForm();

					if (qs3.getLiteral("value") != null)
						value = qs3.getLiteral("value").getLexicalForm();
					if (qs3.getLiteral("minValue") != null)
						minValue = qs3.getLiteral("minValue").getLexicalForm();
					if (qs3.getLiteral("maxValue") != null)
						maxValue = qs3.getLiteral("maxValue").getLexicalForm();
					if (qs3.getResource("unitId") != null)
						unitId = qs3.getResource("unitId").getURI();
					if (qs3.getLiteral("unitSymbol") != null)
						unitSymbol = qs3.getLiteral("unitSymbol").getLexicalForm();

					observatory.append("<tr>");
					observatory.append("<td>");
					observatory.append("<i>" + propertyLabel + "</i>");
					observatory.append("</td>");
					observatory.append("<td>");

					if (value != null) {
						observatory.append(value);
					} else if (minValue != null && maxValue != null) {
						observatory.append(minValue + " - " + maxValue);
					}

					if (unitId != null && unitSymbol != null) {
						observatory.append("&nbsp;");
						observatory.append("<a href=\"#openModal" + propertyCount + "\">");
						observatory.append(unitSymbol);
						observatory.append("</a>");

						observatory.append("<div id=\"openModal" + propertyCount + "\" class=\"modalDialog\">");
						observatory.append("<div>");
						observatory.append("<a href=\"#close\" title=\"Close\" class=\"close\">X</a>");

						QueryExecution qe4 = QueryExecutionFactory
								.sparqlService(service,
										FileUtils
												.readFileToString(
														new File("src/main/resources/sparql/browser-query4.rq"))
												.replaceAll("UNIT_ID", unitId));

						ResultSet rs4 = qe4.execSelect();

						observatory.append("<h2>");
						observatory.append(unitId.replace(unit, "unit:"));
						observatory.append("</h2>");

						observatory.append("<a href=\"" + unitId + "\">" + unitId + "</a>");

						observatory.append("<div id=\"unit\">");
						observatory.append("<table>");

						while (rs4.hasNext()) {
							QuerySolution qs4 = rs4.next();

							observatory.append("<tr>");
							observatory.append("<td>");
							observatory.append(
									qs4.getResource("property").getURI().replace(qudt, "qudt:").replace(rdf, "rdf:"));
							observatory.append("</td>");
							observatory.append("<td>");

							if (qs4.get("object").isResource())
								observatory.append(qs4.getResource("object").getURI().replace(qudt, "qudt:"));
							else
								observatory.append(qs4.getLiteral("object").toString().replace(xsd, "xsd:"));

							observatory.append("</td>");
							observatory.append("</tr>");
						}

						observatory.append("</table>");
						observatory.append("</div>");
						observatory.append("</div>");
						observatory.append("</div>");
					}

					observatory.append("</td>");
					observatory.append("</tr>");

					propertyCount++;
				}

				qe3.close();

				observatory.append("</table>");
				observatory.append("</div>");

				QueryExecution qe5 = QueryExecutionFactory.sparqlService(service,
						FileUtils.readFileToString(new File("src/main/resources/sparql/browser-query5.rq"))
								.replaceAll("SENSOR_ID", sensorId));

				ResultSet rs5 = qe5.execSelect();

				List<String> times = new ArrayList<String>();
				List<String> values = new ArrayList<String>();
				
				while (rs5.hasNext()) {
					QuerySolution qs = rs5.next();
					String time = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(ISODateTimeFormat.dateTime()
							.withOffsetParsed().parseDateTime(qs.getLiteral("time").getLexicalForm()));
					String value = qs.getLiteral("value").getLexicalForm();
					
					times.add(time);
					values.add(value);
				}

				observatory.append("<div id=\"plot" + plotCount + "\" style=\"width:800px;height:600px;\"></div>");
				observatory.append("<script>");
				observatory.append("var data = [");
				observatory.append("{");
				observatory.append("x: [");
				
				boolean first = true;
				
				for (String time : times) {
					if (!first)
						observatory.append(",");
					observatory.append("'" + time + "'");
					first = false;
				}
				
				observatory.append("],");
				observatory.append("y: [");
				
				first = true;
				
				for (String value : values) {
					if (!first)
						observatory.append(",");
					observatory.append(value);
					first = false;
				}
				
				observatory.append("],");
				observatory.append("type: 'scatter'");
				observatory.append("}");
				observatory.append("];");
				observatory.append("Plotly.newPlot('plot" + plotCount + "', data);");
				observatory.append("</script>");
				observatory.append("</div>");
				
				plotCount++;
			}

			qe2.close();

			observatory.append("</div>");

			observatory.append("</div>");
			observatory.append("</body>");
			observatory.append("</html>");

			FileWriter fw = new FileWriter(new File("browser/" + obsLocalName + ".html"));
			fw.append(observatory);
			fw.close();
		}

		qe1.close();

		observatories.append("</div>");
		observatories.append("</body>");
		observatories.append("</html>");

		FileWriter fw = new FileWriter(new File("browser/index.html"));
		fw.append(observatories);
		fw.close();
	}

	public static void main(String[] args) throws IOException {
		CreateBrowser app = new CreateBrowser();
		app.run();
	}

	private class Point {

		String lat, lon;

		public Point(String wkt) {
			wkt = wkt.replace("POINT (", "");
			wkt = wkt.replace(")", "");

			String[] coords = wkt.split(" ");

			lon = coords[0];
			lat = coords[1];
		}

		public String getLat() {
			return lat;
		}

		public String getLon() {
			return lon;
		}
	}

}
