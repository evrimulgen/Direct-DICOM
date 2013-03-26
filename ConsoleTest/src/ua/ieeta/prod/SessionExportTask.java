package ua.ieeta.prod;

import static java.lang.System.out;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import ua.ieeta.dicom.DLink;
import ua.ieeta.dicom.DTask;
import ua.ieeta.dicom.dm.IData;
import ua.ieeta.dicom.dm.ISerie;
import ua.ieeta.dicom.dm.IStudy;

public class SessionExportTask extends DTask {
	private final String oid;
	
	public SessionExportTask(DLink link, String oid) {
		super(link);
		this.oid = oid;
	}
	
	@Override
	public void execute() {
		final File directory = new File("/dicom/storage");
		
		IStudy study = IStudy.$.find().byId(oid);
		out.println("   " + study);
		
		int eIndex = 0;
		final List<ISerie> serieList = study.getSeries();
		for(ISerie serie: serieList) {
			out.println("      " + eIndex + ": " + serie);
			
			final List<IData> dataset = serie.getData();
			for(IData data: dataset)
				copy(directory, data);
			
			eIndex++;
		}
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
