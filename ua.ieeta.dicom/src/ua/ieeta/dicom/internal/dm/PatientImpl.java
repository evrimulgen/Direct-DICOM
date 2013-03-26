package ua.ieeta.dicom.internal.dm;

import java.util.List;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;

import ua.ieeta.context.Context;
import ua.ieeta.dicom.DAssociation;
import ua.ieeta.dicom.dm.IPatient;
import ua.ieeta.dicom.dm.IStudy;
import ua.ieeta.domain.ILoader;

public class PatientImpl extends DBase implements IPatient {	

	public PatientImpl() {super(new BasicDicomObject());}
	public PatientImpl(DicomObject dObject) {super(dObject);}
	
	@Override
	public String id() {return get(IPatient.ID);}
	
	@Override
	public <T> T get(PField<T> field) {
		return super.get(field);
	}
	
	@Override
	public <T> IPatient set(PField<T> field, T value) {
		return (IPatient) super.set(field, value);
	}
	
	@Override
	public List<IStudy> getStudies() {
		return IStudy.$.find().byPatientID(get(IPatient.ID));
	}
	
	@Override
	public List<IStudy> getStudies(IStudy.IFind find) {
		return IStudy.$.find(find.getLoadFields()).byPatientID(get(IPatient.ID));
	}
	
	@Override
	public String getRetrieveLevel() {return "PATIENT";}

	//----------------------------------------------------------------------------------------------
	public static class Loader implements ILoader<IPatient, PField<?>, IFind> {
		static final IFind defaultFind = new Find(ID, F_NAME, F_GENDER, F_BIRTHDATE, F_STUDY_COUNT);
		
		@Override
		public IPatient create() {return new PatientImpl();}

		@Override
		public IFind find() {return defaultFind;}
		
		@Override
		public IFind find(PField<?> ...loadFields) {return new Find(loadFields);}

	}
	
	public static class Find implements IFind {
		final PField<?>[] loadFields;
		
		public Find(PField<?> ...loadFields) {
			this.loadFields = loadFields;
		}
		
		@Override
		public PField<?>[] getLoadFields() {return loadFields;}
		
		@Override
		public IPatient byId(String id) {
			final IPatient example = IPatient.$.create();
			example.set(IPatient.ID,  id);
			return Context.get(DAssociation.class).findUnique(example, loadFields);
		}
		
		@Override
		public List<IPatient> byExample(IPatient example) {
			return Context.get(DAssociation.class).find(example, loadFields);
		}
	}
	
	@Override
	public String toString() {
		return toStringOf(Loader.defaultFind.getLoadFields());
	}
}
