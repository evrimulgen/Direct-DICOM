package ua.ieeta.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import ua.ieeta.context.Context;
import ua.ieeta.dicom.DAssociation;
import ua.ieeta.dicom.DLink;
import ua.ieeta.dicom.DTask;

public class AddImagesTask extends DTask {
	final String[] files;
	
	public AddImagesTask(DLink link, String ...files) {
		super(link);
		this.files = files;
	}
	
	@Override
	public void execute() {
		try {
			for(String file: files) {
				final InputStream is = new FileInputStream(file);
				Context.get(DAssociation.class).store(is);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
