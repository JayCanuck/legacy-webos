function ProgressiveCmd(exec, args, future, subscription) {
	this.executable = exec.trim();
	this.args = args;
	this.future = future;
	this.subscription = subscription;
	this.options = {
		cwd: null,
		env: null,
		customFds: [-1, -1, -1]
	};
};

ProgressiveCmd.prototype.setEnv = function(value) {
	this.options.env = value;
};

ProgressiveCmd.prototype.setCwd = function(value) {
	this.options.cwd = value;
};

ProgressiveCmd.prototype.kill = function() {
	if(this.process) {
		try {
			this.process.kill();
		} catch(e) {}
	}
};

ProgressiveCmd.prototype.run = function(callback) {
	var spawn = require("child_process").spawn;
	this.process = spawn(this.executable, this.args, this.options);
	var future = this.future;
	var subscription = this.subscription;
	var args = this.args;
	args.unshift(this.executable);
	var stdout = "";
	var stderr = "";
	
	if(future && subscription) {
		future.result = {output:data, commands:args, complete:false};
		future = undefined;
	}
	
	this.process.stdout.setEncoding("utf8");
	this.process.stdout.on("data", function(data) {
		if(callback) {
			callback({stdout:data, complete:false});
		} else if(subscription) {
			var f = subscription.get();
			f.result = {output:data, commands:args, complete:false};
		}
		stdout += data;
	});
	
	this.process.stderr.setEncoding("utf8");
	this.process.stderr.on("data", function(data) {
		stderr += data;
	});
	
	this.process.on("exit", function(code) {
		if(callback) {
			callback({stdout:stdout, stderr:stderr, code:code, complete:true});
		} else if(future || subscription) {
			if(!future && subscription) {
				future = subscription.get();
			}
			if(code==0) {
				future.result = {output:stdout, commands:args, complete:true};
			} else {
				future.result = {errorCode:"ERROR", errorText:stdout + stderr, commands:args,
						complete:true, returnValue:false};
			}
		}
	});
};
