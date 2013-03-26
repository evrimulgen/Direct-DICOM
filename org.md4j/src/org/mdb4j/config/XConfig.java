package org.mdb4j.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="config")
public class XConfig {
	
	@XmlElement(name="db")
	Set<XMdb> _dbs = new HashSet<>();
	
	Boolean isMapped = false;
	Map<String, XMdb> dbMap = new HashMap<>();
	
	public XMdb getDbByName(final String name) {
		synchronized (isMapped) {
			if(!isMapped) {
				for(XMdb xdb: _dbs)
					dbMap.put(xdb.getName(), xdb);
				isMapped = true;
			}
		}
		
		return dbMap.get(name);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<config>\n");
		
		for(final XMdb db: _dbs)
			sb.append(db.toString());
		
		sb.append("</config>");
		return sb.toString();
	}
}
