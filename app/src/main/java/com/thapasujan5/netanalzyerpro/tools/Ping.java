package com.thapasujan5.netanalzyerpro.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class Ping {

	public static boolean pingCheck(String ip) {

		System.out.println(" executeCammand");
		Runtime runtime = Runtime.getRuntime();
		try {
			Process mIpAddrProcess = runtime
					.exec("/system/bin/ping -c 1 " + ip);
			int mExitValue = mIpAddrProcess.waitFor();
			System.out.println(" mExitValue " + mExitValue);
			return mExitValue == 0;
		} catch (InterruptedException ignore) {

			ignore.printStackTrace();
			System.out.println(" Exception:" + ignore);
		} catch (IOException e) {

			e.printStackTrace();
			System.out.println(" Exception:" + e);
		}
		return false;
	}

	public static String pingAsRoot(String[] cmds) throws Exception {
		String seg = "";
		Process p = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		InputStream is = p.getInputStream();
		for (String tmpCmd : cmds) {
			os.writeBytes(tmpCmd + "\n");
			int readed = 0;
			byte[] buff = new byte[4096];
			boolean cmdRequiresAnOutput = true;
			if (cmdRequiresAnOutput) {
				while (is.available() <= 0) {
					try {
						Thread.sleep(5000);
					} catch (Exception ex) {
					}
				}

				while (is.available() > 0) {
					readed = is.read(buff);
					if (readed <= 0)
						break;
					seg = new String(buff, 0, readed);

				}
			}
		}
		os.writeBytes("exit\n");
		os.flush();
		Log.d("Ping:" + cmds[0], seg);
		return seg;
	}

	public static String ping(String url) {

		String str = "";
		try {
			Process process = Runtime.getRuntime().exec(
					"/system/bin/ping -c 8 " + url);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			int i;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((i = reader.read(buffer)) > 0)
				output.append(buffer, 0, i);
			reader.close();
			// body.append(output.toString()+"\n");
			str = output.toString();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return str;
	}
}
