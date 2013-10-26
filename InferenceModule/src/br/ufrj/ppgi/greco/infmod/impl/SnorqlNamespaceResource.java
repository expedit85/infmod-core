package br.ufrj.ppgi.greco.infmod.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.util.resource.Resource;

// TODO fazer isso quando houver possibilidade
public class SnorqlNamespaceResource extends Resource {

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
		return false;
	}

	@Override
	public File getFile() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException, SecurityException {
		// TODO Auto-generated method stub
		return null;
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
		return 0;
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
