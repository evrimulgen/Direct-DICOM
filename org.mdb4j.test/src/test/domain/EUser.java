package test.domain;

import test.domain.internal.UserImpl;
import ua.ieeta.domain.IFinder;
import ua.ieeta.domain.ILoader;
import ua.ieeta.domain.MDBEntity;
import ua.ieeta.domain.XEntity;
import ua.ieeta.domain.XField;

@MDBEntity
public interface EUser extends XEntity {
	class Field<T> extends XField {
		public Field(Class<T> type, String name) {super(type, name);}
	}
	
	Field<String> ID = new Field<>(String.class, "id");
	Field<String> F_NAME = new Field<>(String.class, "name");
	Field<String> F_EMAIL = new Field<>(String.class, "email");
	
	Field<?>[] FIELDS = {ID, F_NAME, F_EMAIL};
	
	<T> T get(Field<T> field);
	<T> EUser set(Field<T> field, T value);
	
	ILoader<EUser, Field<?>, IFind> $ = new UserImpl.Loader();
	
	interface IFind extends IFinder<EUser, Field<?>>{}
}
