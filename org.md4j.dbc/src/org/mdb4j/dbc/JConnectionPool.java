package org.mdb4j.dbc;

import java.sql.Driver;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JConnectionPool {
	private final Logger log = LoggerFactory.getLogger("SQL-POOL");
	
	private final Driver driver;
	private final String url;
	private final String user;
	private final String password;
	
	private int minPoolSize;
	private int maxPoolSize;
	private int acquireIncrement;
	private int releaseThreshold;
	
	int poolSize = 0; //pool + locked
	private final Queue<JConnection> pool = new LinkedList<>();
	private final Set<JConnection> locked = new HashSet<>();
	
	public JConnectionPool(
			final Driver driver, final String url, 
			final String user, final String password, 
			final int minPoolSize, final int maxPoolSize, final int acquireIncrement) {
		
		this.driver = driver;
		this.url = url;
		
		this.user = user;
		this.password = password;
		
		this.maxPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.acquireIncrement = acquireIncrement;
		this.releaseThreshold = acquireIncrement * 2;
		
		create(minPoolSize);
	}
	
	public synchronized JConnection connect() {
		if(pool.isEmpty())
			create(acquireIncrement);
		
		final JConnection connection = pool.poll();
		locked.add(connection);
		return connection;
	}
	
	public synchronized void release(final JConnection connection) {
		connection.rollback();	//clear any pending operations...
		
		locked.remove(connection);
		pool.add(connection);
		
		if(pool.size() >= releaseThreshold)
			release(acquireIncrement);
	}
	
	public synchronized void close() {
		for(JConnection connection: pool)
			connection.close();
		
		for(JConnection connection: locked)
			connection.close();
			
		pool.clear();
		locked.clear();
		poolSize = 0;
	}
	
	private int create(int number) {
		int created = 0;
		for(int i=0; i<number; ++i) {
			if(poolSize == maxPoolSize) {
				log.debug("Reached max pool size: {}", maxPoolSize);
				break;
			}
			
			try {
				pool.add(new JConnection(driver, url, user, password));
			} catch(RuntimeException ex) {
				log.error("Unable to create connection: {}", ex.getCause());
				break;
			}
			
			poolSize++;
			created++;
		}
		log.debug("Created JConnection: {}", created);
		return created;
	}
	
	private int release(int number) {
		int released = 0;
		for(int i=0; i<number; ++i) {
			if(poolSize == minPoolSize) {
				log.debug("Reached min pool size: {}", minPoolSize);
				break;
			}
			
			final JConnection connection = pool.poll();
			connection.close();
			
			poolSize--;
			released++;
		}
		
		log.debug("Released JConnection: {}", released);
		return released;
	}
}
