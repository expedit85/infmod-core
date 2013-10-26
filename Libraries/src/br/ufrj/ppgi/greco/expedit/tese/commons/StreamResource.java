package br.ufrj.ppgi.greco.expedit.tese.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.util.resource.Resource;

public class StreamResource extends Resource {
	
	
	private StringBuilder sb = new StringBuilder();
	private OutputStream os = null;
	
	public StreamResource() {}
	

	@Override
	public Resource addPath(String arg0) throws IOException,
			MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete() throws SecurityException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public File getFile() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		
		final String s = sb.toString();
		sb = null;

		return new InputStream() {
			
			private int i = 0;
			
			@Override
			public int read() throws IOException {

				try {
					return s.charAt(i++);
				}
				catch (Exception e) {
					return -1;
				}
			}
		};
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException, SecurityException {
		
		if (os != null) return os;

		os = new OutputStream() {
			
			@Override
			public void write(int b) throws IOException {
				sb.append( (char) (b & 0xFF));
			}
		};
		
		return os;
	}

	@Override
	public URL getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isContainedIn(Resource arg0) throws MalformedURLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDirectory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long lastModified() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long length() {
		// TODO Auto-generated method stub
		return sb.length();
	}

	@Override
	public String[] list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renameTo(Resource arg0) throws SecurityException {
		// TODO Auto-generated method stub
		return false;
	}

}
