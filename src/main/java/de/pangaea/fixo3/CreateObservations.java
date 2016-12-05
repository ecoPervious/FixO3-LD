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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import de.pangaea.fixo3.vocab.FIXO3;
import de.pangaea.fixo3.vocab.SSN;
import de.pangaea.fixo3.vocab.Schema;
import de.pangaea.fixo3.vocab.Time;

/**
 * <p>
 * Title: CreateObservations
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

public class CreateObservations {

	// private static final String csvFile =
	// "src/main/resources/thesis/e2-m3a-adcp-horizontal-current-speed-monthly.csv";
	// private static final DateTime skipBefore = new DateTime(2011, 5, 1, 0, 0,
	// 0);
	// private static final Double skipDepth = 253.0;
	// Resource sensorId =
	// ResourceFactory.createResource("http://fixo3.eu/vocab/0195d649bded6f89d28c061b55b4b175");
	// Resource propertyId = ResourceFactory
	// .createResource("http://esonetyellowpages.com/vocab/da9d4f1ed93c43acc135d6daba0cfe26");
	// Resource featureId = ResourceFactory
	// .createResource("http://esonetyellowpages.com/vocab/f9211947db29a7c0590ab410a5c5111b");
	// Resource stimulusId = ResourceFactory
	// .createResource("http://esonetyellowpages.com/vocab/6fd421abc092d7996a4bed6f50e582f6");
	// Resource meterPerSecondId =
	// ResourceFactory.createResource("http://qudt.org/vocab/unit#MeterPerSecond");
//	private static final String rdfFile = "src/main/resources/thesis/e2-m3a-adcp-observations.rdf";

	private static final String csvFile = "src/main/resources/thesis/pap-pco2.csv";
	private static final DateTime skipBefore = new DateTime(2010, 1, 1, 0, 0, 0);
	private static final Double skipDepth = 0.0;
	Resource sensorId = ResourceFactory.createResource("http://fixo3.eu/vocab/45767d1518b17d76ed77fba69ce8aba1");
	Resource propertyId = ResourceFactory
			.createResource("http://esonetyellowpages.com/vocab/2dfc4c07a3258b5131d86de3b7dff46b");
	Resource featureId = ResourceFactory
			.createResource("http://esonetyellowpages.com/vocab/541de5db9a763b1589dfa2381e24f7b1");
	Resource stimulusId = ResourceFactory
			.createResource("http://esonetyellowpages.com/vocab/746b9db404bec413ab9c469487a98f33");
	Resource meterPerSecondId = ResourceFactory.createResource("http://qudt.org/vocab/unit#PartsPerMillion");
	private static final String rdfFile = "src/main/resources/thesis/pap-pco2-observations.rdf";

	private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	private void run() throws IOException {
		BufferedReader br = null;
		String line;

		Model m = ModelFactory.createDefaultModel();

		Property observedBy = ResourceFactory.createProperty(SSN.observedBy.toString());
		Property observedProperty = ResourceFactory.createProperty(SSN.observedProperty.toString());
		Property featureOfInterest = ResourceFactory.createProperty(SSN.featureOfInterest.toString());
		Property fromStimulus = ResourceFactory.createProperty(SSN.fromStimulus.toString());
		Property observationResult = ResourceFactory.createProperty(SSN.observationResult.toString());
		Property observationResultTime = ResourceFactory.createProperty(SSN.observationResultTime.toString());
		Property hasValue = ResourceFactory.createProperty(SSN.hasValue.toString());
		Property value = ResourceFactory.createProperty(Schema.value.toString());
		Property unitCode = ResourceFactory.createProperty(Schema.unitCode.toString());
		Property inXSDDateTime = ResourceFactory.createProperty(Time.inXSDDateTime.toString());

		Resource Observation = ResourceFactory.createResource(SSN.Observation.toString());
		Resource SensorOutput = ResourceFactory.createResource(SSN.SensorOutput.toString());
		Resource QuantitativeValue = ResourceFactory.createResource(Schema.QuantitativeValue.toString());
		Resource Instant = ResourceFactory.createResource(Time.Instant.toString());

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] row = line.split(",");

				DateTime time = dtf.parseDateTime(row[0]);

				if (time.isBefore(skipBefore))
					continue;

				Double depth = Double.valueOf(row[1]);

				if (depth == skipDepth)
					continue;

				Double speed = Double.valueOf(row[2]);

				Resource observationId = ResourceFactory.createResource(FIXO3.ns + UUID.randomUUID().toString());
				Resource sensorOutputId = ResourceFactory.createResource(FIXO3.ns + UUID.randomUUID().toString());
				Resource valueId = ResourceFactory.createResource(FIXO3.ns + UUID.randomUUID().toString());
				Resource timeId = ResourceFactory.createResource(FIXO3.ns + UUID.randomUUID().toString());

				m.add(observationId, RDF.type, Observation);
				m.add(observationId, observedBy, sensorId);
				m.add(observationId, observedProperty, propertyId);
				m.add(observationId, featureOfInterest, featureId);
				m.add(observationId, fromStimulus, stimulusId);
				m.add(observationId, observationResult, sensorOutputId);
				m.add(sensorOutputId, RDF.type, SensorOutput);
				m.add(sensorOutputId, hasValue, valueId);
				m.add(valueId, RDF.type, QuantitativeValue);
				m.add(valueId, value, ResourceFactory.createTypedLiteral(speed.toString(), XSDDatatype.XSDdouble));
				m.add(valueId, unitCode, meterPerSecondId);
				m.add(observationId, observationResultTime, timeId);
				m.add(timeId, RDF.type, Instant);
				m.add(timeId, inXSDDateTime, ResourceFactory.createTypedLiteral(
						ISODateTimeFormat.dateTime().withOffsetParsed().print(time), XSDDatatype.XSDdateTime));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		m.write(new FileWriter(new File(rdfFile)));
	}

	public static void main(String[] args) throws IOException {
		CreateObservations app = new CreateObservations();
		app.run();
	}
}
