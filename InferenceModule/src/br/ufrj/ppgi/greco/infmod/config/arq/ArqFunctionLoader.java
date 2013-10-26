
package br.ufrj.ppgi.greco.infmod.config.arq;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import br.ufrj.ppgi.greco.infmod.InferenceModuleVocabulary;

import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;


public class ArqFunctionLoader {

	/**
	 * Metodo magico que carrega todas as funcoes dos jars e registra para usar no SPARQL.
	 * 
	 * <p>
	 * Regras:
	 * <ol>
	 *   <li>add the file 'META-INF/infmod.config.xml' to the jar.</li>
	 *   <li>java -Dinfmod.function.path=<';'-separed jar list> ...</li>
	 * </ol>
	 * </p>
	 * <p>
	 * The XML:
	 *   <pre>
	 *     &lt;?xml version="1.0" encoding="UTF-8"?>
	 *     &lt;config>
	 *       &lt;namespace uri="http://geo.infmod.org/function#">
	 *         &lt;function class="br.ufrj.ppgi.greco.expedit.tese.commons.geo.sparql.fn.contains" />
	 *         &lt;function class="&lt;other function class name>" />
	 *       &lt;/namespace>
	 *       &lt;namespace uri="...">
	 *         &lt;function class="..." />
	 *         &lt;function class="..." />
	 *         ...
	 *       &lt;/namespace>
	 *       ...
	 *     &lt;/config>
	 *   </pre>
	 * </p>
	 * @Return the number of loaded and registered classes
	 */
	public static int load()
	{
		loadDefaults();
		
		int result = 0;
		
		String path = System.getProperties().getProperty("infmod.function.path", null);
		
		if (path == null) return result;
		
		String [] paths = path.split(";");
		
		System.out.println("Registering custom function classes:");
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Config.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			for (String jarFile : paths)
			{
				if (jarFile.endsWith(".jar"))
				{
					try
					{
						JarFile jar = new JarFile(jarFile);
						JarEntry entry = jar.getJarEntry("META-INF/infmod.config.xml");
						InputStream is = jar.getInputStream(entry);
						
						Config config = (Config) unmarshaller.unmarshal(is);
						
						//System.out.println(config);
						
						JarClassLoader cld = new JarClassLoader(jar);
						
						
						
						for (Namespace ns : config.getNamespaces()) {

							for (Function fn : ns.getFuntions()) {

								try {
									Class<?> clazz = cld.loadClass(fn.getClazz());
									
									String fnUri = ns.getUri() + ( fn.getName() == null ? clazz.getSimpleName() : fn.getName() );
									
									if (com.hp.hpl.jena.sparql.function.Function.class.isAssignableFrom(clazz))
									{
										System.out.println("- Value function: <" + fnUri + ">");
										FunctionRegistry.get().put(fnUri, clazz);
										result++;
									}
									else if (com.hp.hpl.jena.sparql.pfunction.PropertyFunction.class.isAssignableFrom(clazz))
									{
										System.out.println("- Property function: <" + fnUri + ">");
										PropertyFunctionRegistry.get().put(fnUri, clazz);
										result++;
									}
									else System.out.println("Unrecognized function: " + clazz.getCanonicalName());
									
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
							}
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch (JAXBException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return result;
	}
	
	
	private static void loadDefaults()
	{
		FunctionRegistry.get().put(InferenceModuleVocabulary.getURI() + "sumTime", SumDateTimeFunction.class);
		FunctionRegistry.get().put(InferenceModuleVocabulary.getURI() + "getLastIndexOf", GetLastIndexOfFunction.class);
	}
}
