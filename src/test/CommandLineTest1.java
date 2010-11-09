package test;

import java.io.IOException;

import junit.framework.TestCase;

public class CommandLineTest1 extends TestCase {

	public void setUp() {
		// TODO set Up an HTTP-Server
	}

	public void testExecuteNoFuzzer() {
		Runtime rt = Runtime.getRuntime();
		try {
			rt.getRuntime().getClass();
			String[] arguments = {"--no-execute"};
			Process pr = rt.exec("java -jar ./jar/JBroFuzz.jar --no-execute");
			int returnValue = pr.waitFor();
			System.out.println("ReturnValue: " + returnValue);
			assertTrue(returnValue < 0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void tearDown() {
		// TODO shutdown HTTP-Server
	}
}