package test.domain.internal;

import java.util.List;

import test.domain.EUser;
import ua.ieeta.domain.ILoader;

public class UserImpl implements EUser {

	@Override
	public String id() {
		return get(EUser.ID);
	}

	@Override
	public <T> T get(Field<T> field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> EUser set(Field<T> field, T value) {
		// TODO Auto-generated method stub
		return null;
	}

	//----------------------------------------------------------------------------------------------
	public static class Loader implements ILoader<EUser, Field<?>, IFind> {
		static final IFind defaultFind = new Find(EUser.FIELDS);
		
		@Override
		public EUser create() {return new UserImpl();}

		@Override
		public IFind find() {return defaultFind;}
		
		@Override
		public IFind find(Field<?> ...loadFields) {return new Find(loadFields);}
	}
	
	public static class Find implements IFind {
		final Field<?>[] loadFields;
		
		public Find(Field<?> ...loadFields) {
			this.loadFields = loadFields;
		}
		
		@Override
		public Field<?>[] getLoadFields() {return loadFields;}
		
		@Override
		public EUser byId(String id) {
			//TODO: byId
			return null;
		}
		
		@Override
		public List<EUser> byExample(EUser example) {
			//TODO: byExample
			return null;
		}
	}
}
