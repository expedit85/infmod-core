package br.ufrj.ppgi.greco.infmod;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;

import br.ufrj.ppgi.greco.expedit.tese.commons.clock.CachedNetworkClock;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.Clock;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.ClockException;
import br.ufrj.ppgi.greco.expedit.tese.commons.clock.HandledClockAdapter;
import br.ufrj.ppgi.greco.infmod.config.Utils;
import br.ufrj.ppgi.greco.infmod.config.XsInferenceModule;
import br.ufrj.ppgi.greco.infmod.config.arq.ArqFunctionLoader;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {

		// Gambiarra para descobrir se está em Debug mode
		if ("true".equals(System.getProperty("infmod.debug")))
		{
			System.out.println("Debug mode detected: waiting 6 seconds");
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		/*
		 * Carrega XML de configuração e cria InferenceModule
		 */
		File file = new File(args[0]);
		XsInferenceModule infMod = Utils.loadConfigFromFile(file);
		
		if (infMod != null) {
			System.out.println("Config File loaded.");
		}
		else {
			System.out.println("Config file not loaded");
			System.out.flush();
			System.exit(1);
		}
		
		
		/*
		 * Register SPARQL custom functions
		 */
		ArqFunctionLoader.load();
	
		
		/*
		 * Creates an instance of network clock 
		 */
		Clock clk = null;
		try
		{
			CachedNetworkClock netclk = null;
			if (infMod.clock != null && infMod.clock.baseUri != null)
			{
				URI uri = URI.create(infMod.clock.baseUri);
				netclk = CachedNetworkClock.createInstance(uri);
			}
			else
			{
				netclk = CachedNetworkClock.createInstance();
			}
			clk = new HandledClockAdapter(netclk);
		}
		catch (ClockException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
		InferenceModule infmod = infMod.createInferenceModule(clk);
		
		if (infmod != null) {
			System.out.println("InferenceModule created: " + infmod.getName());
		}
		else {
			System.out.println("InferenceModule could not be created");
			System.out.flush();
			System.exit(1);
		}


		/*
		 * Waits for clock
		 */
		System.out.print("Waiting for clock... ");
		try {
			long time0 = clk.getCurrentTime();
			while (true) {
				try {
					if (clk.getCurrentTime() - time0 > 0) break;
					else Thread.sleep(10);
				} catch (ClockException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (ClockException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("Ok!");
		
		/*
		 * Starts the inference module & wait for termination
		 */
		infmod.start();
		
		waitForInfMod(infmod, clk);
		
		infmod.stop();
	}
	

	public static void waitForInfMod(InferenceModule infmod, Clock clk) {
		waitForClock(clk);
	}
	
	public static void waitForClock(Clock clk) {
		try {
			while (clk.isAlive()) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (ClockException e) {
			e.printStackTrace();
		}
	}
}
