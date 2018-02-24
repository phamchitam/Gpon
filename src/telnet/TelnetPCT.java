package telnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

public class TelnetPCT {
	String host;
	int port;
	TelnetClient telnet = new TelnetClient();
	PrintWriter out;
	BufferedReader in;
	String prompt;

	public TelnetPCT(String host, int port, String prompt) throws SocketException, IOException {
		this.host = host;
		this.port = port;
		this.prompt = prompt;
		telnet.connect(host, port);
		in = new BufferedReader(new InputStreamReader(telnet.getInputStream()));
		out = new PrintWriter(telnet.getOutputStream(), true);
	}

	public String readUntil(String pattern) throws IOException {
		StringBuffer sb = new StringBuffer();
		char lastchar = pattern.charAt(pattern.length() - 1);
		char c = (char) in.read();
		while (true) {
			//System.out.print(c);
			sb = sb.append((char)c);
			if (sb.toString().endsWith(prompt) && !pattern.equals(prompt)){
				return sb.toString();
			}
			if (sb.toString().endsWith("--More--")){
				
				out.print(" ");
				out.flush();
				for (int i=0; i<27; i++){
					int tmp = in.read();
				}
				sb.append("\n");
		//	System.out.print("\n");
			}
			if (c == lastchar){
				if (sb.toString().endsWith(pattern)){
					return sb.toString();
				}
			}
			c = (char) in.read();
			
		}
	}
	public void write(String cmd){
		try 
		{ 
			out.println(cmd);
			
			//System.out.println(cmd);
		} 
		catch (Exception e)
			{
				e.printStackTrace();
			}
	}
	public String sendCommand(String cmd) throws IOException{
		write(cmd);
		return readUntil(prompt);
	}
	
	public void disconnect() throws IOException{
		telnet.disconnect();
	}

}
