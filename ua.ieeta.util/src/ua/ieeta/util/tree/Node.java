package ua.ieeta.util.tree;

import java.util.HashMap;
import java.util.Set;

public class Node {
	Node parent = null;
	
	private final String name;
	private final HashMap<String, Object> properties = new HashMap<>();
	
	public Node(String name) {
		this.name = name;
	}
	
	public Node getParent() {return parent;}
	public String getName() {return name;}
	
	public Set<String> getProperties() {
		return properties.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String name) {
		return (T) properties.get(name);
	}
	
	public Node setProperty(String name, Object value) {
		properties.put(name, value);
		return this;
	}
}
