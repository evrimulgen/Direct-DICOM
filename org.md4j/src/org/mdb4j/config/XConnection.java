package org.mdb4j.config;

import javax.xml.bind.annotation.XmlAttribute;

public class XConnection {

	@XmlAttribute(name="driver", required=true)
	String _driver;
	
	@XmlAttribute(name="url", required=true)
	String _url;
	
	@XmlAttribute(name="user", required=true)
	String _user;
	
	@XmlAttribute(name="password", required=true)
	String _password;
	
	
	public String getDriver() {return _driver;}
	
	public String getUrl() {return _url;}
	
	public String getUser() {return _user;}
	
	public String getPassword() {return _password;}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("    <connection");
		
		sb.append(" driver=\"");
		sb.append(_driver);
		sb.append("\"");
		
		sb.append(" url=\"");
		sb.append(_url);
		sb.append("\"");
		
		sb.append(" user=\"");
		sb.append(_user);
		sb.append("\"");
		
		sb.append(" password=\"");
		sb.append(_password);
		sb.append("\"");
		
		sb.append(" />\n");
		return sb.toString();
	}
}
