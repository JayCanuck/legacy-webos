function LunaSend(address, params, future) {
	this.address = address;
	this.params = params;
	this.future = future;
	this.publicBus = false;
	this.appid = "";
};

LunaSend.prototype.setPublic = function(isPublic) {
	this.publicBus = isPublic;
};

LunaSend.prototype.setAppId = function(appid) {
	this.appid = appid;
};

LunaSend.prototype.run = function(callback) {
	this.callback = callback;
	var exec = "/usr/bin/luna-send ";
	if(this.publicBus) {
		exec += "-P ";
	}
	if(this.appid != "") {
		exec += "-a " + this.appid + " ";
	}
	exec += "-n 1 " + this.address + " '" + JSON.stringify(this.params) + "'";
	this.cmd = new CommandLine(exec);
	this.cmd.run(function(response) {
		var jsonString = response.stdout.substring(response.stdout.indexOf("{"),
				response.stdout.lastIndexOf("}")+1);
		var jsonResult = JSON.parse(jsonString);
		if(this.callback) {
			this.callback(jsonResult);
		} else if(this.future) {
			this.future.result = jsonResult;
		}
	}.bind(this));
};
