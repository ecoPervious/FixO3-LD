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

package de.pangaea.vocab;

import org.semanticweb.owlapi.model.IRI;

/**
 * <p>
 * Title: FIXO3
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

public class FIXO3 {

	public static final IRI ns = IRI.create("http://fixo3.eu/vocab/");

	public static final IRI OceanObservatory = c("OceanObservatory");
	public static final IRI FixedPointOceanObservatory = c("FixedPointOceanObservatory");

	private static IRI c(String suffix) {
		return IRI.create(ns.toString(), suffix);
	}
	
}
