package org.mdb4j;

import java.sql.Driver;
import java.util.Set;

import org.mdb4j.config.XConnection;
import org.mdb4j.config.XMdb;
import org.mdb4j.config.XModule;
import org.mdb4j.config.XSecurity;
import org.mdb4j.dbc.JConnectionPool;
import org.mdb4j.internal.XReflector;
import org.mdb4j.security.ISecurityAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.ieeta.context.Context;
import ua.ieeta.util.Reflector;
import ua.ieeta.util.tree.Group;
import ua.ieeta.util.tree.Tree;

public class MDB {
	private final Logger log = LoggerFactory.getLogger(MDB.class);

	private final Tree schema = new Tree();
	private final JConnectionPool connectionPool;
	private final ISecurityAdapter securityAdapter;
	
	public MDB(XMdb xMdb) {
		final XConnection xConnection = xMdb.getConnection();
		final XSecurity xSecurity = xMdb.getSecurity();
		
		final Driver driver = Reflector.createInstance(xConnection.getDriver());
		
		connectionPool = new JConnectionPool(
				driver, xConnection.getUrl(), xConnection.getUser(), xConnection.getPassword(), 
				0, 20, 5);
		log.debug("Created connection pool: {}", xConnection.getDriver());
		
		securityAdapter = Reflector.createInstance(xSecurity.getAdapter());
		log.debug("Created security adapter: {}", xSecurity.getAdapter());
		
		final Group tRoot = schema.getRoot();
		for(final XModule xModule: xMdb.getModules()) {
			final String name = xModule.getPackage();
			log.info("Loading Module: {}", name);
			
			final Group tModule = new Group(name);
			tRoot.addChild(tModule);
			
			final Set<Class<?>> entities = XReflector.findEntitiesFromModule(Reflector.getClassLoader(), name);
			for(final Class<?> entity: entities) {
				final Group tEntity = new Group(entity.getName());
				tEntity.setProperty("entity", entity);
				tModule.addChild(tEntity);
				
				log.info("Entity found: {}", entity.getName());
			}
		}
	}
	
	public MDBSession login(final String user, final String password) {
		if(securityAdapter.login(user, password)) {
			final MDBSession session = new MDBSession(user, connectionPool);
			Context.set(MDBSession.class, session);
			return session;
		} else 
			throw new MDBException("Login failed for user=" + user + " and password=" + password);
	}
}
