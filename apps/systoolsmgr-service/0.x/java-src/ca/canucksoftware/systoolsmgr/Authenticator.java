package ca.canucksoftware.systoolsmgr;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import org.json.JSONArray;
import org.json.JSONObject;


public class Authenticator {
	private final AllowedApp[] allowed = new AllowedApp[] {
			new AllowedApp("ca.canucksoftware.internalz", 
					new String[] {"app/assistants/ipk-dialog-assistant.js",
							"app/assistants/patch-dialog-assistant.js"},
					new String[] {"BLAH", "BLAH"}),
			new AllowedApp("ca.canucksoftware.sysmonitor",
					new String[] {"app/assistants/appsview-assistant.js"},
					new String[] {"BLAH"})
			};
	private String appId;
	private String path;
	private String key;
	
	public Authenticator(String id, String method) {
		appId = id;
		path = "/var/usr/palm/applications" + appId + "/";
		key = method;
	}
	
	public boolean isAuthentic() {
		boolean result;
		AllowedApp app = searchAllowed();
		if(app!=null) {
			result = app.md5sumMatch();
			if(result) {
				JSONArray srcList = getSources();
				for(int i=0; i<srcList.length(); i++) {
					try {
						JSONObject curr = srcList.getJSONObject(i);
						if(curr.has("source")) {
							String currFile = curr.getString("source");
							if(!app.isCritical(currFile)) {
								result &= !keyFoundInFile(path + currFile);
							}
						}
					} catch(Exception e) {}
				}
			}
		} else {
			result = false;
		}
		return result;
	}
	
	private AllowedApp searchAllowed() {
		AllowedApp result = null;
		for(int i=0; i<allowed.length; i++) {
			if(allowed[i].getId().equalsIgnoreCase(appId)) {
				result = allowed[i];
				break;
			}
		}
		return result;
	}
	
	private JSONArray getSources() {
		JSONArray result = null;
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path + "sources.json"));
			String line = br.readLine();
			while(line!=null) {
				text += line;
				line = br.readLine();
				if(line!=null) {
					text += " ";
				}
			}
			br.close();
			result = new JSONArray(text.trim());
		} catch(Exception e) {
			result = null;
		}
		return result;
	}
	
	private boolean keyFoundInFile(String file) {
		BufferedReader br = null;
		boolean result = false;
		try {
			br = new BufferedReader(
				new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
			String line = br.readLine();
			while(line!=null) {
				if(line.toLowerCase().contains(key.toLowerCase())) {
					result = true;
					break;
				}
				line = br.readLine();
			}
			br.close();
		} catch(Exception e) {
			result = true;
		}
		return result;
	}

	
	private class AllowedApp {
		private String appId;
		private String[] critical;
		private String[] md5sums;
		
		public AllowedApp(String id, String[] files, String[] md5) {
			appId = id;
			critical = files;
			md5sums = md5;
		}
		
		public String getId() {
			return appId;
		}
		
		public boolean isCritical(String file) {
			boolean result = false;
			for(int i=0; i<critical.length; i++) {
				if(critical[i].equalsIgnoreCase(file)) {
					result = true;
					break;
				}
			}
			return result;
		}
		
		public boolean md5sumMatch() {
			boolean result = true;
			String path = "/var/usr/palm/applications" + appId + "/";
			for(int i=0; i<critical.length; i++) {
				result &= md5sums[i].equalsIgnoreCase(getMD5(path + critical[i]));
			}
			return result;
		}
		
		private String getMD5(String filename) {
			FileInputStream fis = null;
			String result = "";
			try {
				fis = new FileInputStream(filename);
			    byte[] buffer = new byte[2048];
			    MessageDigest md5 = MessageDigest.getInstance("MD5");
			    int numRead;
			    do {
			    	numRead = fis.read(buffer);
			    	if (numRead > 0) {
			    		md5.update(buffer, 0, numRead);
			    	}
			    } while(numRead != -1);
			    fis.close();
			    result = hexToString(md5.digest());
			} catch(Exception e) {
				result = "";
			}
		    return result;
		}
		
		private String hexToString(byte[] data) {
			final byte[] HEX_CHAR_TABLE = {
					(byte)'0', (byte)'1', (byte)'2', (byte)'3',
				    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
				    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
				    (byte)'c', (byte)'d', (byte)'e', (byte)'f'};
			byte[] hex = new byte[2 * data.length];
		    int index = 0;
		    String result = "";

		    for (byte b : data) {
		      int v = b & 0xFF;
		      hex[index++] = HEX_CHAR_TABLE[v >>> 4];
		      hex[index++] = HEX_CHAR_TABLE[v & 0xF];
		    }
		    
		    try {
		    	result = new String(hex, "ASCII");
		    } catch(Exception e) {
		    	result = "";
		    }
		    return result;
		}
	}
}