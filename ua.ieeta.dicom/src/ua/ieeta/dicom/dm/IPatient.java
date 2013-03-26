package ua.ieeta.dicom.dm;

import java.util.List;

import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;

import ua.ieeta.dicom.internal.DField;
import ua.ieeta.dicom.internal.IDicom;
import ua.ieeta.dicom.internal.dm.PatientImpl;
import ua.ieeta.domain.IFinder;
import ua.ieeta.domain.ILoader;

public interface IPatient extends IDicom {
	class PField<T> extends DField {
		PField(Class<T> type, String name, int dTag, VR vr) {super(type, name, dTag, vr);}
	}
	
	//available fields...
	PField<String> 	ID = new PField<String>(String.class, "ID", Tag.PatientID, VR.LO);
	PField<String> 	F_NAME = new PField<String>(String.class, "NAME", Tag.PatientName, VR.PN);
	PField<String> 	F_GENDER = new PField<String>(String.class, "GENDER", Tag.PatientSex, VR.CS);
	PField<String> 	F_BIRTHDATE = new PField<String>(String.class, "BIRTHDATE", Tag.PatientBirthDate, VR.DA);
	PField<Integer> F_STUDY_COUNT = new PField<Integer>(Integer.class, "STUDY_COUNT", Tag.NumberOfPatientRelatedStudies, VR.IS);
	
	//fields list...
	PField<?>[] FIELDS = new PField<?>[]{ID, F_NAME, F_GENDER, F_BIRTHDATE, F_STUDY_COUNT};
	
	
	<T> T get(PField<T> field);
	<T> IPatient set(PField<T> field, T value);
	
	List<IStudy> getStudies();
	List<IStudy> getStudies(IStudy.IFind find);
	
	//-------------------------------------------------------------------------
	ILoader<IPatient, PField<?>, IFind> $ = new PatientImpl.Loader();
	
	interface IFind extends IFinder<IPatient, PField<?>>{}
}
