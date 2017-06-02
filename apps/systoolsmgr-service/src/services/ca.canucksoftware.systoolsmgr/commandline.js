function CommandLine(cmd, future) {
	this.command = cmd.trim();
	this.future = future;
	this.options = {
		encoding: "utf8",
		timeout: 0,
		maxBuffer: 200*1024,
		killSignal: "SIGTERM",
		cwd: null,
		env: null
	};
	this.rescan = false;
};

CommandLine.prototype.setEnv = function(value) {
	this.options.env = value;
};

CommandLine.prototype.rescanFileindexer = function(value) {
	this.rescan = value;
};

CommandLine.prototype._rescanFileindexer = function(value) {
	var script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr/"
			+ "scripts/fileindexer.sh";
	var cmd = new CommandLine("/bin/sh " + script);
	cmd.run();
};

CommandLine.prototype.run = function(callback) {
	this.callback = callback;
	var exec = require("child_process").exec;
	var process = exec(this.command, this.options, this._handleExec.bind(this));
};

CommandLine.prototype._handleExec = function(error, stdout, stderr) {
	this.stdout = stdout;
	this.stderr = stderr;
	if(this.stderr.startsWith("/bin/sh: ")) {
		this.stderr.replace("/bin/sh: ", "");
	}
	this.code = 0;
	if(error) {
		this.code = error.code || -1;
	}
	if(this.callback) {
		this.callback({stdout:this.stdout, stderr:this.stderr, code:this.code});
	} else if(this.future) {
		if(this.code == 0) { //success
			this._parseCmd();
			this.future.result = {output:stdout, commands:this.cmdArray};
		} else { //failure
			this._parseCmd();
			this.future.result = {errorText:stderr+stdout, errorCode:"ERROR"+this.code,
					returnValue:false, commands:this.cmdArray};
		}
	}
	if(this.rescan) {
		this._rescanFileindexer();
	}
};

CommandLine.prototype._parseCmd = function() {
	this.cmdArray = [];
	var cmd = this.command;
	while(cmd.length>0) {
		if(cmd.charAt(0)=="\"") {
			cmd = cmd.substring(1);
			var end = cmd.indexOf("\"");
			this.cmdArray.push(cmd.substring(0, end));
			cmd = cmd.substring(end+1);
		} else if(cmd.charAt(0)=="'") {
			cmd = cmd.substring(1);
			var end = cmd.indexOf("'");
			this.cmdArray.push(cmd.substring(0, end));
			cmd = cmd.substring(end+1);
		} else{
			var end = cmd.indexOf(" ");
			if(end<=0) {
				this.cmdArray.push(cmd);
				cmd = "";
			} else {
				this.cmdArray.push(cmd.substring(0, end));
				cmd = cmd.substring(end+1);
			}
			
		}
		cmd = cmd.trim();
	}
};

