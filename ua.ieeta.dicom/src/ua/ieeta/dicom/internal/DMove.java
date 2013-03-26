package ua.ieeta.dicom.internal;

import java.util.Collections;
import java.util.List;
import ua.ieeta.dicom.dm.IData;

public class DMove {
	private final DStorage storage;
	private final String serieUID;
	
	private boolean isAvailable = true;
	private String error;
	
	DMove(DStorage storage, String serieUID) {
		this.storage = storage;
		this.serieUID = serieUID;
	}
	
	public String getSerieUID() {return serieUID;}
	
	public boolean isAvailable() {return isAvailable;}
	
	public String getError() {return error;}
	public void setError(String error) {
		isAvailable = false;
		this.error = error;
	}
	
	public List<IData> getData() {
		if(!isAvailable) throw new RuntimeException("Dataset is not available!");
		
		return Collections.unmodifiableList(storage.getDataset(serieUID));
	}
	
	public void delete() {
		storage.destroyMove(this);
		isAvailable = false;
	}
}
