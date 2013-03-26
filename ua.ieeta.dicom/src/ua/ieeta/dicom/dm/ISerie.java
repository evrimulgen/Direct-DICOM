package ua.ieeta.dicom.dm;

import java.util.List;

import org.dcm4che2.data.Tag;
import org.dcm4che2.data.VR;

import ua.ieeta.dicom.internal.DField;
import ua.ieeta.dicom.internal.IDicom;
import ua.ieeta.dicom.internal.dm.SerieImpl;
import ua.ieeta.domain.IFinder;
import ua.ieeta.domain.ILoader;

public interface ISerie extends IDicom {
	class EField<T> extends DField {
		EField(Class<T> type, String name, int dTag, VR vr) {super(type, name, dTag, vr);}
	}
	
	EField<String> 	ID = new EField<String>(String.class, "UID", Tag.SeriesInstanceUID, VR.UI);
	EField<Integer> F_NUMBER = new EField<Integer>(Integer.class, "NUMBER", Tag.SeriesNumber, VR.IS);
	EField<String> 	F_DESCRIPTION = new EField<String>(String.class, "DESCRIPTION", Tag.SeriesDescription, VR.LO);
	EField<Integer> F_IMAGE_COUNT = new EField<Integer>(Integer.class, "IMAGE_COUNT", Tag.NumberOfSeriesRelatedInstances, VR.IS);
	
	EField<String> 	F_STUDY_UID = new EField<String>(String.class, "STUDY_UID", Tag.StudyInstanceUID, VR.UI);
	
	//fields list...
	EField<?>[] FIELDS = new EField<?>[]{ID, F_NUMBER, F_DESCRIPTION, F_IMAGE_COUNT, F_STUDY_UID};
	
	<T> T get(EField<T> field);
	<T> ISerie set(EField<T> field, T value);
	
	/**
	 * @return Dataset of dicom images (data is only available when DAssociation is open)
	 */
	List<IData> getData();
	
	//-------------------------------------------------------------------------
	ILoader<ISerie, EField<?>, IFind> $ = new SerieImpl.Loader();
	
	interface IFind extends IFinder<ISerie, EField<?>> {
		List<ISerie> byStudyID(String studyID);
	}
}
