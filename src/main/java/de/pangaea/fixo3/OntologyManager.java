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
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.AddOntologyAnnotation;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDataPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentDataPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredInverseObjectPropertiesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredObjectPropertyCharacteristicAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * <p>
 * Title: OntologyManager
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

public class OntologyManager {

	private OWLOntology o;
	private OWLOntologyManager m = OWLManager.createOWLOntologyManager();
	private OWLDataFactory df = OWLManager.getOWLDataFactory();

	private static final String dc = "http://purl.org/dc/elements/1.1/";

	public OntologyManager(IRI ns) throws OWLOntologyCreationException {
		o = m.createOntology(ns);
	}

	public void materialize() throws OWLOntologyCreationException {
		ReasonerFactory rf = new ReasonerFactory();

		Configuration c = new Configuration();
        
		c.ignoreUnsupportedDatatypes = true;
        
		OWLReasoner r = rf.createReasoner(o, c);

		r.precomputeInferences(InferenceType.CLASS_HIERARCHY, InferenceType.CLASS_ASSERTIONS,
				InferenceType.OBJECT_PROPERTY_HIERARCHY, InferenceType.DATA_PROPERTY_HIERARCHY,
				InferenceType.OBJECT_PROPERTY_ASSERTIONS);

		List<InferredAxiomGenerator<? extends OWLAxiom>> gs = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();

		gs.add(new InferredSubClassAxiomGenerator());
		gs.add(new InferredClassAssertionAxiomGenerator());
		gs.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
		gs.add(new InferredPropertyAssertionGenerator());
		gs.add(new InferredDataPropertyCharacteristicAxiomGenerator());
		gs.add(new InferredDisjointClassesAxiomGenerator());
		gs.add(new InferredEquivalentClassAxiomGenerator());
		gs.add(new InferredEquivalentDataPropertiesAxiomGenerator());
		gs.add(new InferredEquivalentObjectPropertyAxiomGenerator());
		gs.add(new InferredInverseObjectPropertiesAxiomGenerator());
		gs.add(new InferredSubDataPropertyAxiomGenerator());
		gs.add(new InferredSubObjectPropertyAxiomGenerator());

		InferredOntologyGenerator iog = new InferredOntologyGenerator(r, gs);
		iog.fillOntology(m, o);
	}

	public void saveInferred(String file) throws OWLOntologyCreationException, OWLOntologyStorageException {
		materialize();
		save(file);
	}

	public void save(String file) throws OWLOntologyStorageException {
		m.saveOntology(o, new FileDocumentTarget(new File(file)));
	}

	public void addImport(IRI iri, IRI mapping) throws OWLOntologyCreationException {
		m.addIRIMapper(new SimpleIRIMapper(iri, mapping));
		addImport(iri);
	}

	public void addImport(IRI iri) throws OWLOntologyCreationException {
		m.loadOntology(iri);
		addImport(df.getOWLImportsDeclaration(iri));
	}

	public void addClass(IRI iri) {
		addAxiom(df.getOWLDeclarationAxiom(df.getOWLEntity(EntityType.CLASS, iri)));
	}

	public void addObjectProperty(IRI iri) {
		addAxiom(df.getOWLDeclarationAxiom(df.getOWLEntity(EntityType.OBJECT_PROPERTY, iri)));
	}

	public void addDataProperty(IRI iri) {
		addAxiom(df.getOWLDeclarationAxiom(df.getOWLEntity(EntityType.DATA_PROPERTY, iri)));
	}

	public void addIndividual(IRI iri) {
		addAxiom(df.getOWLDeclarationAxiom(df.getOWLEntity(EntityType.NAMED_INDIVIDUAL, iri)));
	}
	
	public void addClassAssertion(IRI cls, IRI ind) {
		addAxiom(df.getOWLClassAssertionAxiom(getClass(cls), getIndividual(ind)));
	}

	public void addSubClass(IRI sub, IRI sup) {
		addAxiom(df.getOWLSubClassOfAxiom(getClass(sub), getClass(sup)));
	}
	
	public void addEquivalentClass(IRI cls1, IRI cls2) {
		addAxiom(df.getOWLEquivalentClassesAxiom(getClass(cls1), getClass(cls2)));
	}

	public void addObjectSome(IRI cls, IRI property, OWLRDFVocabulary value) {
		addObjectSome(cls, property, value.getIRI());
	}

	public void addObjectSome(IRI cls, IRI property, IRI value) {
		addAxiom(df.getOWLSubClassOfAxiom(getClass(cls),
				df.getOWLObjectSomeValuesFrom(getObjectProperty(property), getClass(value))));
	}

	public void addObjectAll(IRI cls, IRI property, IRI value) {
		addAxiom(df.getOWLSubClassOfAxiom(getClass(cls),
				df.getOWLObjectAllValuesFrom(getObjectProperty(property), getClass(value))));
	}

	public void addObjectValue(IRI cls, IRI property, IRI value) {
		addAxiom(df.getOWLSubClassOfAxiom(getClass(cls),
				df.getOWLObjectHasValue(getObjectProperty(property), getIndividual(value))));
	}

	public void addDataSome(IRI cls, IRI property, OWL2Datatype value) {
		addAxiom(df.getOWLSubClassOfAxiom(getClass(cls),
				df.getOWLDataSomeValuesFrom(getDataProperty(property), value.getDatatype(df))));
	}

