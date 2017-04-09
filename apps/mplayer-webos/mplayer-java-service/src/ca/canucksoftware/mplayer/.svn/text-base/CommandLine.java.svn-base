package ca.canucksoftware.mplayer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;
import com.palm.luna.service.ServiceMessage;

public class CommandLine extends Thread {
	private ServiceMessage message;
	private String[] command;
	private String response;
	private int returnCode;
	
	public CommandLine(String cmd) {
		this(null, cmd.split(" "));
	}
	
	public CommandLine(ServiceMessage msg, String cmd) {
		this(msg, cmd.split(" "));
	}
	
	public CommandLine(String[] cmd) {
		this(null, cmd);
	}
	
	public CommandLine(ServiceMessage msg, String[] cmd) {
		super();
		message = msg;
		command = cmd;
		returnCode = -1;
	}
	
	public String[] getCommand() {
		return command;
	}
	
	public void setCommand(String[] cmd) {
		command = cmd;
	}
	
	public String getResponse() { return response; }
	
	public Process createProcess() {
		Process p = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			p = pb.start();
		} catch(Exception e) {}
		return p;
	}
	
	public boolean doCmd() {
		try {
			response = null;
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(false);
			Process p = pb.start();
			OutputStream os = p.getOutputStream();
			os.flush();
			os.close();
			String stdout = getTextFromStream(p.getInputStream());
			String stderr = getTextFromStream(p.getErrorStream());
			if(p.waitFor() != 0) {
				returnCode = p.exitValue();
			} else {
				returnCode = 0;
			}
			if(returnCode==0) {
				response = stdout;
			} else {
				response = stderr + stdout;
			}
		} catch(Exception e) {
			response = e.getMessage();
			returnCode = -1;
		}
		return (returnCode==0);
	}
	
	private String getTextFromStream(InputStream is){
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			line = br.readLine();
			while (line!=null) {
				if(line.trim().length()!=0) {
					result += line.trim();
				}
				line = br.readLine();
				if(line!=null) {
					if(line.trim().length()!=0) {
						result += "\n";
					}
				}
			}
			br.close();
		} catch(Exception e) {
			result = "";
		}
		return result;
	}
	
	public void run() {
		try {
			if(doCmd()) {
				if(message!=null) {
					JSONObject reply = new JSONObject();
					reply.put("output", response);
					JSONArray cmds = new JSONArray(Arrays.asList(command));
					reply.put("commands", cmds);
					message.respond(reply.toString());
				}
			} else {
				if(message!=null) {
					message.respondError(String.valueOf(returnCode), response);
				}
			}
		} catch(Exception e) {
			try {
				if(message!=null) {
					message.respondError("-1", e.getMessage());
				}
			} catch(Exception e2) {}
		}
	}
}
