package ua.ieeta.test;

public class TimeTest {
	private static volatile long total = 0;
	
	public static void reset() {
		total = 0;
	}
	
	public static void add(long mili) {
		total += mili;
	}
	
	public static void printTotal() {
		System.out.println("Total copy time from tmp -> storage: " + total + " ms");
	}
}
