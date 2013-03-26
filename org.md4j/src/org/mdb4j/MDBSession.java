package org.mdb4j;

import org.mdb4j.dbc.JConnectionPool;

public class MDBSession {
	private final String user;
	private final JConnectionPool connectionPoll;
	
	public MDBSession(String user, JConnectionPool connectionPoll) {
		this.user = user;
		this.connectionPoll = connectionPoll;
	}
	
	public void close() {
		connectionPoll.close();
	}
}
