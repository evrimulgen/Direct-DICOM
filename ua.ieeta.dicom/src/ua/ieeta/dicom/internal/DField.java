package ua.ieeta.dicom.internal;

import org.dcm4che2.data.VR;

import ua.ieeta.domain.XField;

public class DField extends XField {
	private final int dTag;
	private final VR vr;
	
	protected DField(Class<?> type, String name, int dTag, VR vr) {
		super(type, name);
		
		this.dTag = dTag;
		this.vr = vr;
	}
	
	public int getTag() {return dTag;}
	public VR getVR() {return vr;}
}
