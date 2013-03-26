package org.mdb4j.config;

import javax.xml.bind.annotation.XmlAttribute;

public class XModule {

	@XmlAttribute(name="package", required=true)
	String _package;
	
	public String getPackage() {return _package;}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("      <module");
		
		sb.append(" package=\"");
		sb.append(_package);
		sb.append("\"");
		
		sb.append(" />\n");
		return sb.toString();
	}

}
