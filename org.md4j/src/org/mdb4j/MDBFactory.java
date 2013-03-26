package org.mdb4j;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.mdb4j.config.XConfig;
import org.mdb4j.config.XMdb;

import ua.ieeta.util.Reflector;

public class MDBFactory {
	private static XConfig config = null;
	
	public static void loadDefaultConfig() {		
		loadConfig("db.xml");
	}
	
	public static void loadConfig(String xmlFile) {
		final InputStream stream = Reflector.getResourceAsStream(xmlFile);
		if(stream == null)
			throw new MDBException(xmlFile + " configuration file not found!");

		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(XConfig.class);
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			config = (XConfig) unmarshaller.unmarshal(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static MDB createMDB(String name) {
		if(config == null)
			throw new MDBException("Load config file first!");
		
		final XMdb xdb = config.getDbByName(name);
		if(xdb == null)
			throw new MDBException("No configuration found (in config file) for database name: " + name);
			
		return new MDB(xdb);
	}

}
