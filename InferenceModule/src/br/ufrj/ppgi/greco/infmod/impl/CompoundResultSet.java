package br.ufrj.ppgi.greco.infmod.impl;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class CompoundResultSet implements ResultSet {
	
	private ArrayList<ResultSet> resultSets = new ArrayList<ResultSet>();
	private int index = 0;
	
	
	public void add(ResultSet rs)
	{
		resultSets.add(rs);
	}
	
	

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Calling remove() of CompoundResultSet object is not allowed");
	}

	@Override
	public boolean hasNext() {
		if (index < resultSets.size() && resultSets.get(index).hasNext()) return true;
		else if (index+1 < resultSets.size() && resultSets.get(index+1).hasNext()) return true;
		return false;
	}

	@Override
	public QuerySolution next() {
		if (resultSets.get(index).hasNext()) return resultSets.get(index).next();
		else if (index < resultSets.size() - 1) return resultSets.get(++index).next();
		return null;
	}

	@Override
	public QuerySolution nextSolution() {
		return next();
	}

	@Override
	public Binding nextBinding() {
		//return resultSets.get(index).nextBinding();
		if (resultSets.get(index).hasNext()) return resultSets.get(index).nextBinding();
		else if (index < resultSets.size() - 1) return resultSets.get(++index).nextBinding();
		return null;
	}

	@Override
	public int getRowNumber() {
		return resultSets.get(index).getRowNumber();
	}

	@Override
	public List<String> getResultVars() {
		return resultSets.get(index).getResultVars();
	}

	@Override
	public Model getResourceModel() {
		return resultSets.get(index).getResourceModel();
	}

}
