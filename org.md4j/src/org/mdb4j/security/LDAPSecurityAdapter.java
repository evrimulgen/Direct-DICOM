package org.mdb4j.security;

public class LDAPSecurityAdapter implements ISecurityAdapter {

	@Override
	public boolean login(String user, String password) {
		return user.equals("micael") & password.equals("password");
	}

}
