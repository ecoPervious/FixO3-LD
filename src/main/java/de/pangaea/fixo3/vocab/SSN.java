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

package de.pangaea.fixo3.vocab;

import org.semanticweb.owlapi.model.IRI;

/**
 * <p>
 * Title: SSN
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

public class SSN {

	public static final IRI ns = IRI.create("https://www.w3.org/ns/ssn/");

	public static final IRI Observation = c("Observation");
	public static final IRI Platform = c("Platform");
	public static final IRI Stimulus = c("Stimulus");
	public static final IRI FeatureOfInterest = c("FeatureOfInterest");
	public static final IRI SensingDevice = c("SensingDevice");
	public static final IRI Property = c("Property");
	public static final IRI MeasurementCapability = c("MeasurementCapability");
	public static final IRI MeasurementProperty = c("MeasurementProperty");
	public static final IRI ObservationValue = c("ObservationValue");
	public static final IRI SensorOutput = c("SensorOutput");

	public static final IRI detects = c("detects");
	public static final IRI observes = c("observes");
	public static final IRI observedBy = c("observedBy");
	public static final IRI observedProperty = c("observedProperty");
	public static final IRI isPropertyOf = c("isPropertyOf");
	public static final IRI isProxyFor = c("isProxyFor");
	public static final IRI attachedSystem = c("attachedSystem");
	public static final IRI hasMeasurementCapability = c("hasMeasurementCapability");
	public static final IRI hasMeasurementProperty = c("hasMeasurementProperty");
	public static final IRI hasValue = c("hasValue");
	public static final IRI featureOfInterest = c("featureOfInterest");
	public static final IRI fromStimulus = c("fromStimulus");
	public static final IRI observationResult = c("observationResult");
	public static final IRI observationResultTime = c("observationResultTime");

	private static IRI c(String suffix) {
		return IRI.create(ns.toString(), suffix);
	}
	
}
