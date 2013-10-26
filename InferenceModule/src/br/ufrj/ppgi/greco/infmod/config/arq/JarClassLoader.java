package br.ufrj.ppgi.greco.infmod.config.arq;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassLoader extends ClassLoader {
	
    private JarFile jarFile = null;	//Path to the jar file
    private Hashtable <String, Class<?>> classes = new Hashtable<String, Class<?>> (); //used to cache already defined classes

    public JarClassLoader(JarFile jarFile) {
        super(JarClassLoader.class.getClassLoader()); //calls the parent class loader's constructor
    	this.jarFile = jarFile;
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    public Class<?> findClass(String className) {
        byte classByte[];
        Class<?> result = null;

        result = (Class<?>) classes.get(className); //checks in cached classes
        if (result != null) {
            return result;
        }

        try {
            return findSystemClass(className);
        } catch (Exception e) {
        }

        try {
            String classFilePath = className.replaceAll("\\.", "/") + ".class";
            JarEntry entry = jarFile.getJarEntry(classFilePath);
            InputStream is = jarFile.getInputStream(entry);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int nextValue = is.read();
            while (-1 != nextValue) {
                byteStream.write(nextValue);
                nextValue = is.read();
            }

            classByte = byteStream.toByteArray();
            result = defineClass(className, classByte, 0, classByte.length, null);
            classes.put(className, result);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
