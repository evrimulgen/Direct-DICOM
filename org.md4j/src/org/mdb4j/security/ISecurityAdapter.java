package org.mdb4j.security;

public interface ISecurityAdapter {
	boolean login(String user, String password);
}
