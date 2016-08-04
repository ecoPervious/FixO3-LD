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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * <p>
 * Title:
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

public class CreateFixO3 {

	private final IRI ns = IRI.create("http://fixo3.eu/vocab/");
	private final IRI ssn = IRI.create("https://www.w3.org/ns/ssn/");

	private final String csvFile = "src/main/resources/data/fixo3-observatories.csv";
	private final String fixO3VocabFile = "src/main/resources/ontologies/fixo3-vocab.rdf";
	private final String fixO3VocabInferredFile = "src/main/resources/ontologies/fixo3-vocab-inferred.rdf";
	
	private IRI oceanObservatory = IRI.create("http://fixo3.eu/vocab/OceanObservatory");
	private IRI fixedPointOceanObservatory = IRI.create("http://fixo3.eu/vocab/FixedPointOceanObservatory");
	private IRI platform = IRI.create("https://www.w3.org/ns/ssn/Platform");

	private OntologyManager m;

	private void run()
			throws OWLOntologyCreationException, OWLOntologyStorageException, NoSuchAlgorithmException, IOException {
		m = new OntologyManager(ns);
		m.addVersion("0.1");
		m.addImport(ssn);

		m.addClass(oceanObservatory);
		m.addLabel(oceanObservatory, "Ocean Observatory");
		m.addSubClass(oceanObservatory, platform);

		m.addClass(fixedPointOceanObservatory);
		m.addLabel(fixedPointOceanObservatory, "Fixed-Point Ocean Observatory");
		m.addSubClass(fixedPointOceanObservatory, oceanObservatory);

		CSVParser parser = CSVParser.parse(new File(csvFile), Charset.forName("UTF-8"), CSVFormat.RFC4180);

		for (CSVRecord record : parser) {
			String label = record.get(0);
			String title = record.get(1);
			String comment = record.get(2);
			String source = record.get(3);

			addFixedOceanObservatory(label, title, comment, IRI.create(source));
		}

		m.save(fixO3VocabFile);
		m.saveInferred(fixO3VocabInferredFile);
	}

	private void addFixedOceanObservatory(String label, String title, String comment, IRI source) {
		IRI iri = IRI.create(ns.toString() + md5Hex(label));

		m.addIndividual(iri);
		m.addType(iri, fixedPointOceanObservatory);
		m.addLabel(iri, label);
		m.addTitle(iri, title);
		m.addComment(iri, comment);
		m.addSource(iri, source);
	}

	public static void main(String[] args)
			throws OWLOntologyCreationException, OWLOntologyStorageException, NoSuchAlgorithmException, IOException {
		CreateFixO3 app = new CreateFixO3();
		app.run();
	}

}
