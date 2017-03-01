package edu.letu.libprint.test;

import java.lang.reflect.Method;

public class TestClass {
	boolean performTests(final TestWriter out) {
		out.println("--- Invoking test " + getClass().getName() + " ---");
		boolean success = true;
		for (Method m : this.getClass().getDeclaredMethods()) {
			if (m.getName().contains("performTests") || m.getName().contains("$")) continue;
			try {
				out.println("* " + m.getName() + " *");
				boolean passed = (boolean) m.invoke(this, out);
				if (passed) {
					out.println("--> PASSED");
				} else {
					out.println("--> FAILED");
					success = false;
				}
			} catch (Exception e) {
				out.println(m.getName() + " threw exception " + e.getClass() + " - " + e.getMessage());
				success = false;
				e.printStackTrace();
			}
		}
		out.println("--- Test " + getClass().getName() + " " + (success ? "PASSED" : "FAILED") + " ---");
		return success;
	}
}
