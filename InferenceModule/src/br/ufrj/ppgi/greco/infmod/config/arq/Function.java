package br.ufrj.ppgi.greco.infmod.config.arq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType (XmlAccessType.FIELD)
public class Function
{
	@XmlAttribute
	private String name;
	
	
	@XmlAttribute (name="class")
	private String clazz;
	
	public String toString() {
		return getClazz();
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getClazz() {
		return clazz;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	};
}