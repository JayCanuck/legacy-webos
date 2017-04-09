package ca.canucksoftware.systoolsmgr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import com.palm.luna.LSException;
import com.palm.luna.service.LunaServiceThread;
import com.palm.luna.service.ServiceMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class LunaService extends LunaServiceThread {
	private String hwVersion;
	private long flash;
	private Strobe strobe;
	private long strobeVal;
	
	public LunaService() {
		this.hwVersion = "1.0.2";
		strobeVal = 0;
		flash = 0;
		strobe = null;
	}
	
	@LunaServiceThread.PublicMethod
	public void status(ServiceMessage msg) throws JSONException, LSException {
		JSONObject reply = new JSONObject();
		reply.put("returnValue",true);
		msg.respond(reply.toString());
	}

	@LunaServiceThread.PublicMethod
	public void version(ServiceMessage message)	{
		try {
			StringBuilder sb = new StringBuilder(8192);
			sb.append("{version:");
			sb.append(JSONObject.quote(this.hwVersion));
			sb.append("}");
			message.respond(sb.toString());
		} catch (LSException e) {
			this.logger.severe("", e);
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void setDisplay(ServiceMessage msg) throws LSException, JSONException  {
		if (msg.getJSONPayload().has("state")) {
			File f = new File("/sys/class/display/lcd.0/state");
			String choice = msg.getJSONPayload().getString("state").toLowerCase();
			ShellScript cmds = new ShellScript(msg);
			if(choice.equals("blank")) {
				if(f.exists()) {
					cmds.add("/bin/echo 0 > /sys/class/display/lcd.0/state");
					cmds.start();
				} else {
					msg.respondError("3", "'Blank' display state not supported on " +
							"this device.");
				}
			} else if(choice.equals("on") || choice.equals("dimmed") ||
					choice.equals("off")) {
				if(f.exists()) {
					cmds.add("/bin/echo 1 > /sys/class/display/lcd.0/state");
				}
				cmds.add("/usr/bin/luna-send -n 1 palm://com.palm.display/control" +
						"/setState '{\"state\":\"" + choice + "\"}'");
				cmds.start();
			} else {
				msg.respondError("2", "Invalid state.");
			}
		} else {
			msg.respondError("1", "Improperly formatted request.");
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void onWhenConnected(ServiceMessage msg) throws LSException, JSONException  {
		if(msg.getJSONPayload().has("status")) {
			boolean choice = msg.getJSONPayload().getBoolean("status");
			CommandLine cmd = new CommandLine(msg, "/usr/bin/luna-send" +
					" -n 1 palm://com.palm.display/control/setProperty " +
					"'{\"onWhenConnected\":\"" + choice +  "\"}'");
			cmd.start();
		} else {
			msg.respondError("1", "Improperly formatted request.");
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void javaGC(ServiceMessage msg) throws LSException, JSONException  {
		System.gc();
		msg.respondTrue();
	}
	
	@LunaServiceThread.PublicMethod
	public void javascriptGC(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine(msg, "/usr/bin/luna-send" +
				" -n 1 palm://com.palm.lunastats/gc '{}'");
		cmd.start();
	}
	
	@LunaServiceThread.PublicMethod
	public void runningApps(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine("/usr/bin/luna-send" +
				" -n 1 palm://com.palm.lunastats/getStats '{}'");
		if(cmd.doCmd()) {
			String out = cmd.getResponse();
			int i = out.indexOf("{");
			if(i>-1) {
				JSONObject reply = new JSONObject(out.substring(i));
				msg.respond(reply.toString());
			} else {
				msg.respondError("2", cmd.getResponse());
			}
		} else {
			msg.respondError("1", cmd.getResponse());
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void runningProcesses(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine("/bin/ps aux");
		if(cmd.doCmd()) {
			String[] lines = cmd.getResponse().trim().split("\n");
			boolean ok = false;
			JSONArray results = new JSONArray();
			for(int i=0; i<lines.length-1; i++) {
				if(lines[i].startsWith("USER") && !ok) {
					ok = true;
				} else if (ok) {
					String[] tokens = lines[i].trim().split("\\s+");
					JSONObject curr = new JSONObject();
					curr.put("user", tokens[0]);
					curr.put("id", Integer.parseInt(tokens[1]));
					curr.put("cpu", Integer.parseInt(tokens[2]));
					curr.put("memory", Integer.parseInt(tokens[3]));
					curr.put("vsz", Integer.parseInt(tokens[4]));
					curr.put("rss", Integer.parseInt(tokens[5]));
					curr.put("tty", tokens[6]);
					curr.put("stat", tokens[7]);
					curr.put("start", tokens[8]);
					curr.put("time", tokens[9]);
					String command = "";
					for(int j=10; j<tokens.length; j++) {
						command += tokens;
						if(j!=tokens.length-1) {
							command += " ";
						}
					}
					curr.put("command", command);
					results = results.put(curr);
				}
			}
			JSONObject reply = new JSONObject();
			reply.put("processes", results);
			msg.respond(reply.toString());
		} else {
			msg.respondError("1", cmd.getResponse());
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void killProcess(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd;
		if(msg.getJSONPayload().has("name")) {
			cmd = new CommandLine(msg, "/usr/bin/killall -9 "
					+ msg.getJSONPayload().getString("name"));
			cmd.start();
		} else if(msg.getJSONPayload().has("id")) {
			cmd = new CommandLine(msg, "/bin/kill -9 "
					+ msg.getJSONPayload().getLong("id"));
			cmd.start();
		} else {
			msg.respondError("1", "Improperly formatted request.");
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void cpuLoad(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine("/bin/cat /proc/loadavg");
		if(cmd.doCmd()) {
			String[] tokens = cmd.getResponse().trim().split("\\s+");
			JSONObject reply = new JSONObject();
			reply.put("onemin", Integer.parseInt(tokens[0]));
			reply.put("fivemin", Integer.parseInt(tokens[1]));
			reply.put("tenmin", Integer.parseInt(tokens[2]));
			reply.put("runningprocesses", tokens[3]);
			reply.put("lastprocess", Integer.parseInt(tokens[4]));
			msg.respond(reply.toString());
		} else {
			msg.respondError("1", cmd.getResponse());
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void cpuTemperature(ServiceMessage msg) throws LSException, JSONException  {
		if(new File("/sys/devices/platform/omap34xx_temp/temp1_input").exists()) {
			CommandLine cmd = new CommandLine(msg, "/bin/cat " +
					"/sys/devices/platform/omap34xx_temp/temp1_input");
			if(cmd.doCmd()) {
				JSONObject reply = new JSONObject();
				reply.put("value", Integer.parseInt(cmd.getResponse().trim()));
				msg.respond(reply.toString());
			} else {
				msg.respondError("1", cmd.getResponse());
			}
		} else {
			msg.respondError("1", "UberKernel not installed");
		}
		
	}
	
	@LunaServiceThread.PublicMethod
	public void memInfo(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine("/bin/cat /proc/meminfo");
		if(cmd.doCmd()) {
			String[] lines = cmd.getResponse().trim().split("\n");
			JSONObject reply = new JSONObject();
			for(int i=0; i<lines.length; i++) {
				if(lines[i].trim().length()>0) {
					String[] tokens = lines[i].trim().split("\\s+");
					reply.put(tokens[0].trim().replaceAll(":", "").toLowerCase(),
							Integer.parseInt(tokens[1]));
				}
			}
			msg.respond(reply.toString());
		} else {
			msg.respondError("1", cmd.getResponse());
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void cpuInfo(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine("/bin/cat /proc/cpuinfo");
		if(cmd.doCmd()) {
			String[] lines = cmd.getResponse().trim().split("\n");
			JSONObject reply = new JSONObject();
			for(int i=0; i<lines.length; i++) {
				if(lines[i].trim().length()>0) {
					String first = lines[i].substring(0, lines[i].indexOf(":"));
					first = first.trim().replaceAll(" ", "_").replaceAll("\t", "");
					String second = lines[i].substring(lines[i].indexOf(":")+1);
					reply.put(first.toLowerCase(), second.trim());
				}
			}
			msg.respond(reply.toString());
		} else {
			msg.respondError("1", cmd.getResponse());
		}
	}
	
	//@LunaServiceThread.PublicMethod
	public void getPackageInfo(ServiceMessage msg) throws LSException, JSONException  {
		if(msg.getJSONPayload().has("file")) {
			Package ipk = new Package(msg);
			JSONObject reply = ipk.getInfo(msg.getJSONPayload().getString("file"));
			if(reply!=null) {
				msg.respond(reply.toString());
			} else {
				msg.respondError("2", ipk.getOutput());
			}
		} else {
			msg.respondError("1", "Improperly formatted request.");
		}
	}
	
	//@LunaServiceThread.PublicMethod
	public void isPackageInstalled(ServiceMessage msg) throws LSException, JSONException  {
		if(msg.getJSONPayload().has("appId")) {
			Package ipk = new Package(msg);
			JSONObject reply = new JSONObject();
			reply.put("isInstalled",ipk.isInstalled(msg.getJSONPayload()
					.getString("appId")));
			msg.respond(reply.toString());
		} else {
			msg.respondError("1", "Improperly formatted request.");
		}
	}
	
	//@LunaServiceThread.PublicMethod
	public void installIpk(ServiceMessage msg) throws LSException, JSONException  {
		if(msg.getJSONPayload().has("file") && msg.getJSONPayload().has("appId")) {
			Package ipk = new Package(msg);
			ipk.install(msg.getJSONPayload().getString("file"),
					msg.getJSONPayload().getString("appId"));
		} else {
			msg.respondError("1", "Improperly formatted request.");
		}
	}
	
	//@LunaServiceThread.PublicMethod
	public void packageUninstall(ServiceMessage msg) throws LSException, JSONException  {
		if(msg.getJSONPayload().has("appId")) {
			Package ipk = new Package(msg);
			ipk.uninstall(msg.getJSONPayload().getString("appId"));
		} else {
			msg.respondError("1", "Improperly formatted request.");
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void rescan(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine(msg, "/usr/bin/luna-send -n 1 " +
				"palm://com.palm.applicationManager/rescan '{}'");
		cmd.start();
	}
	
	@LunaServiceThread.PublicMethod
	public void javaRestart(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine(msg, "/usr/bin/killall -9 java");
		cmd.start();
	}
	
	@LunaServiceThread.PublicMethod
	public void novacomRestart(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine(msg, "/usr/bin/killall -HUP novacomd");
		cmd.start();
	}
	
	@LunaServiceThread.PublicMethod
	public void lunaRestart(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine(msg, "/usr/bin/killall -HUP LunaSysMgr");
		cmd.start();
	}
	
	@LunaServiceThread.PublicMethod
	public void deviceRestart(ServiceMessage msg) throws LSException, JSONException  {
		CommandLine cmd = new CommandLine(msg, "/sbin/reboot");
		cmd.run();
	}
	
	@LunaServiceThread.PublicMethod
	public void flashOn(ServiceMessage msg) throws LSException, JSONException  {
		JSONObject json = msg.getJSONPayload();
		json.put("state", "on");
		msg.setPayload(json.toString());
		setFlash(msg);
	}
	
	@LunaServiceThread.PublicMethod
	public void flashOff(ServiceMessage msg) throws LSException, JSONException  {
		JSONObject json = msg.getJSONPayload();
		json.put("state", "off");
		msg.setPayload(json.toString());
		setFlash(msg);
	}
	
	@LunaServiceThread.PublicMethod
	public void setFlash(ServiceMessage msg) throws LSException, JSONException  {
		JSONObject json = msg.getJSONPayload();
		if(json.has("state")) {
			String state = json.getString("state");
			long value = 100;
			if(msg.getJSONPayload().has("value")) {
				value = msg.getJSONPayload().getLong("value");
			}
			if(value==0) {
				state = "off";
			}
			if(state.equalsIgnoreCase("on")) {
				strobeVal = 0;
				flash = value;
				if(strobe!=null) {
					strobe.interrupt();
					strobe.cancel();
					strobe = null;
				}
				Torch.turnOnScript(msg, flash).start();
			} else if(state.equalsIgnoreCase("strobe")) {
				strobeVal = 150;
				flash = value;
				if(msg.getJSONPayload().has("speed")) {
					strobeVal = msg.getJSONPayload().getLong("speed");
				}
				if(strobe!=null) {
					strobe.cancel();
					strobe = null;
				}
				strobe = new Strobe(strobeVal, flash);
				strobe.start();
				msg.respondTrue();
			} else if(state.equalsIgnoreCase("off")) {
				flash = 0;
				strobeVal = 0;
				if(strobe!=null) {
					strobe.interrupt();
					strobe.cancel();
					strobe = null;
				}
				Torch.turnOffScript(msg).start();
			} else {
				msg.respondError("2", "Invalid state");
			}
		} else {
			msg.respondError("1", "Improperly formatted request.");
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void flashState(ServiceMessage msg) throws LSException, JSONException  {
		JSONObject reply = new JSONObject();
		if(flash==0) { 
			reply.put("state", "off");
		} else {
			if(strobe==null) {
				reply.put("state", "on");
			} else {
				reply.put("state", "strobe");
			}
		}
		reply.put("speed", strobeVal);
		reply.put("value", flash);
		msg.respond(reply.toString());
	}
	
	@LunaServiceThread.PublicMethod
	public void syncTime(ServiceMessage msg) throws LSException, JSONException  {
		String server = "pool.ntp.org";
		if(msg.getJSONPayload().has("server")) {
			server = msg.getJSONPayload().getString("server");
		}
		CommandLine cmd = new CommandLine(msg, new String [] {"/usr/bin/ntpdate",
				"-u", server});
		cmd.start();
	}
	
	@LunaServiceThread.PublicMethod
	public void updateClockThemes(ServiceMessage msg) throws LSException, JSONException  {
		if(msg.getApplicationID()==null) { //luna-send only
			final String THEMES_FILE = "/usr/palm/applications/com.palm.app.clock/themes" +
					"/themes.json";
			JSONObject payload = msg.getJSONPayload();
			String action = payload.getString("action");
			String id = payload.getString("id");
			String name = payload.getString("name");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(THEMES_FILE), "UTF-8"));
				String text = "";
				String line = br.readLine();
				while(line!=null) {
					text += line.trim();
					line = br.readLine();
				}
				br.close();
				JSONArray json = new JSONArray(text);
				ArrayList<JSONObject> array = new ArrayList<JSONObject>();
				boolean found = false;
				for(int i=0; i<json.length(); i++) {
					JSONObject curr = json.getJSONObject(i);
					if(curr.get("name").equals(id)) {
						if(!action.equalsIgnoreCase("remove")) {
							array.add(curr);
						}
						found = true;
						break;
					} else {
						array.add(curr);
					}
				}
				if(!found && action.equalsIgnoreCase("add")) {
					JSONObject newEntry = new JSONObject();
					newEntry.put("name", id);
					newEntry.put("nicename", name);
					newEntry.put("description", "");
					newEntry.put("source", "themes/" + id + "/");
					array.add(newEntry);
				}
				JSONArray newJson = new JSONArray(array);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(THEMES_FILE), "UTF-8"));
				bw.write(newJson.toString(4));
				bw.flush();
				bw.close();
				msg.respondTrue();
			} catch (Exception e) {
				msg.respondError("2", "Unable to update themes.json file.");
			}
		} else {
			msg.respondError("1", "Luna-send restricted request.");
		}
	}
}
