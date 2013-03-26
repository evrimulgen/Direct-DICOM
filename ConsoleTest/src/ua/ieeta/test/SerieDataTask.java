package ua.ieeta.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import ua.ieeta.dicom.DLink;
import ua.ieeta.dicom.DTask;
import ua.ieeta.dicom.dm.IData;
import ua.ieeta.dicom.dm.ISerie;

public class SerieDataTask extends DTask {
	final ISerie serie;
	final String name;
	
	public SerieDataTask(DLink link, ISerie serie, String name) {
		super(link);
		this.serie = serie;
		this.name = name;
	}

	@Override
	public void execute() {
		final File directory = new File("/dicom/storage/" + name);
		directory.mkdir();
		
		final List<IData> dataset = serie.getData();
		for(IData data: dataset)
			copy(directory, data);
			
	}
	
	private void copy(File directory, IData data) {
		try {
			final File file = new File(directory, data.getInfo().getName());
			file.createNewFile();
			data.copyTo(new FileOutputStream(file));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}