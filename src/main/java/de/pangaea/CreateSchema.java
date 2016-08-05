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

package de.pangaea;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import static de.pangaea.vocab.Schema.ns;
import static de.pangaea.vocab.Schema.unitCode;
import static de.pangaea.vocab.Schema.value;
import static de.pangaea.vocab.Schema.QuantitativeValue;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.XSD_FLOAT;
import static org.semanticweb.owlapi.vocab.OWLRDFVocabulary.OWL_THING;

/**
 * <p>
 * Title: CreateSchema
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

public class CreateSchema {

	private final String schemaFile = "src/main/resources/ontologies/schema.rdf";
	private final String schemaInferredFile = "src/main/resources/ontologies/schema-inferred.rdf";

	private void run() throws OWLOntologyCreationException, OWLOntologyStorageException {
		OntologyManager m = new OntologyManager(ns);

		m.addObjectProperty(unitCode);
		m.addLabel(unitCode, "unit code");
		m.addComment(unitCode,
				"The unit of measurement given using the UN/CEFACT Common Code (3 characters) or a URL. Other codes than the UN/CEFACT Common Code may be used with a prefix followed by a colon.");

		m.addDataProperty(value);
		m.addLabel(value, "value");
		m.addComment(value,
				"The value of the quantitative value or property value node. For QuantitativeValue and MonetaryValue, the recommended type for values is &apos;Number&apos;. For PropertyValue, it can be &apos;Text;&apos;, &apos;Number&apos;, &apos;Boolean&apos;, or &apos;StructuredValue&apos;.");

		m.addClass(QuantitativeValue);
		m.addLabel(QuantitativeValue, "Quantitative Value");
		m.addComment(QuantitativeValue, "A point value or interval for product characteristics and other purposes.");
		m.addObjectSome(QuantitativeValue, unitCode, OWL_THING);
		m.addDataAll(QuantitativeValue, value, XSD_FLOAT);

		m.save(schemaFile);
		m.saveInferred(schemaInferredFile);
	}

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		CreateSchema app = new CreateSchema();
		app.run();
	}
}
