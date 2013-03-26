package ua.ieeta.util.tree;

import java.util.HashMap;
import java.util.Set;

public class Group extends Node {
	private final HashMap<String, Node> childs = new HashMap<>();

	public Group(String name) {
		super(name);
	}

	public Set<String> getChilds() {
		return childs.keySet();
	}
	
	public Node getChild(String name) {
		return childs.get(name);
	}
	
	public void addChild(Node child) {
		if(child.parent != null)
			throw new RuntimeException("The node already has a parent! Remove the node from the tree first.");
		
		if(childs.containsKey(child.getName()))
			throw new RuntimeException("Child name colision! " + child.getName());
		
		childs.put(child.getName(), child);
		child.parent = this;	
	}
	
	public void removeChild(Node child) {
		childs.remove(child.getName());
		child.parent = null;
	}
}
