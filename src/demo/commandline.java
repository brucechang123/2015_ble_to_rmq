package demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class commandline {
	private String bdAddress;

	public void lescan() throws Exception {
		Process pl = Runtime.getRuntime().exec("hcitool lescan");
		String line = "";
		BufferedReader p_in = new BufferedReader(new InputStreamReader(
				pl.getInputStream()));
		while ((line = p_in.readLine()) != null) {
			if (line.contains("BLE Shield")) {
				System.out.print("BLE device's MAC: ");
				bdAddress = line.substring(0, 17);
				System.out.println(bdAddress);
				break;
			}
		}
		p_in.close();
		pl.waitFor();
		pl.destroy();
	}
	public void bdConnect() throws Exception {
		String cmd = "gatttool -b " + bdAddress + " -I -t random";
		Process pl = Runtime.getRuntime().exec(cmd);
		pl = Runtime.getRuntime().exec(cmd);
		InputStream in = pl.getInputStream();
		OutputStream out = pl.getOutputStream();
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(isr);
		char[] chars = new char[1024];
		int readlen = -1;
		String status = "disconnect";
		String temp0 = "[" + bdAddress + "][LE]> ";
		String temp = "[" + bdAddress + "][LE]>";
		while ((readlen = br.read(chars, 0, chars.length)) != -1) {
			String str = new String(chars, 0, readlen);
			if (str.contains("Notification handle = 0x0010")){
				System.out.println(str.substring(40));
			}
			else
				System.out.println(str);
			if (temp0.equals(str)) {
				status = "connectting";
				System.out.println("connecting");
				out.write("connect\n".getBytes());
				out.flush();
				Thread.sleep(1000);
			} else if (status.equals("connectting")
					&& str.indexOf(temp) >= 0) {
				status = "test";
				System.out.println("connectted");
				out.write("char-write-req 0x0011 0100 -listen\n".getBytes());
				out.flush();
				Thread.sleep(1000);
			} 
		}
	}

	public static void main(String a[]) throws Exception {
		commandline testA = new commandline();
		testA.lescan();
		testA.bdConnect();
	}
}
