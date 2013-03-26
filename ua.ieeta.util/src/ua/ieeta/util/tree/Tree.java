package ua.ieeta.util.tree;

public class Tree {
	private final Group root = new Group("root");
	
	public Group getRoot() {return root;}
	
	public boolean hasNode(String path) {
		final String[] split = path.split("/");
		
		Node node = root;
		for(String name: split) {
			if(node instanceof Group)
				node = ((Group) node).getChild(name);
			else
				return false;
		}
		
		return true;
	}
	
	// path like /<l1-name>/<l2-name>
	public Node getNode(String path) {
		final String[] split = path.split("/");

		Node node = root;
		for(String name: split) {
			if(node instanceof Group)
				node = ((Group) node).getChild(name);
			else
				throw new RuntimeException("No node found with path: " + path);
		}
		
		return node;
	}
}
