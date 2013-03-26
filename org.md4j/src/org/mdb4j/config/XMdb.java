package org.mdb4j.config;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class XMdb {
	
	@XmlAttribute(name="name", required=true)
	String _name;
	
	@XmlElement(name="connection", required=true)
	XConnection _connection;
	
	@XmlElement(name="security", required=true)
	XSecurity _security;
	
	@XmlElementWrapper(name="modules")
	@XmlElement(name="module")
	Set<XModule> _modules = new HashSet<>();
	
	public String getName() {return _name;}

	public XConnection getConnection() {return _connection;}
	public XSecurity getSecurity() {return _security;}
	
	public Set<XModule> getModules() {return _modules;}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("  <db name=\"");
		sb.append(_name);
		sb.append("\">\n");
		
		sb.append(_connection);
		sb.append(_security);
		
		sb.append("    <modules>\n");
		for(XModule m: _modules)
			sb.append(m);
		sb.append("    </modules>\n");
		
		sb.append("  </db>\n");
		return sb.toString();
	}
}
