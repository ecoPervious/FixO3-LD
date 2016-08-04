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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * <p>
 * Title: CreateSchemaOrg
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

public class CreateSchemaOrg {

	private final IRI ns = IRI.create("http://schema.org/");
	private final String file = "src/main/resources/ontologies/schemaorg.rdf";

	private void run() throws OWLOntologyCreationException, OWLOntologyStorageException {
		OntologyManager m = new OntologyManager(ns);

		IRI unitCode = IRI.create("http://schema.org/unitCode");
		IRI value = IRI.create("http://schema.org/value");
		IRI quantitativeValue = IRI.create("http://schema.org/QuantitativeValue");

		m.addObjectProperty(unitCode);
		m.addLabel(unitCode, "unit code");
		m.addComment(unitCode,
				"The unit of measurement given using the UN/CEFACT Common Code (3 characters) or a URL. Other codes than the UN/CEFACT Common Code may be used with a prefix followed by a colon.");

		m.addDataProperty(value);
		m.addLabel(value, "value");
		m.addComment(value,
				"The value of the quantitative value or property value node. For QuantitativeValue and MonetaryValue, the recommended type for values is &apos;Number&apos;. For PropertyValue, it can be &apos;Text;&apos;, &apos;Number&apos;, &apos;Boolean&apos;, or &apos;StructuredValue&apos;.");

		m.addClass(quantitativeValue);
		m.addLabel(quantitativeValue, "Quantitative Value");
		m.addComment(quantitativeValue, "A point value or interval for product characteristics and other purposes.");
		m.addObjectSome(quantitativeValue, unitCode, OWLRDFVocabulary.OWL_THING);
		m.addDataAll(quantitativeValue, value, OWL2Datatype.XSD_FLOAT);

		m.save(file);
	}

	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		CreateSchemaOrg app = new CreateSchemaOrg();
		app.run();
	}
}
