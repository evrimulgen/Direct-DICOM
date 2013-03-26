package test;

import org.mdb4j.MDB;
import org.mdb4j.MDBFactory;
import org.mdb4j.MDBSession;

import static java.lang.System.out;

public class MainTest {
	public static void main(String[] args) {
		MDBFactory.loadDefaultConfig();

		final MDB testMDB = MDBFactory.createMDB("test");
		final MDBSession session = testMDB.login("micael", "password");
			
			test1(session);
		
		session.close();
	}
	
	public static void test1(MDBSession session) {
		out.println("test1");
	}
}
