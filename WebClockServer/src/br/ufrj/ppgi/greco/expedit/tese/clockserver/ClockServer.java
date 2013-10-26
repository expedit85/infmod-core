package br.ufrj.ppgi.greco.expedit.tese.clockserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import br.ufrj.ppgi.greco.expedit.tese.commons.SnorqlPublisher;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class ClockServer {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		System.out.println("Starting clock server...");
		
		int port = 3030;
		if (args.length == 2) {
			if ("-p".equals(args[0])) port = Integer.parseInt(args[1]);
		}

		ServletHolder sh = new ServletHolder(ServletContainer.class);

		sh.setInitParameter(
				"com.sun.jersey.config.property.resourceConfigClass",
		 		"com.sun.jersey.api.core.PackagesResourceConfig");
		sh.setInitParameter(
				"com.sun.jersey.config.property.packages",
				"br.ufrj.ppgi.greco.expedit.tese.clockserver");

		Server server = new Server(port);
		
		
		ServletContextHandler sch = new ServletContextHandler(server, "/");
		sch.addServlet(sh, "/");
		SnorqlPublisher.publishSnorqlContent(sch, ".", null, "");

		
		server.start();

		System.out.println("Server started at port " + port);
		
		System.out.println("Type {domain}/help into your browser for help.\nTo exit type Ctrl+C.");
		
		server.join();
	}
}
