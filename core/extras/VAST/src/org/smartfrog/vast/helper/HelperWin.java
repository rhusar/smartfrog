/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */

package org.smartfrog.vast.helper;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class HelperWin implements Helper {
	public ArrayList<String> retrieveNICNames() {
		ArrayList<String> names = new ArrayList<String>();

		try {
			// execute the ifconfig command
			Process ps = Runtime.getRuntime().exec("ipconfig -all");

			// read the output and the error stream
			// doing this asynchronously prevents blocking
			// of the process
			ArrayList<String> outBuffer = new ArrayList<String>();
			ArrayList<String> errBuffer = new ArrayList<String>();
			ReaderThread rtOut = new ReaderThread(ps.getInputStream(), outBuffer);
			ReaderThread rtErr = new ReaderThread(ps.getErrorStream(), errBuffer);
			rtOut.start();
			rtErr.start();

			// wait for ifconfig to finish
			ps.waitFor();

			// wait for the output reader to finish
			while (rtOut.getState() != Thread.State.TERMINATED)
				Thread.sleep(100);

			// parse the output
			Pattern pattern = Pattern.compile("^Ethernet\\sadapter\\s([\\w\\s]+)\\:$");
			System.out.println("pattern: " + pattern);
			for (String strLine : outBuffer)
			{
				System.out.println("line: " + strLine);
				Matcher matcher = pattern.matcher(strLine);
				if (matcher.matches()) {
					System.out.println("matched: " + matcher.group(1));
					names.add(matcher.group(1));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return names;
	}

	public void setNetworkAddress(String inNICName, String inIP, String inMask) {
		// check addresses
		if (Validator.isValidIP(inIP) && Validator.isValidSubnetMask(inMask)) {
			try {
				Process ps = Runtime.getRuntime().exec(String.format("netsh int ip add address \"%s\" %s %s", inNICName, inIP, inMask));

				// just for blocking prevention
				ArrayList<String> outBuffer = new ArrayList<String>();
				ArrayList<String> errBuffer = new ArrayList<String>();
				ReaderThread rtOut = new ReaderThread(ps.getInputStream(), outBuffer);
				ReaderThread rtErr = new ReaderThread(ps.getErrorStream(), errBuffer);
				rtOut.start();
				rtErr.start();

				ps.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
