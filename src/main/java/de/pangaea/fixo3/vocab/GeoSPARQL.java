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
 * Title: GeoSPARQL
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

public class GeoSPARQL {

	public static final IRI ns = IRI.create("http://www.opengis.net/ont/geosparql#");
	
	public static final IRI Feature = c("Feature");
	
	public static final IRI hasGeometry = c("hasGeometry");
	
	public static final IRI asWKT = c("asWKT");
	
	public static final IRI wktLiteral = c("wktLiteral");
	
	private static IRI c(String suffix) {
		return IRI.create(ns.toString(), suffix);
	}
	
}
