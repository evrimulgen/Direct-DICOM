package ua.ieeta.util;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reflector {
	static final Logger log = LoggerFactory.getLogger(Reflector.class);
	
	public static ClassLoader getClassLoader() {
		//FIX: change this loader when using OSGi
		return Reflector.class.getClassLoader();
	}
	
	public static InputStream getResourceAsStream(final String name) {
		return getClassLoader().getResourceAsStream(name);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T createInstance(final String className) {
		try {
			final Class<?> zclass = getClassLoader().loadClass(className);
			return (T) zclass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructor(Class<?> instanceClass, Class<?> ...pClasses) {
		try {
			return (Constructor<T>) instanceClass.getConstructor(pClasses);
		} catch (Exception e) {
			log.error("getConstructor", e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T create(Constructor<?> constructor, Object ...params) {
		try {
			return (T)constructor.newInstance(params);
		} catch (Exception e) {
			log.error("create", e);
			throw new RuntimeException(e);
		}
	}
	
	/*public static Set<Class<?>> readClassesFrom(String inPackage) {
		final String path = inPackage.replace('.', '/');
		final URL url = getClassLoader().getResource(path);
		final File directory = new File(url.getPath().replace("%20", " "));
		
		return findClasses(directory, inPackage);
	}

	
	//internal helper methods.............................................................................................
	private static Set<Class<?>> findClasses(File directory, String inPackage) {
		final Set<Class<?>> classes = new HashSet<>();
		
		final File[] files = directory.listFiles();
		for(final File file: files) {
			if(file.isDirectory()) {
				classes.addAll(findClasses(file, inPackage + '.' + file.getName()));
			} else if(file.getName().endsWith(".class")) {
				try {
					final Class<?> clazz = Class.forName(inPackage + '.' + file.getName().substring(0, file.getName().length() - 6));
					classes.add(clazz);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return classes;
	}*/
}
