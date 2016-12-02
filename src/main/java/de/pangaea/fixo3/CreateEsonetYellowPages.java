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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.pangaea.fixo3.vocab.EYP;
import de.pangaea.fixo3.vocab.SSN;

import static de.pangaea.fixo3.vocab.EYP.AcousticDopplerCurrentProfiler;
import static de.pangaea.fixo3.vocab.EYP.CellSize;
import static de.pangaea.fixo3.vocab.EYP.DopplerEffect;
import static de.pangaea.fixo3.vocab.EYP.Infrared;
import static de.pangaea.fixo3.vocab.EYP.WaterCurrent;
import static de.pangaea.fixo3.vocab.EYP.CarbonDioxide;
import static de.pangaea.fixo3.vocab.EYP.Frequency;
import static de.pangaea.fixo3.vocab.EYP.MeasuringRange;
import static de.pangaea.fixo3.vocab.EYP.OperatingDepth;
import static de.pangaea.fixo3.vocab.EYP.PartialPressureOfCO2Analyzer;
import static de.pangaea.fixo3.vocab.EYP.ProfilingRange;
import static de.pangaea.fixo3.vocab.EYP.TemperatureRange;
import static de.pangaea.fixo3.vocab.EYP.Speed;
import static de.pangaea.fixo3.vocab.EYP.PartialPressure;
import static de.pangaea.fixo3.vocab.SSN.MeasurementProperty;
import static de.pangaea.fixo3.vocab.SSN.FeatureOfInterest;
import static de.pangaea.fixo3.vocab.SSN.Property;
import static de.pangaea.fixo3.vocab.SSN.MeasurementCapability;
import static de.pangaea.fixo3.vocab.SSN.SensingDevice;
import static de.pangaea.fixo3.vocab.SSN.Stimulus;
import static de.pangaea.fixo3.vocab.SSN.detects;
import static de.pangaea.fixo3.vocab.SSN.hasMeasurementCapability;
import static de.pangaea.fixo3.vocab.SSN.hasMeasurementProperty;
import static de.pangaea.fixo3.vocab.SSN.isPropertyOf;
import static de.pangaea.fixo3.vocab.SSN.isProxyFor;
import static de.pangaea.fixo3.vocab.SSN.observes;
import static de.pangaea.fixo3.vocab.SSN.hasValue;
import static de.pangaea.fixo3.vocab.Schema.QuantitativeValue;
import static de.pangaea.fixo3.vocab.Schema.maxValue;
import static de.pangaea.fixo3.vocab.Schema.minValue;
import static de.pangaea.fixo3.vocab.Schema.unitCode;
import static de.pangaea.fixo3.vocab.Schema.value;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * <p>
 * Title: CreateEsonetYellowPages
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

public class CreateEsonetYellowPages {

	private final IRI ns = IRI.create("http://esonetyellowpages.com/vocab/");

	private final String eypDevicesFile = "src/main/resources/thesis/esonetyellowpages-devicetypes.json";
	private final String eypFile = "src/main/resources/thesis/esonetyellowpages.rdf";
	private final String eypInferredFile = "src/main/resources/thesis/esonetyellowpages-inferred.rdf";

	private OntologyManager m;

	private void run() throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		m = new OntologyManager(ns);
		m.addTitle("ESONET Yellow Pages Ontology");
		m.addVersion("1.0");
		m.addDate("2016-11-23");
		m.addCreator("Markus Stocker");
		m.addSeeAlso("http://www.esonetyellowpages.com/");
		m.addImport(SSN.ns);

		m.addSubClass(Frequency, MeasurementProperty);
		m.addLabel(Frequency, "Frequency");
		m.addSubClass(ProfilingRange, MeasurementProperty);
		m.addLabel(ProfilingRange, "Profiling Range");
		m.addSubClass(CellSize, MeasurementProperty);
		m.addLabel(CellSize, "Cell Size");
		m.addSubClass(OperatingDepth, MeasurementProperty);
		m.addLabel(OperatingDepth, "Operating Depth");
		m.addSubClass(TemperatureRange, MeasurementProperty);
		m.addLabel(TemperatureRange, "Temperature Range");
		m.addSubClass(MeasuringRange, MeasurementProperty);
		m.addLabel(MeasuringRange, "Measuring Range");

		m.addClass(WaterCurrent);
		m.addLabel(WaterCurrent, "Water Current");
		m.addSubClass(WaterCurrent, FeatureOfInterest);

