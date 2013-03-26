package ua.ieeta.dicom.internal.dm;

import java.util.List;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;

import ua.ieeta.context.Context;
import ua.ieeta.dicom.DAssociation;
import ua.ieeta.dicom.dm.ISerie;
import ua.ieeta.dicom.dm.IStudy;
import ua.ieeta.domain.ILoader;

public class StudyImpl extends DBase implements IStudy {

	public StudyImpl() {super(new BasicDicomObject());}
	public StudyImpl(DicomObject dObject) {super(dObject);}
	
	@Override
	public String id() {return get(IStudy.ID);}
	
	@Override
	public <T> T get(SField<T> field) {
		return super.get(field);
	}

	@Override
	public <T> IStudy set(SField<T> field, T value) {
		return (IStudy) super.set(field, value);
	}
	
	@Override
	public List<ISerie> getSeries() {
		return ISerie.$.find().byStudyID(get(IStudy.ID));
	}
	
	@Override
	public List<ISerie> getSeries(ISerie.IFind find) {
		return ISerie.$.find(find.getLoadFields()).byStudyID(get(IStudy.ID));
	}
	
	@Override
	public String getRetrieveLevel() {return "STUDY";}

	//----------------------------------------------------------------------------------------------
	public static class Loader implements ILoader<IStudy, SField<?>, IFind> {
		static final IFind defaultFind = new Find(ID, F_PATIENT_ID, F_DESCRIPTION, F_DATE, F_TIME, F_SERIE_COUNT);
		
		@Override
		public IStudy create() {return new StudyImpl();}

		@Override
		public IFind find() {return defaultFind;}
		
		@Override
		public IFind find(SField<?> ...loadFields) {return new Find(loadFields);}
	}
	
	public static class Find implements IFind {
		final SField<?>[] loadFields;
		
		public Find(SField<?>... loadFields) {
			this.loadFields = loadFields;
		}
		
		@Override
		public SField<?>[] getLoadFields() {return loadFields;}
		
		@Override
		public IStudy byId(String id) {
			final IStudy example = IStudy.$.create();
			example.set(IStudy.ID,  id);
			return Context.get(DAssociation.class).findUnique(example, loadFields);
		}
		
		@Override
		public List<IStudy> byExample(IStudy example) {
			return Context.get(DAssociation.class).find(example, loadFields);
		}
		
		@Override
		public List<IStudy> byPatientID(String patientID) {
			final IStudy example = IStudy.$.create();
			example.set(IStudy.F_PATIENT_ID, patientID);
			return byExample(example);
		};
	}

	@Override
	public String toString() {
		return toStringOf(Loader.defaultFind.getLoadFields());
	}
}
