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
 * Title: Schema
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

public class Schema {

	public static final IRI ns = IRI.create("http://schema.org/");

	public static final IRI QuantitativeValue = c("QuantitativeValue");
	
	public static final IRI unitCode = c("unitCode");
	public static final IRI value = c("value");

	private static IRI c(String suffix) {
		return IRI.create(ns.toString(), suffix);
	}
	
}