		m.addClass(CarbonDioxide);
		m.addLabel(CarbonDioxide, "Carbon Dioxide");
		m.addSubClass(CarbonDioxide, FeatureOfInterest);

		m.addClass(Speed);
		m.addLabel(Speed, "Speed");
		m.addSubClass(Speed, Property);
		m.addObjectSome(Speed, isPropertyOf, WaterCurrent);

		m.addClass(PartialPressure);
		m.addLabel(PartialPressure, "Partial Pressure");
		m.addSubClass(PartialPressure, Property);
		m.addObjectSome(PartialPressure, isPropertyOf, CarbonDioxide);

		m.addClass(DopplerEffect);
		m.addSubClass(DopplerEffect, Stimulus);
		m.addLabel(DopplerEffect, "Doppler Effect");
		m.addComment(DopplerEffect,
				"The Doppler effect (or the Doppler shift) is the change in frequency of a wave (or other periodic event) for an observer moving relative to its source.");
		m.addSource(DopplerEffect, IRI.create("https://en.wikipedia.org/wiki/Doppler_effect"));
		m.addObjectAll(DopplerEffect, isProxyFor, Speed);

		m.addClass(Infrared);
		m.addSubClass(Infrared, Stimulus);
		m.addLabel(Infrared, "Infrared");
		m.addComment(Infrared, "Infrared (IR) is an invisible radiant energy.");
		m.addSource(Infrared, IRI.create("https://en.wikipedia.org/wiki/Infrared"));
		m.addObjectAll(Infrared, isProxyFor, PartialPressure);

		m.addClass(AcousticDopplerCurrentProfiler);
		m.addLabel(AcousticDopplerCurrentProfiler, "Acoustic Doppler Current Profiler");
		m.addSource(AcousticDopplerCurrentProfiler,
				IRI.create("https://en.wikipedia.org/wiki/Acoustic_Doppler_current_profiler"));
		m.addComment(AcousticDopplerCurrentProfiler,
				"An acoustic Doppler current profiler (ADCP) is a hydroacoustic current meter similar to a sonar, attempting to measure water current velocities over a depth range using the Doppler effect of sound waves scattered back from particles within the water column.");
		m.addObjectAll(AcousticDopplerCurrentProfiler, detects, DopplerEffect);
		m.addSubClass(AcousticDopplerCurrentProfiler, SensingDevice);

		m.addClass(PartialPressureOfCO2Analyzer);
		m.addLabel(PartialPressureOfCO2Analyzer, "Partial Pressure of CO2 Analyzer");
		m.addObjectAll(PartialPressureOfCO2Analyzer, detects, Infrared);
		m.addSubClass(PartialPressureOfCO2Analyzer, SensingDevice);

		JsonReader jr = Json.createReader(new FileReader(new File(eypDevicesFile)));
		JsonArray ja = jr.readArray();

		for (JsonObject jo : ja.getValuesAs(JsonObject.class)) {
			String name = jo.getString("name");
			String label = jo.getString("label");
			String comment = jo.getString("comment");
			String seeAlso = jo.getString("seeAlso");
			JsonArray equivalentClasses = jo.getJsonArray("equivalentClasses");
			JsonArray subClasses = jo.getJsonArray("subClasses");

			addDeviceType(name, label, comment, seeAlso, equivalentClasses, subClasses);
		}

