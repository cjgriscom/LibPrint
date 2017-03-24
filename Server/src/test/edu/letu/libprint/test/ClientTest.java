package edu.letu.libprint.test;

import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;

import com.quirkygaming.propertylib.MutableProperty;

import edu.letu.libprint.Util;

public class ClientTest extends TestClass {
	boolean secTokenTest(final PrintWriter out) throws NoSuchAlgorithmException {
		MutableProperty<Boolean> success = MutableProperty.newProperty(true);
		
		String[][] params = new String[][]{
			{"temp", "tempUser", "tempPassword", "2O8WCqBjA_mZyp-kSh_ExjKyAUjURMWghtAz2jH_MXY"},
			{"AxBx32yq", "chandles", "seldnahc", "6D8zSiGLuRsBAsJl9nWbFpss9sU07TM3CWuX8wVDs80"},
			{"temp", "chandlergriscom", "Prototype Servlet", "SxY-ildir7TC0y-64Yk-ny86ITr6LkaXXhdxzK0z-qs"}
			};
		
		for (String[] test : params) {
			String token = Util.generateSecToken(test[0], test[1], test[2]);
			boolean passed = token.equals(test[3]);
			out.println("Compare ref " + test[3] + " to token " + token);
			out.println(passed + " -- " + test[0] + ":" + test[1] + ":" + test[2]);
			if (!passed) success.set(false);
			
		}

		return success.get();
	}
	
}
