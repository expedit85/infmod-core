package br.ufrj.ppgi.greco.infmod.config.arq;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType (XmlAccessType.FIELD)
public class Namespace
{
	@XmlAttribute
	private
	String uri;
	
	@XmlElement (name="function")
	private
	List <Function> funtions;
	
	@Override
	public String toString() {
		return String.format("<%s>: %s", getUri(), getFuntions().toString());
	}

	public void setFuntions(List <Function> funtions) {
		this.funtions = funtions;
	}

	public List <Function> getFuntions() {
		return funtions;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	
}