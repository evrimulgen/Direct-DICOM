package ua.ieeta.test;

import static java.lang.System.out;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import ua.ieeta.dicom.DLink;
import ua.ieeta.dicom.DTask;
import ua.ieeta.dicom.dm.IData;
import ua.ieeta.dicom.dm.IPatient;
import ua.ieeta.dicom.dm.ISerie;
import ua.ieeta.dicom.dm.IStudy;

public class PatientTestTask extends DTask {
	final String taskName;
	
	public PatientTestTask(DLink link,  String taskName) {
		super(link);
		this.taskName = taskName;
	}
	
	@Override
	public void execute() {
		final File directory = new File("/dicom/storage");
		directory.mkdir();
		
		final IPatient example = IPatient.$.create();
		example.set(IPatient.F_NAME, "DIAS*");
		//example.set(IPatient.F_GENDER, "M");
		
		out.println(taskName + " - Query Example: " + example);
		
		//option of reuse find object with retrieve configurations
		final IPatient.IFind find = IPatient.$.find();
		final List<IPatient> result = find.byExample(example);
		
		int pIndex = 0;
		for(IPatient patient: result) {
			out.println(taskName + " - " + pIndex + ": " + patient);
			pIndex++;
			
			//get studies....
			int sIndex = 0;
			final List<IStudy> studyList = patient.getStudies();
			for(IStudy study: studyList) {
				out.println("   " + taskName + " - " + sIndex + ": " + study);
				sIndex++;
				
				
				int eIndex = 0;
				final List<ISerie> serieList = study.getSeries();
				for(ISerie serie: serieList) {
					out.println("      " + taskName + " - " + eIndex + ": " + serie);
					
					final List<IData> dataset = serie.getData();
					for(IData data: dataset)
						copy(directory, data);
					
					
					eIndex++;
				}
				
			}
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
