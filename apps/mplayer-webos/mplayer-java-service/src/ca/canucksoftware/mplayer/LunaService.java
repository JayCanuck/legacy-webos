package ca.canucksoftware.mplayer;

import com.palm.luna.LSException;
import com.palm.luna.service.LunaServiceThread;
import com.palm.luna.service.ServiceMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class LunaService extends LunaServiceThread {
	private String hwVersion;

	public LunaService() {
		hwVersion = "1.0.0";
	}
	
	@LunaServiceThread.PublicMethod
	public void status(ServiceMessage msg) throws JSONException, LSException {
		JSONObject reply = new JSONObject();
		reply.put("returnValue",true);
		msg.respond(reply.toString());
	}
	
	@LunaServiceThread.PublicMethod
	public void version(ServiceMessage msg) throws JSONException, LSException {
		JSONObject reply = new JSONObject();
		reply.put("version", hwVersion);
		msg.respond(reply.toString());
	}
	
	@LunaServiceThread.PublicMethod
	public void list(ServiceMessage msg) throws JSONException, LSException {
		final String[] extList = {"mpeg", "mpg", "vob", "avi", "ogg", "ogv", "asf", "wmv",
				"qt", "mov", "mp4", "rm", "rv", "mkv", "flv", "wma", "oga", "asx", "ra",
				"mp3", "wav", "3gp", "flac"};
		JSONObject json = msg.getJSONPayload();
		if(json.has("path")) {
			ArrayList<JSONObject> fileList = new ArrayList<JSONObject>();
			ArrayList<JSONObject> dirList = new ArrayList<JSONObject>();
			JSONObject reply = new JSONObject();
			String path = json.getString("path");
			if(!path.startsWith("/")) {
				path = "/" + path;
			}
			if(!path.endsWith("/")) {
				path += "/";
			}
			File f = new File(path);
			if(f.exists()) {
				File[] files = f.listFiles();
				for(int i=0; i<files.length; i++) {
					String name = formatFilename(files[i]);
					if(!name.startsWith(".")) {
						if(files[i].isFile()) {
							String ext = getExt(files[i]);
							if(Arrays.asList(extList).contains(ext)) {
								JSONObject entry = new JSONObject();
								entry.put("name", name);
								entry.put("type", ext);
								entry.put("path", path + name);
								fileList.add(entry);
							}
						} else {
							JSONObject entry = new JSONObject();
							entry.put("name", name);
							entry.put("type", "dir");
							entry.put("path", path + name);
							dirList.add(entry);
						}
						
					}
				}
				Collections.sort(fileList, new JSONSorter());
				Collections.sort(dirList, new JSONSorter());
				reply.put("files", new JSONArray(fileList));
				reply.put("dirs", new JSONArray(dirList));
				msg.respond(reply.toString());
			} else {
				if(path.equalsIgnoreCase("/media/internal/video/")) {
					f.mkdirs();
					reply.put("files", new JSONArray(fileList));
					reply.put("dirs", new JSONArray(dirList));
					msg.respond(reply.toString());
				} else {
					msg.respondError("ERROR", "Directory does not exist.");
				}
			}
		} else {
			msg.respondError("ERROR", "Improperly formatted request.");
		}
	}
	
	private String formatFilename(File f) {
		String name = f.getName();
		int j;
		if(name.charAt(name.length()-1)=='/'
			|| name.charAt(name.length()-1)=='\\')
			name = name.substring(0, name.length()-1);
		j = name.lastIndexOf("\\");
		if(j>-1)
			name = name.substring(j+1);
		j = name.lastIndexOf("/");
		if(j>-1)
			name = name.substring(j+1);
		return name;
	}
	
	private String getExt(File f) {
		int start;
		String s = f.getName();
		start = s.lastIndexOf(".");
		if(start>-1)
			s = s.substring(start+1);
		else
			s = "---";
		if(s=="")
			s = "---";
		return s;
	}
	
	@LunaServiceThread.PublicMethod
	public void open(ServiceMessage msg) throws JSONException,
			LSException, IOException {
		final String mplayer = "/media/cryptofs/apps/usr/palm/services/" +
				"ca.canucksoftware.mplayer/bin/mplayer";
		if(msg.getJSONPayload().has("file")) {
			String filepath = msg.getJSONPayload().getString("file");
			File f = new File(filepath);
			if(f.exists()) {
				if(filepath.startsWith("/media/internal/")) {
					ShellScript ss = new ShellScript();
					String exec = mplayer + " -fs " + "-framedrop -pp 10";
					if(msg.getJSONPayload().has("subtitle")) {
						exec += " -sub \"" + msg.getJSONPayload().getString("subtitle")
								+ "\"";
					}
					exec += " \"" + filepath + "\" &";
					ss.add(exec);
					ss.start();
					msg.respondTrue();
				} else {
					msg.respondError("ERROR", "That section of the device is off" +
							" limits for data writing/deleting.");
				}
			} else {
				msg.respondError("ERROR", "File/directory does not exist.");
			}
		} else {
			msg.respondError("ERROR", "Improperly formatted request.");
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void exists(ServiceMessage msg) throws JSONException,
			LSException, IOException {
		if(msg.getJSONPayload().has("file")) {
			JSONObject reply = new JSONObject();
			String filepath = msg.getJSONPayload().getString("file");
			File f = new File(filepath);
			reply.put("exists", f.exists());
			msg.respond(reply.toString());
		} else {
			msg.respondError("ERROR", "Improperly formatted request.");
		}
	}

	@LunaServiceThread.PublicMethod
	public void delete(ServiceMessage msg) throws JSONException,
			LSException, IOException {
		if(msg.getJSONPayload().has("file")) {
			String filepath = msg.getJSONPayload().getString("file");
			File f = new File(filepath);
			if(f.exists()) {
				if(filepath.startsWith("/media/internal/")) {
					CommandLine cmd;
					if(f.isDirectory()) {
						cmd = new CommandLine(msg, new String[] {"/bin/rm",
								"-f", "-r", f.getCanonicalPath()});
					} else {
						cmd = new CommandLine(msg, new String[] {"/bin/rm",
								"-f", f.getCanonicalPath()});
					}
					cmd.start();
				} else {
					msg.respondError("ERROR", "That section of the device is off" +
							" limits for data writing/deleting.");
				}
			} else {
				msg.respondError("ERROR", "File/directory does not exist.");
			}
		} else {
			msg.respondError("ERROR", "Improperly formatted request.");
		}
	}
	
	@LunaServiceThread.PublicMethod
	public void registerAsHandler(ServiceMessage msg) throws JSONException, LSException {
		JSONObject json = msg.getJSONPayload();
		if(validForPrivate(msg)) {
			String value = "disable";
			ShellScript ss = new ShellScript(msg, "/var/handling.sh");
			ss.readFromResource("scripts/handling.sh");
			if(json.has("value") && json.getBoolean("value")) {
				value = "enable";
			}
			ss.setParams(new String[] {value});
			ss.start();
		} else {
			msg.respondError("ERROR", "This is a private function reserved for " +
					"Internalz.");
		}
	}
	
	private boolean validForPrivate(ServiceMessage msg) {
		String appId = msg.getApplicationID();
		return ((appId == null) || appId.equalsIgnoreCase("ca.canucksoftware.mplayer"));
	}
	
	//Sorting class
	private class JSONSorter implements Comparator<JSONObject> {
        public int compare(JSONObject a, JSONObject b) {
            int i;
        	try {
        		String nameA = a.getString("name");
        		String nameB = b.getString("name");
       			i = nameA.compareToIgnoreCase(nameB);
        	} catch(Exception e) {
        		i = 0;
        	}
            return i;
        }
    }
}
