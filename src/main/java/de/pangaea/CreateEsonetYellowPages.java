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
	private final String file = "src/main/resources/ontologies/esonetyellowpages.rdf";
	
	private void run() throws OWLOntologyCreationException, OWLOntologyStorageException {
		OntologyManager m = new OntologyManager(ns);
		
		m.save(file);
	}
	
	public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException {
		CreateEsonetYellowPages app = new CreateEsonetYellowPages();
		app.run();
	}

}
