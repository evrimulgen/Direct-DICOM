package org.mdb4j.internal;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ua.ieeta.domain.MDBEntity;
import ua.ieeta.domain.XEntity;

public class XReflector {

	public static Set<Field> findFields(Class<?> entity) {
		final Set<Field> fields = new HashSet<>(Arrays.asList(entity.getDeclaredFields()));
		final Class<?>[] interfaces = entity.getInterfaces();
		
		for(Class<?> inter: interfaces) {
			if(!inter.equals(XEntity.class))
				fields.addAll(findFields(entity));
		}
		
		return fields;
	}
	
	public static Set<Class<?>> findEntitiesFromModule(ClassLoader cLoader, String module) {
		final String path = module.replace('.', '/');
		final URL url = cLoader.getResource(path);
		final File directory = new File(url.getPath().replace("%20", " "));
		
		return findEntities(directory, module);
	}
	
	//internal helper methods.............................................................................................
	private static Set<Class<?>> findEntities(File directory, String inPackage) {
		final Set<Class<?>> classes = new HashSet<>();
		
		final File[] files = directory.listFiles();
		for(final File file: files) {
			if(file.isDirectory()) {
				classes.addAll(findEntities(file, inPackage + '.' + file.getName()));
			} else if(file.getName().endsWith(".class")) {
				try {
					final Class<?> clazz = Class.forName(inPackage + '.' + file.getName().substring(0, file.getName().length() - 6));
					
					if(clazz.isAnnotationPresent(MDBEntity.class))
						classes.add(clazz);
					
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return classes;
	}
}
