package org.mdb4j.old;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.mdb4j.config.XConfig;
import org.mdb4j.config.XDb;

import ua.ieeta.util.Reflector;

public class DB {
	
	private static XConfig config = null;
	
	//only one DBConnection for every XDb is valid...
	private static final Map<String, DBConnection> connections = new HashMap<>();
	
	public synchronized static DBConnection getConnection(final String name) {
		DBConnection con = connections.get(name);
		if(con == null) {
			final XConfig xconfig = getConfig();
			final XDb xdb = xconfig.getDbByName(name);
			if(xdb == null)
				throw new DBException("No configuration found in db.xml for database name: " + name);
			
			con = new DBConnection(xdb);
			connections.put(name, con);
		}

		return con;
	}
	
	public synchronized static void loadConfig() {
		final InputStream stream = Reflector.getResourceAsStream("db.xml");
		if(stream == null)
			throw new DBException("db.xml configuration file not found!");

		try {
			final JAXBContext jaxbContext = JAXBContext.newInstance(XConfig.class);
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			config = (XConfig) unmarshaller.unmarshal(stream);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public synchronized static XConfig getConfig() {
		if(config == null)
			loadConfig();
			
		return config;
	}
	
	synchronized static void close(final DBConnection con) {
		connections.remove(con.getName());
	}
	
}
