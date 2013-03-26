package ua.ieeta.dicom.dm;

import java.util.List;

import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;

import ua.ieeta.dicom.internal.DField;
import ua.ieeta.dicom.internal.IDicom;
import ua.ieeta.dicom.internal.dm.StudyImpl;
import ua.ieeta.domain.IFinder;
import ua.ieeta.domain.ILoader;

public interface IStudy extends IDicom {
	class SField<T> extends DField {
		SField(Class<T> type, String name, int dTag, VR vr) {super(type, name, dTag, vr);}
	}
	
	//available fields...
	SField<String> 	ID = new SField<String>(String.class, "UID", Tag.StudyInstanceUID, VR.UI);
	SField<String> 	F_DESCRIPTION = new SField<String>(String.class, "DESCRIPTION", Tag.StudyDescription, VR.LO);
	SField<String> 	F_DATE = new SField<String>(String.class, "DATE", Tag.StudyDate, VR.DA);
	SField<String> 	F_TIME = new SField<String>(String.class, "TIME", Tag.StudyTime, VR.TM);
	SField<Integer> F_SERIE_COUNT = new SField<Integer>(Integer.class, "SERIE_COUNT", Tag.NumberOfStudyRelatedSeries, VR.IS);
	
	SField<String> 	F_PATIENT_ID = new SField<String>(String.class, "PATIENT_ID", Tag.PatientID, VR.LO);
	
	//fields list...
	SField<?>[] FIELDS = new SField<?>[]{ID, F_DESCRIPTION, F_DATE, F_TIME, F_SERIE_COUNT, F_PATIENT_ID};
	
	<T> T get(SField<T> field);
	<T> IStudy set(SField<T> field, T value);
	
	List<ISerie> getSeries();
	List<ISerie> getSeries(ISerie.IFind find);
	
	//-------------------------------------------------------------------------
	ILoader<IStudy, SField<?>, IFind> $ = new StudyImpl.Loader();
		
	interface IFind extends IFinder<IStudy, SField<?>> {
		List<IStudy> byPatientID(String patientID);
	}
}
