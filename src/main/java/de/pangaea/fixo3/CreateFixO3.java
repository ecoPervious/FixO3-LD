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

package de.pangaea.fixo3;

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

import de.pangaea.fixo3.vocab.EYP;
import de.pangaea.fixo3.vocab.FIXO3;
import de.pangaea.fixo3.vocab.SSN;
import de.pangaea.fixo3.vocab.Schema;

import static de.pangaea.fixo3.vocab.Schema.location;
import static de.pangaea.fixo3.vocab.SF.Point;
import static de.pangaea.fixo3.vocab.GeoSPARQL.Feature;
import static de.pangaea.fixo3.vocab.GeoSPARQL.hasGeometry;
import static de.pangaea.fixo3.vocab.GeoSPARQL.asWKT;
import static de.pangaea.fixo3.vocab.GeoSPARQL.wktLiteral;
import static de.pangaea.fixo3.vocab.FIXO3.FixedPointOceanObservatory;
import static de.pangaea.fixo3.vocab.FIXO3.OceanObservatory;
import static de.pangaea.fixo3.vocab.SSN.Platform;
import static de.pangaea.fixo3.vocab.SSN.attachedSystem;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * <p>
 * Title: CreateFixO3
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

	private final String schemaFile = "file:///home/ms/workspace-sensdatatran/FixO3-LD/src/main/resources/thesis/schema.rdf";
	private final String eypFile = "file:///home/ms/workspace-sensdatatran/FixO3-LD/src/main/resources/thesis/esonetyellowpages.rdf";

	private final String fixO3ObservatoriesFile = "src/main/resources/thesis/fixo3-observatories.json";
	private final String fixO3File = "src/main/resources/thesis/fixo3.rdf";
	private final String fixO3InferredFile = "src/main/resources/thesis/fixo3-inferred.rdf";

	private OntologyManager m;

	private void run()
			throws OWLOntologyCreationException, OWLOntologyStorageException, NoSuchAlgorithmException, IOException {
		m = new OntologyManager(FIXO3.ns);
		m.addVersion("1.0");
		m.addDate("2016-11-23");
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
			addFixedOceanObservatory(jo);
		}

		m.save(fixO3File);
		m.saveInferred(fixO3InferredFile);
	}

	private void addFixedOceanObservatory(JsonObject jo) {
		String label;

		if (jo.containsKey("label"))
			label = jo.getString("label");
		else
			throw new RuntimeException("Label is expected [jo = " + jo + "]");

		IRI observatory = IRI.create(FIXO3.ns.toString() + md5Hex(label));

		m.addIndividual(observatory);
		m.addType(observatory, FixedPointOceanObservatory);
		m.addLabel(observatory, label);

		if (jo.containsKey("title")) {
			m.addTitle(observatory, jo.getString("title"));
		}
		if (jo.containsKey("comment")) {
			m.addComment(observatory, jo.getString("comment"));
		}
		if (jo.containsKey("source")) {
			m.addSource(observatory, IRI.create(jo.getString("source")));
		}
		if (jo.containsKey("location")) {
			JsonObject j = jo.getJsonObject("location");

			String place, coordinates;

			if (j.containsKey("place"))
				place = j.getString("place");
			else
				throw new RuntimeException("Place is expected [j = " + j + "]");

			if (j.containsKey("coordinates"))
				coordinates = j.getString("coordinates");
			else
				throw new RuntimeException("Coordinates are expected [j = " + j + "]");

			String fl = place + " @ " + coordinates;
			String gl = coordinates;

			IRI feature = IRI.create(FIXO3.ns.toString() + md5Hex(fl));
			IRI geometry = IRI.create(FIXO3.ns.toString() + md5Hex(gl));

			m.addIndividual(feature);
			m.addType(feature, Feature);
			m.addLabel(feature, fl);
			m.addObjectAssertion(observatory, location, feature);

			if (coordinates.startsWith("POINT"))
				m.addType(geometry, Point);
			else
				throw new RuntimeException("Coordinates not recognized, expected POINT [j = " + j + "]");

			m.addIndividual(geometry);
			m.addLabel(geometry, gl);
			m.addObjectAssertion(feature, hasGeometry, geometry);
			m.addDataAssertion(geometry, asWKT, coordinates, wktLiteral);
		}

		if (!jo.containsKey("attachedSystems"))
			return;

		JsonArray attachedSystems = jo.getJsonArray("attachedSystems");

		for (JsonObject j : attachedSystems.getValuesAs(JsonObject.class)) {
			String l, t;

			if (j.containsKey("label"))
				l = j.getString("label");
			else
				throw new RuntimeException("Label expected [j = " + j + "]");

			if (j.containsKey("type"))
				t = j.getString("type");
			else
				throw new RuntimeException("Type excepted [j = " + j + "]");

			IRI system = IRI.create(FIXO3.ns.toString() + md5Hex(l));

			m.addIndividual(system);
			m.addType(system, IRI.create(t));
			m.addLabel(system, l);
			m.addObjectAssertion(observatory, attachedSystem, system);
		}
	}

	public static void main(String[] args)
			throws OWLOntologyCreationException, OWLOntologyStorageException, NoSuchAlgorithmException, IOException {
		CreateFixO3 app = new CreateFixO3();
		app.run();
	}

}
