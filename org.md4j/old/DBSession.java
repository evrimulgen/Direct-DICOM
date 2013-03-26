package org.mdb4j.old;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.mdb4j.dbc.JConnection;


public class DBSession {
	private final DBConnection con;
	private final String user;
	
	private final List<Node> newNodes = new LinkedList<>();
	private final Tree<Node, String, Object> fieldChanges = new Tree<>();
	
	
	DBSession(final DBConnection con, final String user) {
		this.con = con;
		this.user = user;
	}
	
	void addNewNode(final Node node) {
		newNodes.add(node);
	}
	
	@SuppressWarnings("unchecked")
	<T> T fGet(final Node node, final String field) {
		if(fieldChanges.containsKey(node, field))
			return (T) fieldChanges.get(node, field);
		else
			return con.getCache().fGet(node, field);
	}
	
	void fSet(final Node node, final String field, final Object value) {
		fieldChanges.add(node, field, value);
	}
	
	
	public DBConnection getConnection() {return con;}
	
	public String getUser() {return user;}
	
	public SEntityImpl getSchema(final Class<?> clazz) {
		return con.getSchema(clazz);
	}
	
	public void commit() {
		final JConnection link = con.connect(this);
		
		//use hash set for contains method performance, used when update entities
		final HashSet<Node> newNodesCache = new HashSet<>();
		
		//create entities (in order of creation)
		for(final Node node: newNodes) {
			newNodesCache.add(node);
			
			final SEntityImpl sNode = node.getSchema();
			
			final int fieldNumber = fieldChanges.get(node).size();
			final String[] dbFields = new String[fieldNumber];
			final Object[] params = new Object[fieldNumber];
			
			int i = 0;
			for(final String field: fieldChanges.get(node).keySet()) {
				final SFieldImpl sField = (SFieldImpl)sNode.getField(field);
				dbFields[i] = sField.getDbName();
				
				params[i] = Converter.convertToDB(fieldChanges.get(node, field));
				i++;
			}
			
			final String sql = SQLFactory.insert(sNode.getDbName(), dbFields);
			final long id = link.insert(sql, params);
			node.setId(id);
		}
		
		//update entities
		for(final Node node: fieldChanges.keySetLevel1()) {
			if(!newNodesCache.contains(node)) {
				final SEntityImpl sNode = node.getSchema();
				
				final int fieldNumber = fieldChanges.get(node).size();
				final String[] dbFields = new String[fieldNumber];
				final Object[] params = new Object[fieldNumber+1];
				
				int i = 0;
				for(final String field: fieldChanges.get(node).keySet()) {
					final SFieldImpl sField = (SFieldImpl)sNode.getField(field);
					dbFields[i] = sField.getDbName();
					
					params[i] = Converter.convertToDB(fieldChanges.get(node, field));
					i++;
				}
				
				final String sql = SQLFactory.update(sNode.getDbName(), dbFields);
				
				params[i] = node.getId();
				link.update(sql, params);
			}
		}
		
		//if all OK, commit to database
		link.commit();
		con.disconnect(link);
		
		con.getCache().commitChanges(fieldChanges);
		
		rollback();
	}
	
	public void rollback() {
		newNodes.clear();
		fieldChanges.clear();
	}
}
