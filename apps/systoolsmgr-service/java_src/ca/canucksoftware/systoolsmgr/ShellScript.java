package ca.canucksoftware.systoolsmgr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.palm.luna.service.ServiceMessage;

public class ShellScript extends CommandLine {
	private static final String SCRIPTFILE = "/media/internal/shellScript.sh";
	private File script;
	private ArrayList<String> commands;
	
	public ShellScript() {
		this(null, SCRIPTFILE);
	}
	
	public ShellScript(ServiceMessage msg) {
		this(msg, SCRIPTFILE);
	}
	
	public ShellScript(String file) {
		this(null, file);
	}
	
	public ShellScript(ServiceMessage msg, String file) {
		super(msg, new String[] {"/bin/sh", file});
		script = new File(file);
		commands = new ArrayList<String>();
	}
	
	public void setParams(String[] params) {
		String[] newCmd = new String[super.getCommand().length + params.length];
		int index = 0;
		for(int i=0; i<super.getCommand().length; i++) {
			newCmd[index] = super.getCommand()[i];
			index++;
		}
		for(int i=0; i<params.length; i++) {
			newCmd[index] = params[i];
			index++;
		}
		super.setCommand(newCmd);
	}
	
	public void add(String cmd) { commands.add(cmd); }
	
	public void add(int index, String cmd) { commands.add(index, cmd); }
	
	public void set(int index, String cmd) { commands.set(index, cmd); }
	
	public int indexOf(String cmd) { return commands.indexOf(cmd); }
	
	public void remove(String cmd) { commands.remove(cmd); }
	
	public void remove(int index) { commands.remove(index); }
	
	public void clear() { commands.clear(); }
	
	public boolean isEmpty() { return commands.isEmpty(); }
	
	public int size() { return commands.size(); }
	
	public void readFromResource(String resource) {
		try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
            		super.getClass().getResourceAsStream(resource)));
            String line = br.readLine();
            while(line!=null) {
            	commands.add(line);
            	line = br.readLine();
            }
        } catch (Exception e) {}
	}
	
	private void buildScript() {
		try {
			cleanupScript();
			BufferedWriter bw = new BufferedWriter(new FileWriter(script));
			for(int i=0; i<commands.size(); i++) {
				bw.write(commands.get(i) + "\n");
			}
			bw.write("exit 0\n");
			bw.flush();
			bw.close();
		} catch(Exception e) {}
	}
	
	private void cleanupScript() {
		if(script.exists()) {
			script.delete();
		}
	}
	
	public boolean doCmd() {
		buildScript();
		boolean result = super.doCmd();
		cleanupScript();
		return result;
	}
	
	public void run() {
		buildScript();
		super.run();
		cleanupScript();
	}
	
}
