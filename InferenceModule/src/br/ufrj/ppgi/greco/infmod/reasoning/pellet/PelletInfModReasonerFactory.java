package br.ufrj.ppgi.greco.infmod.reasoning.pellet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;

import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.mindswap.pellet.utils.VersionInfo;

import com.clarkparsia.pellet.rules.model.Rule;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.util.FileManager;

import br.ufrj.ppgi.greco.infmod.InfModReasonerFactory;

public class PelletInfModReasonerFactory implements InfModReasonerFactory<Rule> {

	protected static final String URI = "http://greco.ppgi.ufrj.br/infmod/reasonerfactory#pellet";
	
	private List <String> ontologies;
	private Model schema;
	
	
	public PelletInfModReasonerFactory() {
		ontologies = new ArrayList<String>();
		schema = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		init();
	}

	
	@Override
	public void addOntology(String uri) {
		ontologies.add(uri);
	}
	
	
	@Override
	public void addRule(Rule rule) {
		throw new UnsupportedOperationException("rule addition not supported yet");
	}

	@Override
	public void addRules(List<Rule> rules) {
		throw new UnsupportedOperationException("rule addition not supported yet");
	}

	
	
	private void init() {

		try
		{
			File file = resourceToFile("logging.properties");
			LogManager.getLogManager().reset(); // This blows away existing configuration
			LogManager.getLogManager().readConfiguration(new FileInputStream(file));

			file = resourceToFile("pellet.properties");
			//System.out.println("Loading options from " + file.toURI().toURL().toString());
			PelletOptions.load(file.toURI().toURL());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	private static File resourceToFile(String resName) {
		File tempFile = null;
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(resName);
			InputStreamReader isr = new InputStreamReader(is);
			
			String property = "java.io.tmpdir";
			String tempDir = System.getProperty(property);
			tempFile = new File(tempDir, resName);
			
			FileWriter fw = new FileWriter(tempFile);
			
			int c = -1;
			while ((c = isr.read()) != -1) fw.write(c);
			
			isr.close();
			fw.close();
			
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		return tempFile;
	}
	
	
	private boolean loadSchema()
	{
		boolean useOwl = false;
		if (ontologies.size() > 0)
		{
			int size = 0;
			useOwl = true;
			for (String uri : ontologies)
			{
				Model m = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
				m = FileManager.get().readModel(m, uri);
				schema.add(m);
				size += m.size();
			}
			
			System.out.println("At least " + Math.max(size, schema.size()) 
					+ " triples were added to the schema (TBox)");
		}
		return useOwl;
	}
	

	@Override
	public Reasoner create(Resource configuration)
	{
		Reasoner reasoner = PelletReasonerFactory.theInstance().create(configuration);
		
		loadSchema();
		
		// Bind schema to the reasoner
		if (reasoner != null && schema.size() > 0)
			reasoner = reasoner.bindSchema(schema);
		
		
		return reasoner;
	}

	@Override
	public Model getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return URI;
	}


	@Override
	public String getDescription() {
		return String.format(
			"Pellet %s (release date: %s)",
				VersionInfo.getInstance().getVersionString(),
				VersionInfo.getInstance().getReleaseDate());
	}
}
