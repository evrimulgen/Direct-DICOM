package ua.ieeta.domain;

public class XField {
	private final Class<?> type;
	private final String name;
	
	protected XField(Class<?> type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public Class<?> getType() {return type;}
	public String getName() {return name;}
}
