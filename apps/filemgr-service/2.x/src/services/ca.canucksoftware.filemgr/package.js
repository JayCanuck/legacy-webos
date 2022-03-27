function Package(future) {
	this.future = future;
};

Package.prototype.getInfo = function(file) {
	this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr"
			+ "/scripts/info.sh";
	this.file = file;
	this.cmd = new CommandLine("/bin/sh " + this.script + " \"" + this.file + "\"",
			this.future);
	this.cmd.run();
};

Package.prototype.install = function(file, appid) {
	this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr"
			+ "/scripts/install.sh";
	this.file = file;
	this.appid = appid;
	this.cmd = new CommandLine("/bin/sh " + this.script + " \"" + this.file + "\" \""
			+ this.appid + "\"", this.future);
	this.cmd.run();
};

Package.prototype.uninstall = function(appid) {
	this.script = "/media/cryptofs/apps/usr/palm/services/ca.canucksoftware.filemgr"
		+ "/scripts/uninstall.sh";
	this.appid = appid;
	this.cmd = new CommandLine("/bin/sh " + this.script + " \"" + this.appid + "\"",
			this.future);
	this.cmd.run();
};

Package.isInstalled = function(appid, pathLib) {
	var path = pathLib || require("path");
	return path.existsSync("/media/cryptofs/apps/usr/lib/ipkg/info/" + appid + ".control");
};
