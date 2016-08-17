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
import static de.pangaea.fixo3.vocab.EYP.CurrentMeter;
import static de.pangaea.fixo3.vocab.EYP.DopplerEffect;
import static de.pangaea.fixo3.vocab.EYP.FlowVelocity;
import static de.pangaea.fixo3.vocab.EYP.Fluid;
import static de.pangaea.fixo3.vocab.EYP.Frequency;
import static de.pangaea.fixo3.vocab.EYP.HydroacousticCurrentMeter;
import static de.pangaea.fixo3.vocab.EYP.MeasuringRange;
import static de.pangaea.fixo3.vocab.EYP.OceanographicDevice;
import static de.pangaea.fixo3.vocab.EYP.OperatingDepth;
import static de.pangaea.fixo3.vocab.EYP.PartialPressureOfCO2Analyzer;
import static de.pangaea.fixo3.vocab.EYP.ProfilingRange;
import static de.pangaea.fixo3.vocab.EYP.SoundWave;
import static de.pangaea.fixo3.vocab.EYP.TemperatureRange;
import static de.pangaea.fixo3.vocab.EYP.Velocity;
import static de.pangaea.fixo3.vocab.EYP.Water;
import static de.pangaea.fixo3.vocab.SSN.FeatureOfInterest;
import static de.pangaea.fixo3.vocab.SSN.MeasurementCapability;
import static de.pangaea.fixo3.vocab.SSN.Property;
import static de.pangaea.fixo3.vocab.SSN.SensingDevice;
import static de.pangaea.fixo3.vocab.SSN.Stimulus;
import static de.pangaea.fixo3.vocab.SSN.detects;
import static de.pangaea.fixo3.vocab.SSN.hasMeasurementCapability;
import static de.pangaea.fixo3.vocab.SSN.hasMeasurementProperty;
import static de.pangaea.fixo3.vocab.SSN.isPropertyOf;
import static de.pangaea.fixo3.vocab.SSN.isProxyFor;
import static de.pangaea.fixo3.vocab.SSN.observes;
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

	private final String eypDevicesFile = "src/main/resources/data/esonetyellowpages-devicetypes.json";
	private final String eypFile = "src/main/resources/ontologies/esonetyellowpages.rdf";
	private final String eypInferredFile = "src/main/resources/ontologies/esonetyellowpages-inferred.rdf";

	private OntologyManager m;

	private void run() throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		m = new OntologyManager(ns);
		m.addTitle("ESONET Yellow Pages Ontology");
		m.addVersion("0.1");
		m.addDate("2016-08-01");
		m.addCreator("Markus Stocker");
		m.addSeeAlso("http://www.esonetyellowpages.com/");
		m.addImport(SSN.ns);

		m.addSubClass(Frequency, QuantitativeValue);
		m.addLabel(Frequency, "Frequency");
		m.addSubClass(ProfilingRange, QuantitativeValue);
		m.addLabel(ProfilingRange, "Profiling Range");
		m.addSubClass(CellSize, QuantitativeValue);
		m.addLabel(CellSize, "Cell Size");
		m.addSubClass(OperatingDepth, QuantitativeValue);
		m.addLabel(OperatingDepth, "Operating Depth");
		m.addSubClass(TemperatureRange, QuantitativeValue);
		m.addLabel(TemperatureRange, "Temperature Range");
		m.addSubClass(MeasuringRange, QuantitativeValue);
		m.addLabel(MeasuringRange, "Measuring Range");

		m.addClass(SoundWave);
		m.addLabel(SoundWave, "Sound Wave");
		m.addSubClass(SoundWave, Stimulus);

		m.addClass(Velocity);
		m.addLabel(Velocity, "Velocity");
		m.addSubClass(Velocity, Property);

		m.addClass(Fluid);
		m.addLabel(Fluid, "Fluid");
		m.addSubClass(Fluid, FeatureOfInterest);

		m.addClass(Water);
		m.addLabel(Water, "Water");
		m.addSubClass(Water, Fluid);

		m.addClass(FlowVelocity);
		m.addLabel(FlowVelocity, "Flow Velocity");
		m.addSubClass(FlowVelocity, Velocity);
		m.addObjectSome(FlowVelocity, isPropertyOf, Fluid);

		m.addClass(DopplerEffect);
		m.addLabel(DopplerEffect, "Doppler Effect");
		m.addComment(DopplerEffect,
				"The Doppler effect (or the Doppler shift) is the change in frequency of a wave (or other periodic event) for an observer moving relative to its source.");
		m.addSource(DopplerEffect, IRI.create("https://en.wikipedia.org/wiki/Doppler_effect"));
		m.addSubClass(DopplerEffect, Stimulus);
		m.addObjectAll(DopplerEffect, isProxyFor, FlowVelocity);

		m.addClass(OceanographicDevice);
		m.addLabel(OceanographicDevice, "Oceanographic Device");
		m.addSubClass(OceanographicDevice, SensingDevice);

		m.addClass(CurrentMeter);
		m.addLabel(CurrentMeter, "Current Meter");
		m.addComment(CurrentMeter,
				"A current meter is an oceanographic device for flow measurement by mechanical (rotor current meter), tilt (Tilt Current Meter), acoustical (ADCP) or electrical means.");
		m.addSource(CurrentMeter, IRI.create("https://en.wikipedia.org/wiki/Current_meter"));

		m.addSubClass(CurrentMeter, OceanographicDevice);
		m.addObjectAll(CurrentMeter, observes, FlowVelocity);

		m.addClass(HydroacousticCurrentMeter);
		m.addLabel(HydroacousticCurrentMeter, "Hydroacoustic Current Meter");
		m.addSubClass(HydroacousticCurrentMeter, CurrentMeter);
		m.addObjectAll(HydroacousticCurrentMeter, detects, SoundWave);

		m.addClass(AcousticDopplerCurrentProfiler);
		m.addLabel(AcousticDopplerCurrentProfiler, "Acoustic Doppler Current Profiler");
		m.addSource(AcousticDopplerCurrentProfiler,
				IRI.create("https://en.wikipedia.org/wiki/Acoustic_Doppler_current_profiler"));
		m.addComment(AcousticDopplerCurrentProfiler,
				"An acoustic Doppler current profiler (ADCP) is a hydroacoustic current meter similar to a sonar, attempting to measure water current velocities over a depth range using the Doppler effect of sound waves scattered back from particles within the water column.");
		m.addObjectAll(AcousticDopplerCurrentProfiler, detects, DopplerEffect);
		m.addSubClass(AcousticDopplerCurrentProfiler, HydroacousticCurrentMeter);

		m.addClass(PartialPressureOfCO2Analyzer);
		m.addSubClass(PartialPressureOfCO2Analyzer, SensingDevice);
		m.addLabel(PartialPressureOfCO2Analyzer, "Partial Pressure of CO2 Analyzer");

		JsonReader jr = Json.createReader(new FileReader(new File(eypDevicesFile)));
		JsonArray ja = jr.readArray();

		for (JsonObject jo : ja.getValuesAs(JsonObject.class)) {
			String name = jo.getString("name");
			String label = jo.getString("label");
			String comment = jo.getString("comment");
			String seeAlso = jo.getString("seeAlso");
			JsonArray subClasses = jo.getJsonArray("subClasses");

			addDevice(name, label, comment, seeAlso, subClasses);
		}

		m.save(eypFile);
		m.saveInferred(eypInferredFile);
	}

	private void addDevice(String name, String label, String comment, String seeAlso, JsonArray subClasses) {
		IRI deviceIRI = IRI.create(name);

		m.addClass(deviceIRI);
		m.addLabel(deviceIRI, label);
		m.addComment(deviceIRI, comment);
		m.addSeeAlso(deviceIRI, seeAlso);

		// Default sub class, though implicit with curated sensing device type
		// hierarchy
		m.addSubClass(deviceIRI, SSN.SensingDevice);

		for (JsonObject subClass : subClasses.getValuesAs(JsonObject.class)) {
			if (subClass.containsKey("type")) {
				m.addSubClass(deviceIRI, IRI.create(subClass.getString("type")));
			} else if (subClass.containsKey("capability")) {
				JsonObject capability = subClass.getJsonObject("capability");
				String capabilityLabel = capability.getString("label");
				JsonObject quantity = capability.getJsonObject("quantity");
				String quantityLabel = quantity.getString("label");

				IRI capabilityIRI = IRI.create(EYP.ns.toString() + md5Hex(capabilityLabel));
				IRI quantityIRI = IRI.create(EYP.ns.toString() + md5Hex(quantityLabel));

				// Value restriction on property hasMeasurementCapability
				m.addObjectValue(deviceIRI, hasMeasurementCapability, capabilityIRI);

				m.addIndividual(capabilityIRI);
				m.addType(capabilityIRI, MeasurementCapability);
				m.addLabel(capabilityIRI, capabilityLabel);
				m.addObjectAssertion(capabilityIRI, hasMeasurementProperty, quantityIRI);

				m.addIndividual(quantityIRI);
				// Default
				m.addType(quantityIRI, SSN.MeasurementProperty); 

				if (quantity.containsKey("type")) {
					m.addType(quantityIRI, IRI.create(quantity.getString("type")));
				}

				m.addLabel(quantityIRI, quantityLabel);

				if (quantity.containsKey("value")) {
					m.addDataAssertion(quantityIRI, value, Float.valueOf(quantity.getString("value")));
				} else if (quantity.containsKey("minValue") && quantity.containsKey("maxValue")) {
					m.addDataAssertion(quantityIRI, minValue, Float.valueOf(quantity.getString("minValue")));
					m.addDataAssertion(quantityIRI, maxValue, Float.valueOf(quantity.getString("maxValue")));
				} else {
					throw new RuntimeException("Expected value or min/max value [quantity = " + quantity + "]");
				}

				m.addObjectAssertion(quantityIRI, unitCode, IRI.create(quantity.getString("unitCode")));
			}
		}

	}

	public static void main(String[] args)
			throws OWLOntologyCreationException, OWLOntologyStorageException, FileNotFoundException {
		CreateEsonetYellowPages app = new CreateEsonetYellowPages();
		app.run();
	}

}
