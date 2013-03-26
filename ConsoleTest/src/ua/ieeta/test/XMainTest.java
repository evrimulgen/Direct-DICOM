package ua.ieeta.test;

import static java.lang.System.out;

import java.util.List;

import ua.ieeta.dicom.DClient;
import ua.ieeta.dicom.DLink;
import ua.ieeta.dicom.dm.ISerie;
import ua.ieeta.dicom.dm.IStudy;

public class XMainTest {
	public static final String folder = "/dicom/tmp";
	
	public static void main(String[] args) {
		//mData();
		mTask();
		//mSever();
		
		//mAdd();
		
		//TimeTest.printTotal();
	}
	
	public static void mAdd() {
		final DClient dClient = new DClient("D-TEST", "192.168.238.171", 1105, folder);
		try {
			final DLink link = dClient.linkTo("BPPP1", "192.168.238.245", 104);
			
			AddImagesTask task = new AddImagesTask(link, 
					"/dicom/to_store/image_EJN20120424-0896_2.dcm",
					"/dicom/to_store/image_EJN20120424-0896_3.dcm");
			final Thread t1 = new Thread(task);
			t1.start();
			
			while(t1.isAlive());
		} finally {
			dClient.close();
		}
	}

	
	public static void mSever() {
		final DClient dClient = new DClient("D-TEST", "192.168.238.171", 1105, folder);
		try {
			final DLink lChee = dClient.linkTo("BPPP1", "192.168.238.245", 104);
			out.println("ECHO = " + lChee.associate().echo());
			Thread.currentThread().sleep(60000);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			dClient.close();
		}
	}
	
	public static void mData() {
		final DClient dClient = new DClient("D-TEST", "192.168.238.171", 1105, folder);
		try {
			final DLink link = dClient.linkTo("BPPP1", "192.168.238.245", 104);	//DCM4CHEE
			//final DLink link = dClient.linkTo("BPPP2", "192.168.238.245", 1004);	//DICOOGLE
			
			final ISerie sExample1 = ISerie.$.create();
			sExample1.set(ISerie.F_STUDY_UID, "1.3.6.1.4.1.22619.2.1.201008031055169520046");
			sExample1.set(ISerie.ID, "1.3.12.2.1107.5.2.32.35377.2010080312112612678641823.0.0.0");
			SerieDataTask task1 = new SerieDataTask(link, sExample1, "example1");
			
	
			final ISerie sExample2 = ISerie.$.create();
			sExample2.set(ISerie.F_STUDY_UID, "1.3.6.1.4.1.22619.2.1.201008031055169520046");
			sExample2.set(ISerie.ID, "1.3.12.2.1107.5.2.32.35377.201008031111259528000531.0.0.0");
			SerieDataTask task2 = new SerieDataTask(link, sExample2, "example2");

			final Thread t1 = new Thread(task1);
			final Thread t2 = new Thread(task2);
			
			t1.start();
			t2.start();
			
			while(t1.isAlive() || t2.isAlive());
			
			link.close();
		} finally {
			dClient.close();
		}
	}
	
	public static void mTask() {
		final DClient dClient = new DClient("D-TEST", "192.168.238.171", 1105, folder);
		try {
			
			DLink lChee = dClient.linkTo("BPPP1", "192.168.238.245", 104);
			//DLink lDicoogle = dClient.linkTo("BPPP2", "192.168.238.245", 1004);
			
			final PatientTestTask task1 = new PatientTestTask(lChee, "DCM4CHEE");
			//final PatientTestTask task2 = new PatientTestTask(lDicoogle, "DICOOGLE");
			
			
			final Thread t1 = new Thread(task1);
			//final Thread t2 = new Thread(task2).start();
			
			t1.start();
			
			while(t1.isAlive());
			
		} finally {			
			dClient.close();
		}
	}
		
	public void studyTest() {
		IStudy example = IStudy.$.create();
		//example.set(IStudy.DATE, "20100802-20100804");
		example.set(IStudy.F_PATIENT_ID, "*04"); //SMd19820831-0654 MCG19810128-0504
		
		out.println("Query Example: " + example);
		
		List<IStudy> result = IStudy.$.find(IStudy.ID, IStudy.F_PATIENT_ID, IStudy.F_DESCRIPTION, IStudy.F_DATE, IStudy.F_TIME).byExample(example);
		
		int index = 0;
		for(IStudy study: result) {
			out.println(index + ": " + study);
			index++;
		}	
	}
	
	public void getStudy() {
		//1.3.6.1.4.1.22619.2.1.201002221130230250254
		//IStudy(date=20100222, time=112659.500000, description=head^clinical libraries, patientID=LPM19810930-0917, uid=1.3.6.1.4.1.22619.2.1.201002221130230250254, )
		
		IStudy study = IStudy.$.find().byId("1.3.6.1.4.1.22619.2.1.201002221130230250254");
		out.println(study);
	}
}
