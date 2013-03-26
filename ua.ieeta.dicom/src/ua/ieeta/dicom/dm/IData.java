package ua.ieeta.dicom.dm;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;

import ua.ieeta.dicom.internal.dm.DataImpl;

public interface IData {
	public class Info {
		final String patientID;
		final String studyUID;
		final String serieUID;
		final String imageUID;
		
		final int serie;
		final int image;
		
		final String name;
		
		//other not required fields...
		
		final String patientName;
		final String studyDescription;
		final String serieDescription;
		
		public Info(DicomObject dObject) {
			this.patientID = dObject.getString(Tag.PatientID);
			this.studyUID = dObject.getString(Tag.StudyInstanceUID);
			this.serieUID = dObject.getString(Tag.SeriesInstanceUID);
			this.imageUID = dObject.getString(Tag.SOPInstanceUID);
			
			this.serie = dObject.getInt(Tag.SeriesNumber);
			this.image = dObject.getInt(Tag.InstanceNumber);
			
			this.name = "study." +studyUID+ "_serie." +serie+ "_image." +image+ ".dcm";
			
			this.patientName = dObject.getString(Tag.PatientName);
			this.studyDescription = dObject.getString(Tag.StudyDescription);
			this.serieDescription = dObject.getString(Tag.SeriesDescription);
			
		}
		
		public String getPatientID() {return patientID;}
		public String getStudyUID() {return studyUID;}
		public String getSerieUID() {return serieUID;}
		public String getImageUID() {return imageUID;}
		
		public int getSerieNumber() {return serie;}
		public int getImageNumber() {return image;}
		
		public String getName() {return name;}
		
		public String getPatientName() {return patientName;}
		public String getStudyDescription() {return studyDescription;}
		public String getSerieDescription() {return serieDescription;}
	}
	
	Info getInfo();
	InputStream getStream();
	
	void copyTo(OutputStream os);
	//void store();
	
	//-------------------------------------------------------------------------
	ILoader $ = new DataImpl.Loader();
	
	interface ILoader {
		IData create(File file);
	}
}
