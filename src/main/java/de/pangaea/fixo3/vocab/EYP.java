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
 * Title: EYP
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

public class EYP {

	public static final IRI ns = IRI.create("http://esonetyellowpages.com/vocab/");

	public static final IRI ProfilingRange = c("ProfilingRange");
	public static final IRI CellSize = c("CellSize");
	public static final IRI OperatingDepth = c("OperatingDepth");
	public static final IRI TemperatureRange = c("TemperatureRange");
	public static final IRI MeasuringRange = c("MeasuringRange");
	public static final IRI Frequency = c("Frequency");
	
	public static final IRI SoundWave = c("SoundWave");
	public static final IRI DopplerEffect = c("DopplerEffect");
	public static final IRI Velocity = c("Velocity");
	public static final IRI FlowVelocity = c("FlowVelocity");
	
	public static final IRI Fluid = c("Fluid");
	public static final IRI Water = c("Water");
	
	public static final IRI OceanographicDevice = c("OceanographicDevice");
	public static final IRI CurrentMeter = c("CurrentMeter");
	public static final IRI HydroacousticCurrentMeter = c("HydroacousticCurrentMeter");
	public static final IRI AcousticDopplerCurrentProfiler = c("AcousticDopplerCurrentProfiler");
	public static final IRI PartialPressureOfCO2Analyzer = c("PartialPressureOfCO2Analyzer");

	private static IRI c(String suffix) {
		return IRI.create(ns.toString(), suffix);
	}

}
