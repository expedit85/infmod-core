package br.ufrj.ppgi.greco.expedit.tese.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

public class SnorqlPublisher {

	public static Servlet createDefaultServlet(
			final String snorqlPath,
			final Map<String, String> prefixesMap,
			final String sparqlServerPath) 
	{
		return new DefaultServlet() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void doPost(HttpServletRequest request,
					HttpServletResponse response) throws ServletException,
					IOException {
				if (request.getPathInfo() == null) response.sendRedirect("/snorql/");
				else super.doPost(request, response);
			}
			
			@Override
			protected void doGet(HttpServletRequest request,
					HttpServletResponse response) throws ServletException,
					IOException {
				if (request.getPathInfo() == null) response.sendRedirect("/snorql/");
				else super.doGet(request, response);
			}

			@Override
			public Resource getResource(String subpath) {
				try {
					if (subpath.equals("/snorql")) return null;
					if (subpath.matches("/?snorql/")) subpath += "index.html";

					Resource resource = null;

					URL url = SnorqlPublisher.class.getResource(SnorqlPublisher.class.getSimpleName() + ".class");
					if (url.toString().startsWith("jar:")) {	// within JAR
						subpath = subpath.replaceFirst("/?snorql/", "/");
						url = ClassLoader.getSystemResource(subpath);
						resource = Resource.newClassPathResource(subpath);
					}
					else {			// common file
						File path = new File(snorqlPath, subpath);
						resource = Resource.newResource(path.getAbsolutePath());
					}
					
					if (subpath.endsWith("/namespaces.js")) {

						InputStream is = resource.getInputStream();
						StreamResource r2 = new StreamResource();

						int b = -1;
						OutputStream os = r2.getOutputStream();
						while ((b = is.read() ) != -1) {
							os.write(b);
						}

						OutputStreamWriter osw = new OutputStreamWriter(os);
						
						if (prefixesMap != null && prefixesMap.size() > 0) {
						
							for (Map.Entry<String, String> entry : prefixesMap.entrySet()) {
								osw.write(String.format("\t%s: '%s',\n", entry.getKey(), entry.getValue()));
							}
						}
						
						osw.write("};\n");
						osw.write("var sparqlServerPath = '"
								+ sparqlServerPath.replaceAll("^\\/?([^\\/]+)\\/?$", "$1")
								+ "';\n");
						
						osw.flush();
						
						resource = r2;
					}

					
					return resource;
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		};
	}


	public static void publishSnorqlContent(ServletContextHandler context, String snorqlPath, Map<String, String> prefixesMap, String sparqlServerPath) {
		Servlet servlet = SnorqlPublisher.createDefaultServlet(snorqlPath, prefixesMap, sparqlServerPath);
		ServletHolder sh = new ServletHolder(servlet);
		sh.setInitParameter("gzip", "false"); //ver
		context.addServlet(sh, "/snorql/*");
	}


	// TODO conteudo dentro do jar (no futuro)	
	public static void publishSnorqlContent(Server jettyServer, String snorqlPath, Map<String, String> prefixesMap, String sparqlServerPath) {
        ServletContextHandler context = (ServletContextHandler) jettyServer.getHandler() ;
        publishSnorqlContent(context, snorqlPath, prefixesMap, sparqlServerPath);
	}
}
