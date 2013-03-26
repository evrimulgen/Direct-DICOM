package ua.ieeta.dicom.internal.dm;

import java.util.List;

import org.dcm4che2.data.BasicDicomObject;
import org.dcm4che2.data.DicomObject;

import ua.ieeta.context.Context;
import ua.ieeta.dicom.DAssociation;
import ua.ieeta.dicom.dm.IData;
import ua.ieeta.dicom.dm.ISerie;
import ua.ieeta.domain.ILoader;

public class SerieImpl extends DBase implements ISerie {

	public SerieImpl() {super(new BasicDicomObject());}
	public SerieImpl(DicomObject dObject) {super(dObject);}
	
	@Override
	public String id() {return get(ISerie.ID);}
	
	@Override
	public <T> T get(EField<T> field) {
		return super.get(field);
	}

	@Override
	public <T> ISerie set(EField<T> field, T value) {
		return (ISerie) super.set(field, value);
	}
	
	@Override
	public List<IData> getData() {
		final String serieUID = get(ISerie.ID);
		final String studyUID = get(ISerie.F_STUDY_UID);
		return Context.get(DAssociation.class).loadData(studyUID, serieUID);
	}
	
	@Override
	public String getRetrieveLevel() {return "SERIES";}
	
	//----------------------------------------------------------------------------------------------
	public static class Loader implements ILoader<ISerie, EField<?>, IFind> {
		static final IFind defaultFind = new Find(ID, F_STUDY_UID, F_NUMBER, F_DESCRIPTION, F_IMAGE_COUNT);
		
		@Override
		public ISerie create() {return new SerieImpl();}

		@Override
		public IFind find() {return defaultFind;}
		
		@Override
		public IFind find(EField<?> ...loadFields) {return new Find(loadFields);}
	}
	
	public static class Find implements IFind {
		final EField<?>[] loadFields;
		
		public Find(EField<?>... loadFields) {
			this.loadFields = loadFields;
		}
		
		@Override
		public EField<?>[] getLoadFields() {return loadFields;}
		
		@Override
		public ISerie byId(String id) {
			final ISerie example = ISerie.$.create();
			example.set(ISerie.ID,  id);
			return Context.get(DAssociation.class).findUnique(example, loadFields);
		}
		
		@Override
		public List<ISerie> byExample(ISerie example) {
			return Context.get(DAssociation.class).find(example, loadFields);
		}
		
		@Override
		public List<ISerie> byStudyID(String studyID) {
			final ISerie example = ISerie.$.create();
			example.set(ISerie.F_STUDY_UID, studyID);
			return byExample(example);
		};
	}
	
	@Override
	public String toString() {
		return toStringOf(Loader.defaultFind.getLoadFields());
	}
}
