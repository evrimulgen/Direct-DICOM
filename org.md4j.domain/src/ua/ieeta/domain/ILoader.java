package ua.ieeta.domain;

@SuppressWarnings("unchecked")
public interface ILoader<E extends XEntity, F extends XField, Fi extends IFinder<E, F>> {
	E create();
	
	Fi find();
	Fi find(F ...loadFields);
}
