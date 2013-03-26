package org.mdb4j.config;

import javax.xml.bind.annotation.XmlAttribute;

public class XSecurity {
	@XmlAttribute(name="adapter", required=true)
	String _adapter;
	
	public String getAdapter() {return _adapter;}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("    <security");
		
		sb.append(" adapter=\"");
		sb.append(_adapter);
		sb.append("\"");
		
		sb.append(" />\n");
		return sb.toString();
	}
}