		m.save(eypFile);
		m.saveInferred(eypInferredFile);
	}

	private void addDeviceType(String name, String label, String comment, String seeAlso, JsonArray equivalentClasses,
			JsonArray subClasses) {
		IRI deviceTypeIRI = IRI.create(name);

		m.addClass(deviceTypeIRI);
		m.addLabel(deviceTypeIRI, label);
		m.addComment(deviceTypeIRI, comment);
		m.addSeeAlso(deviceTypeIRI, seeAlso);

		// Default sub class, though implicit with curated sensing device type
		// hierarchy
		m.addSubClass(deviceTypeIRI, SSN.SensingDevice);

		for (JsonObject equivalentClass : equivalentClasses.getValuesAs(JsonObject.class)) {
			if (equivalentClass.containsKey("type")) {
				m.addEquivalentClass(deviceTypeIRI, IRI.create(equivalentClass.getString("type")));
			}
		}

		for (JsonObject subClass : subClasses.getValuesAs(JsonObject.class)) {
			if (subClass.containsKey("type")) {
				m.addSubClass(deviceTypeIRI, IRI.create(subClass.getString("type")));
			} else if (subClass.containsKey("observes")) {
				JsonObject observesJson = subClass.getJsonObject("observes");
				String propertyLabel = observesJson.getString("label");
				String propertyType = observesJson.getString("type");
				JsonObject featureJson = observesJson.getJsonObject("isPropertyOf");
				String featureLabel = featureJson.getString("label");
				String featureType = featureJson.getString("type");

				IRI propertyIRI = IRI.create(EYP.ns.toString() + md5Hex(propertyLabel));
				IRI propertyTypeIRI = IRI.create(propertyType);
				IRI featureIRI = IRI.create(EYP.ns.toString() + md5Hex(featureLabel));
				IRI featureTypeIRI = IRI.create(featureType);

				m.addObjectValue(deviceTypeIRI, observes, propertyIRI);
				m.addIndividual(propertyIRI);
				m.addType(propertyIRI, propertyTypeIRI);
				m.addLabel(propertyIRI, propertyLabel);
				m.addIndividual(featureIRI);
				m.addType(featureIRI, featureTypeIRI);
				m.addLabel(featureIRI, featureLabel);
				m.addObjectAssertion(propertyIRI, isPropertyOf, featureIRI);
			} else if (subClass.containsKey("detects")) {
				JsonObject detectsJson = subClass.getJsonObject("detects");
				String stimulusLabel = detectsJson.getString("label");
				String stimulusType = detectsJson.getString("type");

				IRI stimulusIRI = IRI.create(EYP.ns.toString() + md5Hex(stimulusLabel));
				IRI stimulusTypeIRI = IRI.create(stimulusType);

				m.addObjectValue(deviceTypeIRI, detects, stimulusIRI);
				m.addIndividual(stimulusIRI);
				m.addType(stimulusIRI, stimulusTypeIRI);
				m.addLabel(stimulusIRI, stimulusLabel);
			} else if (subClass.containsKey("capability")) {
				JsonObject capabilityJson = subClass.getJsonObject("capability");
				String capabilityLabel = capabilityJson.getString("label");
				JsonObject propertyJson = capabilityJson.getJsonObject("property");
				String propertyLabel = propertyJson.getString("label");
				String propertyType = propertyJson.getString("type");
				JsonObject valueJson = propertyJson.getJsonObject("value");
				String valueLabel = valueJson.getString("label");

				IRI capabilityIRI = IRI.create(EYP.ns.toString() + md5Hex(capabilityLabel));
				IRI propertyIRI = IRI.create(EYP.ns.toString() + md5Hex(propertyLabel));
				IRI propertyTypeIRI = IRI.create(propertyType);
				IRI valueIRI = IRI.create(EYP.ns.toString() + md5Hex(valueLabel));

				m.addObjectValue(deviceTypeIRI, hasMeasurementCapability, capabilityIRI);
				m.addIndividual(capabilityIRI);
				m.addType(capabilityIRI, MeasurementCapability);
				m.addLabel(capabilityIRI, capabilityLabel);
				m.addObjectAssertion(capabilityIRI, hasMeasurementProperty, propertyIRI);
				m.addIndividual(propertyIRI);
				m.addType(propertyIRI, SSN.MeasurementProperty);
				m.addType(propertyIRI, propertyTypeIRI);
				m.addLabel(propertyIRI, propertyLabel);
				m.addObjectAssertion(propertyIRI, hasValue, valueIRI);
				m.addIndividual(valueIRI);
				m.addType(valueIRI, SSN.ObservationValue);
				m.addLabel(valueIRI, valueLabel);

				if (valueJson.containsKey("value")) {
					m.addType(valueIRI, QuantitativeValue);
					m.addDataAssertion(valueIRI, value, Float.valueOf(valueJson.getString("value")));
				} else if (valueJson.containsKey("minValue") && valueJson.containsKey("maxValue")) {
					m.addType(valueIRI, QuantitativeValue);
					m.addDataAssertion(valueIRI, minValue, Float.valueOf(valueJson.getString("minValue")));
					m.addDataAssertion(valueIRI, maxValue, Float.valueOf(valueJson.getString("maxValue")));
				} else {
					throw new RuntimeException("Expected value or min/max value [valueJson = " + valueJson + "]");
				}

				m.addObjectAssertion(valueIRI, unitCode, IRI.create(valueJson.getString("unitCode")));
			}
		}

	}

	public static void main(String[] args)
			throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		CreateEsonetYellowPages app = new CreateEsonetYellowPages();
		app.run();
	}

}