	public void addDataAll(IRI cls, IRI property, OWL2Datatype value) {
		addAxiom(df.getOWLSubClassOfAxiom(getClass(cls),
				df.getOWLDataAllValuesFrom(getDataProperty(property), value.getDatatype(df))));
	}

	public void addDataValue(IRI cls, IRI property, String value) {
		addAxiom(df.getOWLSubClassOfAxiom(getClass(cls),
				df.getOWLDataHasValue(getDataProperty(property), df.getOWLLiteral(value, OWL2Datatype.XSD_STRING))));
	}

	public void addLabel(IRI iri, String label) {
		addAnnotation(iri, getLabel(label));
	}

	public void addComment(IRI iri, String comment) {
		addAnnotation(iri, getComment(comment));
	}

	public void addSource(IRI iri, IRI source) {
		addAnnotation(iri, getSource(source));
	}

	public void addTitle(IRI iri, String title) {
		addAnnotation(iri, getTitle(title));
	}

	public void addTitle(String title) {
		addOntologyAnnotation(getTitle(title));
	}

	public void addVersion(String version) {
		addOntologyAnnotation(getVersion(version));
	}

	public void addDate(String date) {
		addOntologyAnnotation(getDate(date));
	}

	public void addCreator(String creator) {
		addOntologyAnnotation(getCreator(creator));
	}

	public void addSeeAlso(IRI iri, String seeAlso) {
		addAnnotation(iri, getSeeAlso(seeAlso));
	}

	public void addSeeAlso(String seeAlso) {
		addOntologyAnnotation(getSeeAlso(seeAlso));
	}

	public void addOntologyTitle(String title) {
		addOntologyAnnotation(
				df.getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create(dc, "title")), df.getOWLLiteral(title)));
	}

	public void addType(IRI individual, IRI type) {
		addAxiom(df.getOWLClassAssertionAxiom(getClass(type), getIndividual(individual)));
	}

	public void addObjectAssertion(IRI subject, IRI predicate, IRI object) {
		addAxiom(df.getOWLObjectPropertyAssertionAxiom(df.getOWLObjectProperty(predicate), getIndividual(subject),
				getIndividual(object)));
	}

	public void addDataAssertion(IRI subject, IRI predicate, Float value) {
		addAxiom(df.getOWLDataPropertyAssertionAxiom(df.getOWLDataProperty(predicate), getIndividual(subject),
				df.getOWLLiteral(value)));
	}

	public void addDataAssertion(IRI subject, IRI predicate, String value) {
		addAxiom(df.getOWLDataPropertyAssertionAxiom(df.getOWLDataProperty(predicate), getIndividual(subject),
				df.getOWLLiteral(value)));
	}

	public void addDataAssertion(IRI subject, IRI predicate, String value, IRI datatype) {
		addAxiom(df.getOWLDataPropertyAssertionAxiom(df.getOWLDataProperty(predicate), getIndividual(subject),
				df.getOWLLiteral(value, df.getOWLDatatype(datatype))));
	}

	private void addAnnotation(IRI iri, OWLAnnotation annotation) {
		addAxiom(df.getOWLAnnotationAssertionAxiom(iri, annotation));
	}

	private void addAxiom(OWLAxiom a) {
		m.applyChange(new AddAxiom(o, a));
	}

	private void addImport(OWLImportsDeclaration d) {
		m.applyChange(new AddImport(o, d));
	}

	private void addOntologyAnnotation(OWLAnnotation a) {
		m.applyChange(new AddOntologyAnnotation(o, a));
	}

	private OWLObjectProperty getObjectProperty(IRI iri) {
		return df.getOWLObjectProperty(iri);
	}

	private OWLDataProperty getDataProperty(IRI iri) {
		return df.getOWLDataProperty(iri);
	}

	private OWLClass getClass(IRI iri) {
		return df.getOWLClass(iri);
	}

	private OWLIndividual getIndividual(IRI iri) {
		return df.getOWLNamedIndividual(iri);
	}

	private OWLAnnotation getLabel(String label) {
		return df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
				df.getOWLLiteral(label, "en"));
	}

	private OWLAnnotation getComment(String comment) {
		return df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_COMMENT.getIRI()),
				df.getOWLLiteral(comment, "en"));
	}

	private OWLAnnotation getSource(IRI source) {
		return df.getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create(dc, "source")),
				df.getOWLLiteral(source.toString(), OWL2Datatype.XSD_ANY_URI));
	}

	private OWLAnnotation getTitle(String title) {
		return df.getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create(dc, "title")), df.getOWLLiteral(title, "en"));
	}

	private OWLAnnotation getVersion(String version) {
		return df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_VERSION_INFO.getIRI()),
				df.getOWLLiteral(version));
	}

	private OWLAnnotation getDate(String date) {
		return df.getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create(dc, "date")),
				df.getOWLLiteral(date, OWL2Datatype.XSD_DATE_TIME));
	}

	private OWLAnnotation getCreator(String creator) {
		return df.getOWLAnnotation(df.getOWLAnnotationProperty(IRI.create(dc, "creator")), df.getOWLLiteral(creator));
	}

	private OWLAnnotation getSeeAlso(String seeAlso) {
		return df.getOWLAnnotation(df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI()),
				df.getOWLLiteral(seeAlso, OWL2Datatype.XSD_ANY_URI));
	}

}
