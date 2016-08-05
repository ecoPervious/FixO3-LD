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
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.pangaea.vocab.EYP;
import de.pangaea.vocab.FIXO3;
import de.pangaea.vocab.SSN;
import de.pangaea.vocab.Schema;

import static de.pangaea.vocab.FIXO3.OceanObservatory;
import static de.pangaea.vocab.FIXO3.FixedPointOceanObservatory;
import static de.pangaea.vocab.SSN.Platform;
import static de.pangaea.vocab.SSN.attachedSystem;

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

	private final String schemaFile = "file:///home/ms/workspace-sensdatatran/EsonetYellowPagesSparql/src/main/resources/ontologies/schema.rdf";
	private final String eypFile = "file:///home/ms/workspace-sensdatatran/EsonetYellowPagesSparql/src/main/resources/ontologies/esonetyellowpages.rdf";

	private final String fixO3ObservatoriesFile = "src/main/resources/data/fixo3-observatories.json";
	private final String fixO3File = "src/main/resources/ontologies/fixo3.rdf";
	private final String fixO3InferredFile = "src/main/resources/ontologies/fixo3-inferred.rdf";

	private OntologyManager m;

	private void run()
			throws OWLOntologyCreationException, OWLOntologyStorageException, NoSuchAlgorithmException, IOException {
		m = new OntologyManager(FIXO3.ns);
		m.addVersion("0.1");
		m.addDate("2016-08-01");
		m.addCreator("Markus Stocker");
		m.addSeeAlso("http://www.fixo3.eu/");
		m.addImport(Schema.ns, IRI.create(schemaFile));
		m.addImport(SSN.ns);
		m.addImport(EYP.ns, IRI.create(eypFile));

		m.addClass(OceanObservatory);
		m.addLabel(OceanObservatory, "Ocean Observatory");
		m.addSubClass(OceanObservatory, Platform);

		m.addClass(FixedPointOceanObservatory);
		m.addLabel(FixedPointOceanObservatory, "Fixed-Point Ocean Observatory");
		m.addSubClass(FixedPointOceanObservatory, OceanObservatory);

		JsonReader jr = Json.createReader(new FileReader(new File(fixO3ObservatoriesFile)));
		JsonArray ja = jr.readArray();

		for (JsonObject jo : ja.getValuesAs(JsonObject.class)) {
			String label = jo.getString("label");
			String title = jo.getString("title");
			String comment = jo.getString("comment");
			String source = jo.getString("source");
			JsonArray attachedSystems = jo.getJsonArray("attachedSystems");

			addFixedOceanObservatory(label, title, comment, IRI.create(source), attachedSystems);
		}

		m.save(fixO3File);
		m.saveInferred(fixO3InferredFile);
	}

	private void addFixedOceanObservatory(String label, String title, String comment, IRI source,
			JsonArray attachedSystems) {
		IRI observatory = IRI.create(FIXO3.ns.toString() + md5Hex(label));

		m.addIndividual(observatory);
		m.addType(observatory, FixedPointOceanObservatory);
		m.addLabel(observatory, label);
		m.addTitle(observatory, title);
		m.addComment(observatory, comment);
		m.addSource(observatory, source);

		if (attachedSystems == null)
			return;

		for (JsonObject jo : attachedSystems.getValuesAs(JsonObject.class)) {
			IRI system = IRI.create(FIXO3.ns.toString() + md5Hex(jo.getString("label")));

			m.addIndividual(system);
			m.addType(system, IRI.create(jo.getString("type")));
			m.addLabel(system, jo.getString("label"));
			m.addObjectAssertion(observatory, attachedSystem, system);
		}
	}

	public static void main(String[] args)
			throws OWLOntologyCreationException, OWLOntologyStorageException, NoSuchAlgorithmException, IOException {
		CreateFixO3 app = new CreateFixO3();
		app.run();
	}

}
