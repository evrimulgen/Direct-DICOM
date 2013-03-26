package ua.ieeta.prod;

import static java.lang.System.out;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import ua.ieeta.dicom.DClient;
import ua.ieeta.dicom.DLink;
import ua.ieeta.dicom.DTask;
import ua.ieeta.dicom.dm.IData;
import ua.ieeta.dicom.dm.IPatient;
import ua.ieeta.dicom.dm.ISerie;
import ua.ieeta.dicom.dm.IStudy;

public class PatientFindTask extends DTask {
	final String patientName;
	
	public static void main(String[] args) {
		final String tmp = "/dicom/tmp";
		
		final DClient dClient = new DClient("MICAEL", "192.168.238.171", 1104, tmp);
		try {
			final DLink link = dClient.linkTo("XSARCHIVE", "192.168.238.196", 104);
			
			final PatientFindTask task1 = new PatientFindTask(link, "Paiva");
			final Thread t1 = new Thread(task1);
			
			t1.start();
			while(t1.isAlive());
		} finally {
			dClient.close();
		}
	}
	
	public PatientFindTask(DLink link, String patientName) {
		super(link);
		this.patientName = patientName;
	}
	
	@Override
	public void execute() {
		final File directory = new File("/dicom/storage");
		directory.mkdir();
		
		final IPatient example = IPatient.$.create();
		example.set(IPatient.F_NAME, patientName);
		out.println("Query Example: " + example);
		
		//option of reuse find object with retrieve configurations
		final IPatient.IFind find = IPatient.$.find();
		final List<IPatient> result = find.byExample(example);
		
		int pIndex = 0;
		for(IPatient patient: result) {
			out.println(pIndex + ": " + patient);
			pIndex++;
			
			//get studies....
			int sIndex = 0;
			final List<IStudy> studyList = patient.getStudies();
			for(IStudy study: studyList) {
				out.println("   " + sIndex + ": " + study);
				sIndex++;
				
				
				int eIndex = 0;
				final List<ISerie> serieList = study.getSeries();
				for(ISerie serie: serieList) {
					out.println("      " + eIndex + ": " + serie);
					
					/*final List<IData> dataset = serie.getData();
					for(IData data: dataset)
						copy(directory, data);*/
					
					
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
