package br.ufrj.ppgi.greco.infmod.config.arq;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name="config")
@XmlAccessorType (XmlAccessType.FIELD)
public class Config
{
	@XmlElement (name="namespace")
	private
	List <Namespace> namespaces;
	
	@Override
	public String toString() {
		return getNamespaces().toString();
	}

	public void setNamespaces(List <Namespace> namespaces) {
		this.namespaces = namespaces;
	}

	public List <Namespace> getNamespaces() {
		return namespaces;
	}
}