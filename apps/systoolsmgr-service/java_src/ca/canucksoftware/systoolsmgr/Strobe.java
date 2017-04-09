package ca.canucksoftware.systoolsmgr;

public class Strobe extends Thread {
	private long strobeVal;
	private long flash;
	private boolean isOn;
	private boolean stop;

    public Strobe(long sv, long f) {
    	stop = false;
    	strobeVal = sv;
    	flash = f;
    	isOn = false;
    }
	
	public void run() {
		//long startMilli;
		//long currTime;
		ShellScript curr;
		//startMilli = System.currentTimeMillis();
		while(!stop) {
			//currTime = System.currentTimeMillis() - startMilli;
			//if(currTime>=strobeVal) {
				if(isOn) {
					curr = Torch.turnOffScript();
				} else {
					curr = Torch.turnOnScript(flash);
				}
				curr.doCmd();
				isOn = !isOn;
				try {
					sleep(strobeVal);
				} catch(Exception e) {}
				//startMilli = System.currentTimeMillis();
			//}
		}
    }
	
	public void cancel() {
		stop = true;
		interrupt();
	}
}
