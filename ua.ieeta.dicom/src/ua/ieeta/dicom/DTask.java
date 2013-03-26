package ua.ieeta.dicom;

import ua.ieeta.context.CTask;
import ua.ieeta.context.ContextData;

public abstract class DTask extends CTask {
	protected final DLink link;
	
	public DTask(DLink link) {
		super(new ContextData());
		this.link = link;
	}
	
	@Override
	public void run() {
		final DAssociation ass = link.associate();
		data.set(DAssociation.class, ass);
		super.run();
		ass.close();
	}
	
}
