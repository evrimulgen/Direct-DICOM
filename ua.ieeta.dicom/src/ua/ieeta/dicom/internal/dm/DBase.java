package ua.ieeta.dicom.internal.dm;

import org.dcm4che2.data.DicomObject;

import ua.ieeta.dicom.internal.DField;

public abstract class DBase {
	final DicomObject dObject;
	
	public DBase(DicomObject dObject) {
		this.dObject = dObject;
	}
	
	public DicomObject getDicomObject() {return dObject; }
	

	@SuppressWarnings("unchecked")
	public <T> T get(DField field) {
		if(field.getType() == String.class)
			return (T) dObject.getString(field.getTag());
		else if(field.getType() == Integer.class)
			return (T) (Integer) dObject.getInt(field.getTag());
		
		throw new RuntimeException("Unsuported field type of: " + field.getType());
	}
	
	public <T> DBase set(DField field, T value) {
		if(field.getType() == String.class)
			dObject.putString(field.getTag(), field.getVR(), (String)value);
		else if(field.getType() == Integer.class)
			dObject.putInt(field.getTag(), field.getVR(), (Integer)value);
		else
			throw new RuntimeException("Unsuported field type of: " + field.getType());
		
		return this;
	}
	
	
	public abstract String getRetrieveLevel();
	
	protected String toStringOf(DField ...fields) {
		final StringBuilder sb = new StringBuilder(getClass().getInterfaces()[0].getSimpleName());
		sb.append("(");
		int i=1;
		for(DField field: fields) {
			Object value = dObject.getString(field.getTag()); 
			
			sb.append(field.getName());
			sb.append("=");
			sb.append(value);
			if(i < fields.length) sb.append(", ");
			
			i++;
		}
		sb.append(")");
		
		return sb.toString();
	}
}
