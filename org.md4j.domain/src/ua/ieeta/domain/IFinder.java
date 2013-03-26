package ua.ieeta.domain;

import java.util.List;

public interface IFinder<E extends XEntity, F extends XField> {
	F[] getLoadFields();
	
	E byId(String id);
	List<E> byExample(E example);
}
