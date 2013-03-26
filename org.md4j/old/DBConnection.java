package org.mdb4j.old;

import java.lang.reflect.Field;
import java.sql.Driver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mdb4j.config.XConnection;
import org.mdb4j.config.XDb;
import org.mdb4j.config.XModule;
import org.mdb4j.dbc.JConnection;
import org.mdb4j.dbc.JConnectionPool;
import org.mdb4j.internal.CLoader;

import ua.ieeta.context.Context;

public class DBConnection {
	private final Logger log = LoggerFactory.getLogger(DBConnection.class);
	
	private final XDb xdb;
	private final Map<String, SModuleImpl> modules = new HashMap<>();
	private final Map<Class<?>, SEntityImpl> schemas = new HashMap<>();
	
	private final Cache cache = new Cache();
	
	private JConnectionPool connectionPool;
	
	public DBConnection(final XDb xdb) {
		this.xdb = xdb;
		
		for(final XModule xModule: xdb.getModules()) {
			final SModuleImpl module = new SModuleImpl(xModule.getPackage());
			modules.put(module.getName(), module);
			
			log.info("Loading Module: {}", module.getName());
			final Iterable<Class<?>> entities = Reflection.getEntities(module.getName());
			
			for(final Class<?> entity: entities) {
				final SEntityImpl sEntity = new SEntityImpl(module, entity);
				schemas.put(entity, sEntity);
				
				final List<Field> fields = Reflection.getFields(entity);
				for(final Field field: fields) {
					if(field.isAnnotationPresent(org.mdb4j.schema.Field.class))
						new SFieldImpl(sEntity, field);
				}
				
				log.info("Entity found: {}", sEntity);
			}
		}
	}
	
	public String getName() {return xdb.getName();}
	public XDb getXdb() {return xdb;}
	
	public DBSession login(final String user, final String password) {
		//TODO: replace by authentication API
		if(user.equals("micael") & password.equals("password")) {
			final DBSession session = new DBSession(this, user);
			Context.set(DBSession.class, session);
			return session;
		} else 
			throw new DBException("Login failed for user=" + user + " and password=" + password);
	}
		
	public void close() {
		//TODO: close and disable connection pool
		DB.close(this);
	}
	
	SEntityImpl getSchema(final Class<?> clazz) {
		return schemas.get(clazz);
	}

	Cache getCache() {return cache;}
	
	//TODO: sincronize threads DB link pool........................................................................
	JConnection connect(final DBSession session) {
		//TODO: manage JConnection pool/reconnection/reuse (is in use by session)
		//final XConnection xcon = xdb.getConnection();
		//final Driver driver = CLoader.createInstance(xcon.getDriver());
		//return new JConnection(driver, xcon.getUrl(), xcon.getUser(), xcon.getPassword());
		return connectionPool.connect();
	}
	
	void disconnect(final JConnection link) {
		//TODO: connection is available for other sessions
		connectionPool.release(link);
	}
	
}
