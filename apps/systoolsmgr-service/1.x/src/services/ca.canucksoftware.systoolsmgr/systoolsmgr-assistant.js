function StatusAssistant() {};  //status
StatusAssistant.prototype.run = function(future){
	future.result = {returnValue:true};
};

function VersionAssistant() {}; //version
VersionAssistant.prototype.run = function(future){
	future.result = {version:"1.0.6"};
};

function SetDisplayAssistant() {}; //setDisplay
SetDisplayAssistant.prototype.run = function(future) {
	this.file = "/sys/class/display/lcd.0/state";
	this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.systoolsmgr/"
			+ "scripts/setDisplay.sh";
	this.future = future;
	if(this.controller.args.state) {
		this.state = this.controller.args.state.toLowerCase();
		this.pathLib = require("path");
		if(this.state=="blank") {
			if(this.pathLib.existsSync(this.file)) {
				this.cmd = new CommandLine("/bin/echo 0 > " + this.file, this.future);
				this.cmd.run();
			} else {
				this.future.result = {errorCode:"ERROR", errorText:"'Blank' display state "
						+ "not supported on this device."};
			}
		} else if(this.state=="dimmed" || this.state=="on" || this.state=="off") {
			this.cmd = new CommandLine("/bin/sh " + this.script + " " + this.state,
					this.future);
			this.cmd.run();
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"Invalid state"};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function OnWhenConnectedAssistant() {}; //onWhenConnected
OnWhenConnectedAssistant.prototype.run = function(future) {
	this.future = future;
	if(this.controller.args.status!=undefined) {
		this.status = this.controller.args.status
		this.ls = new LunaSend("palm://com.palm.display/control/setProperty",
				{onWhenConnected:this.status}, this.future);
		this.ls.run();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function JavaGCAssistant() {}; //javaGC
JavaGCAssistant.prototype.run = function(future) {
	this.future = future;
	this.future.result = {errorCode:"ERROR", errorText:"There is no Java on webOS 2.0."};
};

function JavascriptGCAssistant() {}; //javascriptGC
JavascriptGCAssistant.prototype.run = function(future) {
	this.future = future;
	this.ls = new LunaSend("palm://com.palm.lunastats/gc", {}, this.future);
	this.ls.run();
};

function RunningAppsAssistant() {}; //runningApps
RunningAppsAssistant.prototype.run = function(future) {
	this.future = future;
	this.ls = new LunaSend("palm://com.palm.lunastats/getStats", {}, this.future);
	this.ls.run();
};

function RunningProcessesAssistant() {}; //runningProcesses
RunningProcessesAssistant.prototype.run = function(future) {
	this.future = future;
	this.cmd = new CommandLine("/bin/ps aux");
	this.cmd.run(function(response) {
		if(response.code==0) {
			this.processes = [];
			var lines = response.stdout.split("\n");
			var ok = false;
			for(var i=0; i<lines.length-1; i++) {
				if(lines[i].startsWith("USER") && !ok) {
					ok = true;
				} else if(ok) {
					var tokens = lines[i].split(/\s+/);
					var currProcess = {};
					currProcess.user = tokens[0];
					currProcess.id = parseInt(tokens[1]);
					currProcess.cpu = parseInt(tokens[2]);
					currProcess.memory = parseInt(tokens[3]);
					currProcess.vsz = parseInt(tokens[4]);
					currProcess.rss = parseInt(tokens[5]);
					currProcess.tty = tokens[6];
					currProcess.stat = tokens[7];
					currProcess.start = tokens[8];
					currProcess.time = tokens[9];
					var command = "";
					for(var j=10; j<tokens.length; j++) {
						command += tokens[j] + " ";
					}
					currProcess.command = command.trim();
					this.processes.push(currProcess);
				}
			}
			this.future.result = {processes:this.processes};
		} else {
			this.future.result = {errorCode:"ERROR", errorText:response.stderr};
		}
	}.bind(this));
};

function KillProcessAssistant() {}; //killProcess
KillProcessAssistant.prototype.run = function(future) {
	this.future = future;
	if(this.controller.args.name) {
		this.name = this.controller.args.name;
		this.cmd = new CommandLine("/usr/bin/killall -9 " + this.name, this.future);
		this.cmd.run();
	} else if(this.controller.args.id) {
		this.id = this.controller.args.id;
		this.cmd = new CommandLine("/bin/kill -9 " + this.id, this.future);
		this.cmd.run();
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function CpuLoadAssistant() {}; //cpuLoad
CpuLoadAssistant.prototype.run = function(future) {
	this.future = future;
	this.cmd = new CommandLine("/bin/cat /proc/loadavg");
	this.cmd.run(function(response) {
		if(response.code==0) {
			var tokens = response.stdout.trim().split(" ");
			this.future.result = {
				onemin: parseFloat(tokens[0]),
				fivemin: parseFloat(tokens[1]),
				tenmin: parseFloat(tokens[2]),
				fivemin: parseFloat(tokens[3]),
				runningprocesses: tokens[4],
				lastprocess: parseInt(tokens[5])
			};
		} else {
			this.future.result = {errorCode:"ERROR", errorText:response.stderr};
		}
	}.bind(this));
};

function CpuTemperatureAssistant() {}; //cpuTemperature
CpuTemperatureAssistant.prototype.run = function(future) {
	this.future = future;
	this.file = "/sys/devices/platform/omap34xx_temp/temp1_input";
	this.pathLib = require("path");
	if(this.pathLib.existsSync(this.file)) {
		this.cmd = new CommandLine("/bin/cat " + this.file);
		this.cmd.run(function(response) {
			if(response.code==0) {
				this.future.result = {value:parseInt(response.stdout.trim())};
			} else {
				this.future.result = {errorCode:"ERROR", errorText:response.stderr};
			}
		}.bind(this));
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"UberKernel not installed"};
	}
};

function MemInfoAssistant() {}; //memInfo
MemInfoAssistant.prototype.run = function(future) {
	this.future = future;
	this.cmd = new CommandLine("/bin/cat /proc/meminfo");
	this.cmd.run(function(response) {
		if(response.code==0) {
			this.info = {};
			var lines = response.stdout.split("\n");
			for(var i=0; i<lines.length; i++) {
				lines[i] = lines[i].trim();
				if(lines[i].length>0) {
					var tokens = lines[i].split(/\s+/);
					this.info[tokens[0].replace(/:/g, "").toLowerCase()] =
							parseInt(tokens[1]);
				}
			}
			this.future.result = this.info;
		} else {
			this.future.result = {errorCode:"ERROR", errorText:response.stderr};
		}
	}.bind(this));
};

function CpuInfoAssistant() {}; //cpuInfo
CpuInfoAssistant.prototype.run = function(future) {
	this.future = future;
	this.cmd = new CommandLine("/bin/cat /proc/cpuinfo");
	this.cmd.run(function(response) {
		if(response.code==0) {
			this.info = {};
			var lines = response.stdout.split("\n");
			for(var i=0; i<lines.length; i++) {
				lines[i] = lines[i].trim();
				if(lines[i].length>0) {
					var first = lines[i].substring(0, lines[i].indexOf(":"));
					first = first.replace(/ /g, "_").replace(/\t/g, "").toLowerCase();
					var second = lines[i].substring(lines[i].indexOf(":")+1).trim();
					this.info[first] = second;
				}
			}
			this.future.result = this.info;
		} else {
			this.future.result = {errorCode:"ERROR", errorText:response.stderr};
		}
	}.bind(this));
};

function SyncTimeAssistant() {}; //syncTime
SyncTimeAssistant.prototype.run = function(future) {
	this.future = future;
	this.server = "pool.ntp.org";
	if(this.controller.args.server) {
		this.server = this.controller.args.server;
	}
	this.cmd = new CommandLine("/usr/bin/ntpdate -u " + this.server, this.future);
	this.cmd.run();
};

function RecanAssistant() {}; //rescan
RecanAssistant.prototype.run = function(future) {
	this.future = future;
	this.ls = new LunaSend("palm://com.palm.applicationManager/rescan", {}, this.future);
	this.ls.run();
};

function JavaRestartAssistant() {}; //javaRestart
JavaRestartAssistant.prototype.run = function(future) {
	this.future = future;
	this.future.result = {errorCode:"ERROR", errorText:"There is no Java on webOS 2.0."};
};

function NovacomRestartAssistant() {}; //novacomRestart
NovacomRestartAssistant.prototype.run = function(future) {
	this.future = future;
	this.cmd = new CommandLine("/usr/bin/killall -HUP novacomd", this.future);
	this.cmd.run();
};

function LunaRestartAssistant() {}; //lunaRestart
LunaRestartAssistant.prototype.run = function(future) {
	this.future = future;
	this.cmd = new CommandLine("/usr/bin/killall -HUP LunaSysMgr", this.future);
	this.cmd.run();
};

function DeviceRestartAssistant() {}; //deviceRestart
DeviceRestartAssistant.prototype.run = function(future) {
	this.future = future;
	this.ls = new LunaSend("palm://com.palm.power/shutdown/machineReboot",
			{reason:"SysToolsMgr service has requested a device reboot"}, this.future);
	this.ls.run();
};

function FlashOnAssistant() {}; //flashOn
FlashOnAssistant.prototype.run = function(future) {
	this.controller.args.state = "on";
	handleTorch(future, this.controller.args);
};

function FlashOffAssistant() {}; //flashOff
FlashOffAssistant.prototype.run = function(future) {
	this.controller.args.state = "off";
	this.controller.args.value = 0;
	handleTorch(future, this.controller.args);
};

function SetFlashAssistant() {}; //setFlash
SetFlashAssistant.prototype.run = function(future) {
	handleTorch(future, this.controller.args);
};

function handleTorch(future, args) {
	this.future = future;
	this.file = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.systoolsmgr/"
			+ "torch.status";
	this.onScript = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.systoolsmgr/"
			+ "scripts/torchOn.sh";
	this.offScript = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.systoolsmgr/"
			+ "scripts/torchOff.sh";
	this.pathLib = require("path");
	this.isPre3 = this.pathLib.existsSync("/sys/devices/i2c-4/4-0033/flash/flash0");
	if(args.state) {
		this.state = args.state.toLowerCase();
		if(args.value!=undefined) {
			this.value = args.value;
		} else {
			if(this.isPre3) {
				this.value = 93;
			} else {
				this.value = 100;
			}
		}
		this.fs = require("fs");
		if(this.value==0) {
			this.state = "off";
		}
		if(this.state=="on") {
			this.fs.writeFile(this.file, JSON.stringify({state:"on", value:this.value*1}),
					"utf8");
			this.cmd = new CommandLine("/bin/sh " + this.onScript + " " + this.value,
					this.future);
			this.cmd.run();
		} else if(this.state=="off") {
			if(this.pathLib.existsSync(this.file)) {
				this.fs.unlink(this.file);
			}
			this.cmd = new CommandLine("/bin/sh " + this.offScript, this.future);
			this.cmd.run();
		} else if(this.state=="strobe") {
			this.future.result = {errorCode:"ERROR", errorText:"Strobe feature not "
					+ "available for webOS 2.0."};
		} else {
			this.future.result = {errorCode:"ERROR", errorText:"Invalid state"};
		}
	} else {
		this.future.result = {errorCode:"ERROR", errorText:"Improperly formatted request."};
	}
};

function FlashStateAssistant() {}; //flashState
FlashStateAssistant.prototype.run = function(future) {
	this.future = future;
	this.file = "/sys/class/i2c-adapter/i2c-2/2-0033/avin";
	this.fileAlt = "/sys/devices/i2c-4/4-0033/flash/flash0/flash_or_torch_start";
	this.file2 = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.systoolsmgr/"
			+ "torch.status";
	this.pathLib = require("path");
	this.isPre3 = this.pathLib.existsSync("/sys/devices/i2c-4/4-0033/flash/flash0");
	if(this.isPre3) {
		this.file = this.fileAlt;
	}
	this.fs = require("fs");
	this.fs.open(this.file, "r", 0666, function(err, fd){
		if(!err) {
			try {
				var text = this.fs.readSync(fd, 1, 0, "utf8")[0];
				this.fs.closeSync(fd);
			} catch(e) {
				var text = "0";
			}
			this.state = "off";
			this.value = 0;
			if(text=="1") {
				this.state = "on";
				if(this.isPre3) {
					this.value = 93;
				} else {
					this.value = 100;
				}
			}
			;
			if(this.state=="on" && this.pathLib.existsSync(this.file2)) {
				this.cmd = new CommandLine("/bin/cat " + this.file2);
				this.cmd.run(function(response) {
					if(response.code==0) {
						var status = JSON.parse(response.stdout);
						if(status.state!=undefined && status.state==this.state &&
								status.value!=undefined) {
							this.value = status.value;
						}
					}
					this.future.result = {state:this.state, value:this.value};
				}.bind(this));
			} else {
				this.future.result = {state:this.state, value:this.value};
			}
		} else {
			this.future.result = {state:"off", value:0};
		}
	}.bind(this));
};

function HasFlashAssistant() {}; //hasFlash
HasFlashAssistant.prototype.run = function(future) {
	var flashDir = "/sys/class/i2c-adapter/i2c-2/2-0033";
	var flashDirAlt = "/sys/devices/i2c-4/4-0033/flash/flash0";
	var pathLib = require("path");
	future.result = {has:(pathLib.existsSync(flashDir) || pathLib.existsSync(flashDirAlt))};
};



function ATestAssistant() {};
ATestAssistant.prototype.run = function(future) {
	var fs = require("fs");
	var children = fs.readdirSync(this.controller.args.path);
	var item = false;
	for(var i=0; i<children.length; i++) {
		var childStat = fs.lstatSync(this.controller.args.path + "/" + children[i]);
		if(childStat.isDirectory() && !childStat.isSymbolicLink()) {
			item = true;
			break;
		}
	}
	future.result = {children:children, item:item};
};

function BTestAssistant() {};
BTestAssistant.prototype.run = function(future) {
	this.cmd = new CommandLine(this.controller.args.exec, future);
	this.cmd.run();
};
