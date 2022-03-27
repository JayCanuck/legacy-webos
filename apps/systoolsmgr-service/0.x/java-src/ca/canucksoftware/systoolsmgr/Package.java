package ca.canucksoftware.systoolsmgr;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.*;

import com.palm.luna.service.ServiceMessage;

public class Package {
	private String output;
	private ServiceMessage msg;
	
	public Package(ServiceMessage sm) {
		output = null;
		msg = sm;
	}
	
	public String getOutput() {
		return output;
	}
	
	public boolean isInstalled(String appId) {
		return new File("/media/cryptofs/apps/usr/lib/ipkg/" + appId
				+ ".control").exists();
	}
	
	public void install(String file, String appId) {
		ShellScript cmds = new ShellScript(msg);
		cmds.add("/usr/bin/ipkg -o /media/cryptofs/apps -force-depends -force-reinstall"
				+ " install " + file);
		cmds.add("if [ -e /media/cryptofs/apps/usr/lib/ipkg/" + appId + ".postinst ]");
		cmds.add("then");
		cmds.add("/bin/sh /media/cryptofs/apps/usr/lib/ipkg/" + appId + ".postinst");
		cmds.add("fi");
		cmds.start();
	}
	
	public void uninstall(String appId) {
		ShellScript cmds = new ShellScript(msg);
		cmds.add("if [ -e /media/cryptofs/apps/usr/lib/ipkg/" + appId + ".prerm ]");
		cmds.add("then");
		cmds.add("/bin/sh /media/cryptofs/apps/usr/lib/ipkg/" + appId + ".prerm");
		cmds.add("fi");
		cmds.add("/usr/bin/ipkg -o /media/cryptofs/apps -force-depends remove " + appId);
		cmds.start();
	}
	
	public JSONObject getInfo(String file) {
		JSONObject result = new JSONObject();
		CommandLine curr = new CommandLine("cd /media/cryptofs/apps/");
		if(curr.doCmd()) {
			curr = new CommandLine("/usr/bin/ar -x " + file + " control.tar.gz");
			if(curr.doCmd()) {
				curr = new CommandLine("/bin/tar xfz control.tar.gz");
				if(curr.doCmd()) {
					result = readInfo(file);
					if(result==null) {
						output = "Unable to read package information.";
					}
				} else {
					new File("/var/control.tar.gz").delete();
					output = curr.getResponse();
					result = null;
				}
			} else {
				output = curr.getResponse();
				result = null;
			}
		} else {
			output = curr.getResponse();
			result = null;
		}
		return result;
	}
	
	private JSONObject readInfo(String file) {
		JSONObject result = new JSONObject();
		File control = new File("/var/control");
		try {
			String name = null;
			String id = null;
			String ver = null;
			String dev = null;
			String size = String.valueOf(new File(file).length());
			String restart = "";
			BufferedReader br = new BufferedReader(new FileReader(control));
			String line = br.readLine();
			while(line!=null) {
				if(line.startsWith("Package")) {
                    id = line.substring(line.indexOf(":")+1).trim();
                }else if(line.startsWith("Description")){
                    name = line.substring(line.indexOf(":")+1).trim();
                }else if(line.startsWith("Version")){
                    ver = line.substring(line.indexOf(":")+1).trim();
                }else if(line.startsWith("Maintainer")){
                    dev = line.substring(line.indexOf(":")+1).trim();
                }else if(line.startsWith("Source")){
                	JSONObject source = new JSONObject(line.substring(
                			line.indexOf(":")+1).trim());
                    restart = source.getString("PostInstallFlags");
                }
				line = br.readLine();
			}
			br.close();
			JSONArray data = new JSONArray();
			if(name!=null) {
				data.put(arrayEntry("name", name));
			}
			if(id!=null) {
				data.put(arrayEntry("id", id));
			} else {
				id = "";
			}
			if(ver!=null) {
				data.put(arrayEntry("version", ver));
			}
			if(dev!=null) {
				data.put(arrayEntry("developer", dev));
			}
			if(size!=null) {
				data.put(arrayEntry("size", size));
			}
			result.put("data", data);
			result.put("appId", id);
			result.put("isInstalled", isInstalled(id));
			result.put("postinst", readScript("/var/postinst"));
			result.put("prerm", readScript("/var/prerm"));
			result.put("restart", restart);
			cleanup();
		} catch(Exception e) {
			result = null;
		}
		return result;
	}
	
	private JSONObject arrayEntry(String name, String value) throws JSONException {
		JSONObject result = new JSONObject();
		result.put(name, value);
		return result;
	}
	
	private String readScript(String file) throws IOException {
		String result = "";
		File curr = new File(file);
		if(curr.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			while(line!=null) {
				result += line;
				line = br.readLine();
				if(line!=null) {
					result += "\n";
				}
			}
			br.close();
		}
		return result;
	}
	
	private void cleanup() {
		File curr = new File("/var/control");
		if(curr.exists())
			curr.delete();
		curr = new File("/var/preinst");
		if(curr.exists())
			curr.delete();
		curr = new File("/var/postinst");
		if(curr.exists())
			curr.delete();
		curr = new File("/var/prerm");
		if(curr.exists())
			curr.delete();
		curr = new File("/var/postrm");
		if(curr.exists())
			curr.delete();
		curr = new File("/var/control.tar.gz");
		if(curr.exists())
			curr.delete();
	}
}
