package ca.canucksoftware.systoolsmgr;

import com.palm.luna.service.ServiceMessage;

public class Torch {
	public static ShellScript turnOnScript(long flash) {
		return turnOnScript(null, flash);
	}
	
	public static ShellScript turnOnScript(ServiceMessage msg, long flash) {
		ShellScript ss = new ShellScript(msg);
		ss.add("/bin/echo -n 1 >/sys/class/i2c-adapter/i2c-2/2-0033/avin");
		ss.add("/bin/echo -n " + flash + 
				"mA >/sys/class/i2c-adapter/i2c-2/2-0033/torch_current");
		ss.add("/bin/echo -n torch >/sys/class/i2c-adapter/i2c-2/2-0033/mode");
		return ss;
	}
	
	public static ShellScript turnOffScript() {
		return turnOffScript(null);
	}
	
	public static ShellScript turnOffScript(ServiceMessage msg) {
		ShellScript ss = new ShellScript(msg);
		ss.add("echo -n shutdown >/sys/class/i2c-adapter/i2c-2/2-0033/mode");
		ss.add("echo -n 0mA >/sys/class/i2c-adapter/i2c-2/2-0033/torch_current");
		ss.add("echo -n 0 >/sys/class/i2c-adapter/i2c-2/2-0033/avin");
		return ss;
	}
}
