package br.ufrj.ppgi.greco.expedit.tese.commons.clock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import br.ufrj.ppgi.greco.expedit.tese.commons.UTCXslDateTimeFormat;

public class NetworkClock implements Clock {

	private URI baseUri;
	private DateFormat fmt;
	private HttpClient persistentHttpClient = null;

	
	public NetworkClock(URI baseUri) {
		this.fmt = new UTCXslDateTimeFormat();
		this.baseUri = baseUri;
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", baseUri.getPort(),
											PlainSocketFactory.getSocketFactory()));

		ClientConnectionManager cm = new ThreadSafeClientConnManager(schemeRegistry);
		persistentHttpClient = new DefaultHttpClient(cm);
	}
	
	
	private static URI getFullUri(URI baseUri, String path, Map <String, String> params)
						throws URISyntaxException {

		String query = null;
		
		if (params != null) {
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();

			for (String key : params.keySet()) {
				qparams.add(new BasicNameValuePair(key, params.get(key)));
			}
			
			query = URLEncodedUtils.format(qparams, "UTF-8");
		}

		return URIUtils.createURI(
				baseUri.getScheme(),
				baseUri.getHost(),
				baseUri.getPort(),
				path,
				query,
				null);
	}
	
	private static String getResponseString(HttpClient httpClient, URI baseUri, String path)
				throws ClientProtocolException, IOException, URISyntaxException {
		return getResponseString(httpClient, baseUri, path, null);
	}
	
	private static String getResponseString(HttpClient httpClient, URI baseUri, String path, Map <String, String> params) throws ClientProtocolException, IOException, URISyntaxException {

		URI uri = getFullUri(baseUri, path, params);
		HttpGet get = new HttpGet(uri);
		HttpResponse response = httpClient.execute(get);

		InputStream is = response.getEntity().getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) sb.append(line);
	
		br.close();

		return sb.toString();
	}

	protected static String getClockURL() {
		String content = null;
		try {
			content = new Scanner(new File("clock_server.txt")).useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			content = "http://localhost:3030/";
			System.err.println("Using default URL for clock: " + content);
		}
		return content;
	}
	
	public static NetworkClock createInstance() throws ClockException
	{
		String clockUrl = getClockURL();
		NetworkClock clock = new NetworkClock(URI.create(clockUrl));
		return clock;
	}
	
	public static NetworkClock createInstance(URI clockUri) throws ClockException
	{
		NetworkClock clock = new NetworkClock(clockUri);
		return clock;
	}
	
	
	@Override
	public Date getCurrentDate() throws ClockException {
		try {
			return fmt.parse(getResponseString(persistentHttpClient, baseUri, "/datetime"));
		} catch (Exception e) {
			throw new ClockException(e);
		}
	}
	
	
	public String getCurrentDateAsString() throws ClockException {
		try {
			return getResponseString(persistentHttpClient, baseUri, "/datetime");
		} catch (Exception e) {
			throw new ClockException(e);
		}
	}
	

	@Override
	public long getCurrentTime() throws ClockException {
		try {
			return Long.parseLong(getResponseString(persistentHttpClient, baseUri, "/milliseconds"));
		} catch (Exception e) {
			throw new ClockException(e);
		}
	}
	
	@Override
	public boolean isAlive() throws ClockException {
		try {
			return Boolean.parseBoolean(getResponseString(persistentHttpClient, baseUri, "/isalive"));
		} catch (Exception e) {
			throw new ClockException(e);
		}
	}
	
	public void setSpeedRate(Double clockSpeed) throws ClockException {
		try {
			Map <String, String> params = new HashMap<String, String>();
			params.put("speedrate", clockSpeed.toString());
			getResponseString(persistentHttpClient, baseUri, "/configure", params); 
		} catch (Exception e) {
			throw new ClockException(e);
		}
	}
	
	public Double getSpeedRate() throws ClockException {
		String infoXml = "";
		try {
			infoXml = getResponseString(persistentHttpClient, baseUri, "/info");
			
			return ClockInfo.fromXML(infoXml).speedRate;
			
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage() + ": " + infoXml);
			throw new ClockException(new Exception(infoXml, e));
		}
	}
	
	
	public Date restart() throws ClockException {
		try {
			return fmt.parse(getResponseString(new DefaultHttpClient(), baseUri, "/restart"));
		} catch (Exception e) {
			throw new ClockException(e);
		}
	}
	
	
	public Date configure(Date startDate, Date finalDate, Double clockSpeed) throws ClockException {
		try {
			Map <String, String> params = new HashMap<String, String>();
			params.put("startdate", fmt.format(startDate));
			params.put("finaldate", fmt.format(finalDate));
			params.put("speedrate", clockSpeed.toString());

			String s = getResponseString(new DefaultHttpClient(), baseUri, "/configure", params); 

			return fmt.parse(s);
			
		} catch (Exception e) {
			throw new ClockException(e);
		}
	}
}
