package ua.ieeta.prod;

import ua.ieeta.dicom.DClient;
import ua.ieeta.dicom.DLink;
import ua.ieeta.dicom.dm.ISerie;
import ua.ieeta.test.AddImagesTask;
import ua.ieeta.test.SerieDataTask;

public class ProdSessionExport {
	public static final String tmp = "/dicom/tmp";
	public static final String store = "/dicom/storage";
	
	public static final String study_oid = "1.3.6.1.4.1.22619.2.1.201110122229349310787";
	
	public static void main(String[] args) {
		/*PACS
		(0000,0002) UI #26 [1.2.840.10008.5.1.4.1.1.4] Affected SOP Class UID
		(0000,0100) US #2 [1] Command Field
		(0000,0110) US #2 [15654] Message ID
		(0000,0700) US #2 [0] Priority
		(0000,0800) US #2 [0] Data Set Type
		(0000,1000) UI #52 [1.3.12.2.1107.5.2.32.35377.2011101222285412392539193] Affected SOP Instance UID
		*/
		
		/*DCM4CHEE
		(0000,0002) UI #26 [1.2.840.10008.5.1.4.1.1.4] Affected SOP Class UID
		(0000,0100) US #2 [1] Command Field
		(0000,0110) US #2 [8] Message ID
		(0000,0700) US #2 [0] Priority
		(0000,0800) US #2 [0] Data Set Type
		(0000,1000) UI #52 [1.3.12.2.1107.5.2.32.35377.2011101222285412392539193] Affected SOP Instance UID
		
		(0000,1030) AE #6 [D-TEST] Move Originator Application Entity Title
		(0000,1031) US #2 [14] Move Originator Message ID
		*/
		 
		
		//fromProd();
		fromDcm4che();
		//fromXnat();
		
		//nerlineExamProd();
		//onlineExamProd();
		
		//xnatAdd();
	}
	
	public static void nerlineExamProd() {
		final DClient dClient = new DClient("MICAEL", "192.168.238.171", 1104, tmp);
		try {
			final DLink link = dClient.linkTo("XSARCHIVE", "192.168.238.196", 104);
			
			final ISerie sExample1 = ISerie.$.create();
			sExample1.set(ISerie.F_STUDY_UID, "1.3.6.1.4.1.22619.2.1.201206261425000930261");
			sExample1.set(ISerie.ID, "1.3.12.2.1107.5.2.32.35377.2012062616063059117276175.0.0.0");
						
			final SerieDataTask task1 = new SerieDataTask(link, sExample1, "example1");
			final Thread t1 = new Thread(task1);
			
			t1.start();
			while(t1.isAlive());
		} finally {
			dClient.close();
		}
	}
	
	public static void onlineExamProd() {
		final DClient dClient = new DClient("MICAEL", "192.168.238.171", 1104, tmp);
		try {
			final DLink link = dClient.linkTo("XSARCHIVE", "192.168.238.196", 104);
						
			final ISerie sExample1 = ISerie.$.create();
			sExample1.set(ISerie.F_STUDY_UID, "1.3.6.1.4.1.22619.2.1.201210300841555780630");
			sExample1.set(ISerie.ID, "1.3.12.2.1107.5.2.32.35377.2012103008563353239400006.0.0.0");
			
			
			final ISerie sExample2 = ISerie.$.create();
			sExample2.set(ISerie.F_STUDY_UID, "1.3.6.1.4.1.22619.2.1.201210300841555780630");
			sExample2.set(ISerie.ID, "1.3.12.2.1107.5.2.32.35377.2012103008572083382800038.0.0.0");
			
			final ISerie sExample3 = ISerie.$.create();
			sExample3.set(ISerie.F_STUDY_UID, "1.3.6.1.4.1.22619.2.1.201210300841555780630");
			sExample3.set(ISerie.ID, "1.3.12.2.1107.5.2.32.35377.2012103008572083382800038.0.0.0");
			
			final SerieDataTask task1 = new SerieDataTask(link, sExample1, "example1");
			final SerieDataTask task2 = new SerieDataTask(link, sExample2, "example2");
			final SerieDataTask task3 = new SerieDataTask(link, sExample3, "example3");
			
			final Thread t1 = new Thread(task1);
			t1.setName("example-1");
			
			final Thread t2 = new Thread(task2);
			t2.setName("example-2");
			
			final Thread t3 = new Thread(task3);
			t3.setName("example-3");
			
			try {
				t1.start();
				t2.start();
				t3.start();
			} catch (Exception e) {
				//ignore
			}
			
			
			while(t1.isAlive() || t2.isAlive() || t3.isAlive());
		} finally {
			dClient.close();
		}
	}
	
	
	public static void fromProd() {
		final DClient dClient = new DClient("MICAEL", "192.168.238.171", 1104, tmp);
		try {
			final DLink link = dClient.linkTo("XSARCHIVE", "192.168.238.196", 104);
			
			final SessionExportTask task1 = new SessionExportTask(link, study_oid);
			final Thread t1 = new Thread(task1);
			
			t1.start();
			while(t1.isAlive());
		} finally {
			dClient.close();
		}
	}
	
	public static void fromDcm4che() {
		final DClient dClient = new DClient("D-TEST", "192.168.238.171", 1105, tmp);
		try {
			final DLink link = dClient.linkTo("BPPP1", "192.168.238.245", 104);
			
			final SessionExportTask task1 = new SessionExportTask(link, study_oid);
			final Thread t1 = new Thread(task1);
			
			t1.start();
			while(t1.isAlive());
		} finally {
			dClient.close();
		}
	}

	public static void fromXnat() {
		final DClient dClient = new DClient("D-TEST", "192.168.238.171", 1105, tmp);
		try {
			final DLink link = dClient.linkTo("XNATGATEWAY", "192.168.238.173", 4006);
			
			final PatientFindTask task1 = new PatientFindTask(link, "*");
			final Thread t1 = new Thread(task1);
			
			t1.start();
			while(t1.isAlive());
		} finally {
			dClient.close();
		}
	}
	
	public static void xnatAdd() {
		final DClient dClient = new DClient("D-TEST", "192.168.238.171", 1105, tmp);
		try {
			final DLink link = dClient.linkTo("XNAT", "192.168.238.246", 8104);
			
			AddImagesTask task = new AddImagesTask(link, 
					"/dicom/storage/exam_AAD20111122-0329_MPRAGE_2/image_AAD20111122-0329_1.dcm");
			final Thread t1 = new Thread(task);
			t1.start();
			
			while(t1.isAlive());
		} finally {
			dClient.close();
		}
	}
	
}
