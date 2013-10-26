package br.ufrj.ppgi.greco.infmod.config;

import br.ufrj.ppgi.greco.infmod.ResultHandler;
import br.ufrj.ppgi.greco.infmod.impl.DefaultResultHandler;


//@XmlRootElement(name="DefaultResultHandler")
//@XmlAccessorType(XmlAccessType.FIELD)
public class XsDefaultResultHandler extends XsResultHandler {

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

	@Override
	public ResultHandler createResultHandler() {
		return new DefaultResultHandler();
	}

}
